package network.walrus.games.uhc.facets.revive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.facets.combatlog.CombatLogTracker;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.tag.PlayerState;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Facet which allows players to be revived after their deaths by tracking their state and group.
 *
 * @author Rafi
 */
public class ReviveFacet extends Facet implements Listener {

  private final UHCRound round;
  private final Map<UUID, RevivedPlayerState> states;

  public ReviveFacet(FacetHolder holder) {
    this.round = (UHCRound) holder;
    states = Maps.newHashMap();
  }

  /**
   * Revives a player and restores them to the state they had prior to death and then teleports them
   * to the specified location.
   *
   * @param player to revive
   * @param location to teleport player
   * @return if the player was revived successfully
   */
  public boolean revivePlayer(Player player, Optional<Location> location) {
    if (!states.containsKey(player.getUniqueId())) {
      return false;
    }

    RevivedPlayerState state = states.get(player.getUniqueId());

    round.getFacetRequired(GroupsManager.class).changeGroup(player, state.getGroup(), false, false);
    state.getState().apply(player, location);
    return true;
  }

  /**
   * Revives a player and restores them to the state they had prior to death.
   *
   * @param player to revive
   * @return if the player was revived successfully
   */
  public boolean revivePlayer(Player player) {
    return revivePlayer(player, Optional.empty());
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    RevivedPlayerState state =
        new RevivedPlayerState(
            new PlayerState(event.getPlayer()),
            round.getFacetRequired(GroupsManager.class).getGroup(event.getPlayer()));

    states.put(event.getPlayer().getUniqueId(), state);
  }

  @EventHandler
  public void onTaggedPlayerDeath(TaggedPlayerDeathEvent event) {
    Optional<Group> group =
        round.getFacetRequired(CombatLogTracker.class).getGroup(event.getPlayer().getUniqueId());
    if (!group.isPresent()) {
      return;
    }

    RevivedPlayerState state =
        new RevivedPlayerState(new PlayerState(event.getPlayer()), group.get());

    states.put(event.getPlayer().getUniqueId(), state);
  }
}
