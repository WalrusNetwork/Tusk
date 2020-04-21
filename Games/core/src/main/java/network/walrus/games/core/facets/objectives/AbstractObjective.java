package network.walrus.games.core.facets.objectives;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;

/**
 * Helpful abstract implementation of {@link Objective}. Non-interface objective classes should
 * ideally extend this to keep functionality streamlined.
 *
 * @author Austin Mayes
 */
public abstract class AbstractObjective implements Objective {

  /** The round this objective is in. */
  protected final GameRound round;
  /** The name of this objective. */
  protected final LocalizedConfigurationProperty name;

  /**
   * @param round the round this objective is in
   * @param name the name of this objective
   */
  protected AbstractObjective(final GameRound round, final LocalizedConfigurationProperty name) {
    this.round = round;
    this.name = name;
  }

  @Override
  public LocalizedConfigurationProperty getName() {
    return this.name;
  }
}
