package network.walrus.games.uhc.scenarios;

import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;

/**
 * A game type for UHC which adds special game mechanics and features.
 *
 * @author Austin Mayes
 */
public abstract class Scenario implements Listener {

  /**
   * The name of the scenario, used for UI. This is intentionally not localized since all scenario
   * names should be uniform across locales.
   */
  public abstract String name();

  /** Description array of information describing the details of this scenario. */
  public abstract LocalizedFormat[] description();

  /** Icon of the scenario, used in selection and information menus. */
  public abstract MaterialData icon();

  /** Information about the author of this scenario. */
  public abstract ScenarioAuthorInfo authorInfo();

  /**
   * Slug of the scenario. This is used for data storage and equality checks, and is always constant
   * for each scenario type.
   */
  public String slug() {
    return name().toLowerCase().replace(" ", "-");
  }

  /**
   * Called when the scenario is activated.
   *
   * @param midRound if the action happened during the round
   */
  public void activated(boolean midRound) {}

  /**
   * Called when the scenario is deactivated.
   *
   * @param midRound if the action happened during the round
   */
  public void deActivated(boolean midRound) {}

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Scenario && ((Scenario) obj).slug().equals(slug());
  }
}
