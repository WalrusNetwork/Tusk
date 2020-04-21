package network.walrus.games.core.round.states;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.LocalizableFormat;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.translation.Localizable;

/**
 * Wrapper for {@link RoundStartCountdown} which ensures minimum player count and balancing
 * requirements are met before the main start countdown is executed. If any conditions fail to be
 * met at any point during the countdown, the time will be reset and an error will be shown to
 * players.
 *
 * @author Austin Mayes
 */
public class AutoStartingCountdown extends RoundStartCountdown {

  /** Time (in mills) that the last chat broadcast occurred. */
  private long lastAnnounce = 0;

  /**
   * @param duration until the round starts
   * @param round which is starting
   */
  public AutoStartingCountdown(Duration duration, GameRound round) {
    super(duration, round);
  }

  @Override
  public void onStart() {
    this.lastAnnounce = System.currentTimeMillis();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    GroupsManager module = this.round.getFacetRequired(GroupsManager.class);

    if (round.getContainer().players().isEmpty()) {
      this.resetElapsedTime();
      return;
    }

    List<Group> needMorePlayers = new ArrayList<>();
    List<Group> needBalancing = new ArrayList<>();

    // Only start with the required players
    for (Group group : module.getGroups()) {
      if (group.isSpectator()) {
        continue;
      }

      int count = group.getMembers().size();
      int needed = group.getMinPlayers();

      if (count < needed) {
        needMorePlayers.add(group);
      } else if (!module.isGroupBalanced(group, 0)) {
        needBalancing.add(group);
      }
    }

    boolean cancel = (!needMorePlayers.isEmpty() || !needBalancing.isEmpty());

    if (cancel) {
      long now = System.currentTimeMillis();

      if (!needMorePlayers.isEmpty()) {
        String textFormat = "";
        for (int i = 0; i < needMorePlayers.size(); i++) {
          textFormat += "{" + i + "}, ";
        }
        textFormat = textFormat.substring(0, textFormat.length() - 2);
        LocalizableFormat format = new UnlocalizedFormat(textFormat);

        Localizable[] args = new Localizable[needMorePlayers.size()];
        for (int i = 0; i < needMorePlayers.size(); i++) {
          LocalizableFormat groupFormat = new UnlocalizedFormat("{0} {1}");
          Group group = needMorePlayers.get(i);
          int countNeeded = group.getMinPlayers() - group.size();
          Localizable text =
              groupFormat.with(
                  group.getColor().style(),
                  new LocalizedNumber(countNeeded),
                  group.getName().toText());
          text.style().click(new ClickEvent(Action.RUN_COMMAND, "/join " + group.id()));
          args[i] = text;
        }
        this.updateBossBar(
            GamesCoreMessages.GENERIC_AUTOSTART_NEED.with(format.with(args)), (float) 1);
        if (now - this.lastAnnounce > 45000) {
          this.lastAnnounce = now;
          this.round
              .getContainer()
              .broadcast(GamesCoreMessages.GENERIC_AUTOSTART_NEED.with(format.with(args)));
        }
      } else {
        this.updateBossBar(GamesCoreMessages.GENERIC_AUTOSTART_BALANCE.with(), (float) 1);
        if (now - this.lastAnnounce > 45000) {
          this.lastAnnounce = now;
          this.round
              .getContainer()
              .broadcast(
                  GamesCoreMessages.GENERIC_AUTOSTART_BALANCE.with(
                      Games.OCN.Countdowns.NEEDED_BALANCE));
        }
      }

      this.resetElapsedTime();
      return;
    }

    // Let StartingCountdown handle the rest.
    super.onTick(elapsedTime, remainingTime);
  }
}
