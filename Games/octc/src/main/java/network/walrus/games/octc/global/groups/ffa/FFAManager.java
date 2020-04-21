package network.walrus.games.octc.global.groups.ffa;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.CompetitorRule;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.ffa.FFATeam;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.octc.global.groups.OCNGroupsManager;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Sub-class of the groups manager specifically for {@link FFATeam}s.
 *
 * @author Austin Mayes
 */
public class FFAManager extends OCNGroupsManager {

  private final FFATeam team;
  private final List<Group> groups;

  /**
   * Constructor.
   *
   * @param holder which this manager is a part of
   * @param team the main FFA team
   * @param spectators the main spectator team
   */
  public FFAManager(FacetHolder holder, FFATeam team, Spectators spectators) {
    super(holder);
    this.team = team;
    this.groups = Arrays.asList(team, spectators);
  }

  @Override
  public CompetitorRule getCompetitorRule() {
    return CompetitorRule.INDIVIDUAL;
  }

  @Override
  public Collection<? extends Group> getGroups() {
    return this.groups;
  }

  @Override
  public Collection<? extends Competitor> getCompetitors() {
    return this.team.getMembers();
  }

  @Override
  public Collection<? extends Competitor> getCompetitors(Group group) {
    return group.getMembers();
  }
}
