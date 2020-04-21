package network.walrus.games.octc.hills.koth.overtime;

import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;

/**
 * A null objective that neither can ever own, used to make the wincalculator class work with the
 * koth overtime scenario (forces the wincalculator to only use end scenarios)
 *
 * @author Matthew Arnold
 */
public class NullObjective implements Objective {

  @Override
  public void initialize() {}

  @Override
  public LocalizedConfigurationProperty getName() {
    return null;
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return true;
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return false;
  }

  @Override
  public double getCompletion(Competitor competitor) {
    return 0;
  }

  @Override
  public boolean isIncremental() {
    return false;
  }
}
