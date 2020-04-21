package network.walrus.games.uhc.scenarios.type;

import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;
import java.time.Duration;
import java.util.Set;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.Scenarios;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scenarios.WeakestLink;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.material.MaterialData;

/**
 * Scenario which kills the person with the lowest health every 10 minutes.
 *
 * @author Austin Mayes
 */
public class WeakestLinkScenario extends Scenario {

  private final Duration KILL_DURATION = Duration.ofMinutes(10);

  private final GameTask killTask =
      GameTask.of(
          "Weakest Link",
          () -> {
            UHCRound round = UHCManager.instance.getUHC();
            double lowestHealth = 20;
            Set<Player> withLowst = Sets.newHashSet();
            for (Player player : round.playingPlayers()) {
              if (player.getHealth() < lowestHealth) {
                lowestHealth = player.getHealth();
                withLowst.clear();
              }
              if (DoubleMath.fuzzyEquals(lowestHealth, player.getHealth(), 0.0005)) {
                withLowst.add(player);
              }
            }

            if (withLowst.isEmpty() || withLowst.size() > 1) {
              round
                  .getContainer()
                  .broadcast(
                      UHCMessages.prefix(UHCMessages.WEAKEST_LINK_NONE.with(WeakestLink.NONE)));
              round.getContainer().broadcast(Scenarios.WeakestLink.NONE);
            } else {
              Player toEliminate = withLowst.iterator().next();
              toEliminate.setHealth(0.0);
              round
                  .getContainer()
                  .broadcast(
                      UHCMessages.prefix(
                          UHCMessages.WEAKEST_LINK_OTHER.with(
                              WeakestLink.OTHER, new PersonalizedBukkitPlayer(toEliminate))));
              round
                  .getFacetRequired(GroupsManager.class)
                  .playScopedSound(
                      toEliminate,
                      Scenarios.WeakestLink.SELF,
                      Scenarios.WeakestLink.OTHER,
                      Scenarios.WeakestLink.OTHER,
                      Scenarios.WeakestLink.OTHER);
              toEliminate.sendMessage(UHCMessages.WEAKEST_LINK_SELF.with(WeakestLink.SELF));
            }
          });

  @Override
  public String name() {
    return "WeakestLink";
  }

  @Override
  public void activated(boolean midRound) {
    if (midRound) {
      killTask.repeat((int) KILL_DURATION.getSeconds() * 20, (int) KILL_DURATION.getSeconds() * 20);
    }
  }

  @Override
  public void deActivated(boolean midRound) {
    if (midRound) {
      killTask.reset();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onStateChange(RoundStateChangeEvent event) {
    if (event.isChangeToNotPlaying()) {
      killTask.cancel();
    }

    if (event.isChangeToPlaying()) {
      killTask.repeat((int) KILL_DURATION.getSeconds() * 20, (int) KILL_DURATION.getSeconds() * 20);
    }
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_WEAKEST_LINK;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.REDSTONE);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return ScenarioAuthorInfo.UNKNOWN;
  }
}
