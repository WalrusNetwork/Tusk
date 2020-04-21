package network.walrus.games.octc.global.groups.teams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.CompetitorRule;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.octc.global.groups.OCNGroupsManager;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Manager for matches that use {@link Team}s.
 *
 * @author Austin Mayes
 */
public class TeamsManager extends OCNGroupsManager {

  private final List<Team> teams;
  private final List<Group> groups;
  private final CompetitorRule competitorRule;

  /**
   * Constructor.
   *
   * @param holder which this manager is inside of
   * @param teams which can be joined
   * @param competitorRule to be used when determining display
   * @param spectators default spectators team
   */
  public TeamsManager(
      FacetHolder holder, List<Team> teams, CompetitorRule competitorRule, Spectators spectators) {
    super(holder);
    this.teams = teams;
    this.competitorRule = competitorRule;

    this.groups = new ArrayList<>(teams);
    this.groups.add(0, spectators); // add it first
  }

  @Override
  public CompetitorRule getCompetitorRule() {
    return this.competitorRule;
  }

  @Override
  public Collection<Group> getGroups() {
    return this.groups;
  }

  @Override
  public Collection<? extends Competitor> getCompetitors() {
    if (this.competitorRule == CompetitorRule.TEAM) {
      return this.teams;
    } else {
      List<Competitor> players = new ArrayList<>();
      for (Team team : this.teams) {
        players.addAll(team.getMembers());
      }
      return players;
    }
  }

  public Collection<? extends Competitor> getCompetitors(Group group) {
    if (group instanceof Spectators) {
      return Collections.emptyList();
    }

    if (this.competitorRule == CompetitorRule.TEAM) {
      return Collections.singletonList((Team) group);
    } else {
      return group.getMembers();
    }
  }
}
