package network.walrus.games.core.facets.group;

/**
 * Rule used to determine how members of a group should interact with each other.
 *
 * @author Avicus Network
 */
public enum CompetitorRule {
  /** The group works together to win. */
  TEAM,

  /** Every player tries to win the round for themselves. */
  INDIVIDUAL
}
