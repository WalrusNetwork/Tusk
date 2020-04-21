package network.walrus.games.uhc.facets.groups;

import java.util.Collection;
import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.CompetitorRule;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.ffa.FFATeam;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Manager for groups inside of singles UHCs
 *
 * @author Austin Mayes
 */
public class SinglesManager extends UHCGroupsManager implements Listener {

  private final FFATeam team;

  /** @param holder which this manager is operating inside of */
  public SinglesManager(FacetHolder holder) {
    super(holder);
    this.team =
        new FFATeam(
            holder,
            null,
            0,
            UHCManager.instance.getConfig().teamSize.get(),
            (int) Math.ceil(UHCManager.instance.getConfig().teamSize.get() * 1.5),
            false,
            true);
    getGroups().add(this.team);
  }

  @Override
  public void addPlayer(Player player) {
    changeGroup(player, team, false, false);
  }

  @Override
  public CompetitorRule getCompetitorRule() {
    return CompetitorRule.INDIVIDUAL;
  }

  @Override
  public Collection<? extends Competitor> getCompetitors() {
    return this.team.getMembers();
  }

  @Override
  public Collection<? extends Competitor> getCompetitors(Group group) {
    return group.getMembers();
  }

  /** Assign players to a group on join. */
  @EventHandler(priority = EventPriority.NORMAL)
  public void onJoin(PlayerJoinEvent event) {
    if (((UHCRound) getHolder()).getState().started()) {
      changeGroup(event.getPlayer(), Optional.empty(), getSpectators(), false, false);
    } else {
      changeGroup(event.getPlayer(), Optional.empty(), team, false, false);
    }
  }
}
