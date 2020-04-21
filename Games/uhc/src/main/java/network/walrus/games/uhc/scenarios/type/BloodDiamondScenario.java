package network.walrus.games.uhc.scenarios.type;

import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;

/**
 * Scenario which deals 1/2 heart of health for every diamond mined.
 *
 * @author Rafi Baum
 */
public class BloodDiamondScenario extends Scenario {

  @Override
  public String name() {
    return "BloodDiamond";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_BLOOD_DIAMOND;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.DIAMOND);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo(
        "/u/PippiterLP",
        "https://www.reddit.com/r/ultrahardcore/comments/1roijf/gametype_idea_blood_diamonds/");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMine(BlockBreakEvent event) {
    if (event.getBlock().getType() != Material.DIAMOND_ORE) return;

    event.getPlayer().damage(1);
  }
}
