package network.walrus.games.octc.destroyables.objectives.monuments;

import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import network.walrus.games.octc.destroyables.objectives.DestroyableProperties;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableEventInfo;
import network.walrus.games.octc.destroyables.objectives.monuments.events.MonumentDestroyEvent;
import network.walrus.utils.bukkit.listener.EventUtil;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.DTM.Destroy;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.DTM;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.Localizable;

/**
 * A block (or collection of blocks) in the world that must be broken by players in order to win.
 *
 * @author Austin Mayes
 */
public class MonumentObjective extends DestroyableObjective {

  /**
   * @param round that the objective is in
   * @param properties describing the objective's attributes
   */
  public MonumentObjective(GameRound round, DestroyableProperties properties) {
    super(round, properties);
  }

  @Override
  public boolean isIncremental() {
    return this.getOriginals().size() > 1;
  }

  @Override
  public boolean isCompleted() {
    if (getProperties().owner.isPresent()) {
      return getCompletion() >= this.getProperties().neededCompletion;
    } else if (getCompletion() >= this.getProperties().neededCompletion) {
      return true;
    } else if (getHighestCompleter().isPresent()) {
      return getCompletion(getHighestCompleter().get()) >= this.getProperties().neededCompletion;
    } else {
      return false;
    }
  }

  @Override
  public LocalizedFormat getTouchMessage() {
    return OCNMessages.MONUMENT_TOUCHED;
  }

  @Override
  public void onComplete(DestroyableEventInfo info) {
    MonumentDestroyEvent event = new MonumentDestroyEvent(this, info);
    Localizable monumentName =
        getName()
            .toText(
                getProperties()
                    .owner
                    .map(t -> t.getColor().getChatColor())
                    .orElse(ChatColor.WHITE));
    getRound()
        .getContainer()
        .broadcast(
            OCNMessages.MONUMENT_BROKEN.with(
                DTM.MONUMENT_BROKEN, monumentName, new PersonalizedBukkitPlayer(info.getActor())));

    this.getRound()
        .getFacetRequired(GroupsManager.class)
        .playScopedSound(
            info.getActor(), Destroy.SELF, Destroy.TEAM, Destroy.ENEMY, Destroy.SPECTATOR);

    EventUtil.call(event);
  }
}
