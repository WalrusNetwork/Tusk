package network.walrus.games.uhc.scenarios.type;

import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/**
 * Makes golden heads auto-drop with death drops instead of needing crafting.
 *
 * @author Rafi Baum
 */
public class GoldenRetrieverScenario extends Scenario {

  @Override
  public String name() {
    return "GoldenRetriever";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_GOLDEN_RETRIEVER;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.GOLDEN_APPLE);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return ScenarioAuthorInfo.UNKNOWN;
  }
}
