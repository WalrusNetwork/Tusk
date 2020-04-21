package network.walrus.games.core.facets.objectives;

/**
 * Represents an objective that exists in one state. A hill for example is global because it can
 * only be at one percentage at a team, no matter what team is trying to complete it. On the other
 * hand, scores can be at any value depending on what team is in question.
 *
 * @author Austin Mayes
 */
public interface GlobalObjective {

  /** Check if the objective is completed. */
  boolean isCompleted();

  /** @return a value between [0,1] to represent the completion of the objective */
  double getCompletion();
}
