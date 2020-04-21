package network.walrus.games.uhc.scenarios.type;

import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.utils.bukkit.block.BlockFaceUtils;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;

/**
 * Scenario which causes trees to auto break when the bottom is broken.
 *
 * @author Austin Mayes
 */
public class TimberScenario extends Scenario {

  private static final MultiMaterialMatcher BREAKABLE =
      new MultiMaterialMatcher(Material.LOG, Material.LOG_2);

  @Override
  public String name() {
    return "Timber";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_TIMBER;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.IRON_AXE);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return ScenarioAuthorInfo.UNKNOWN;
  }

  /** Timberrrrrrrrr */
  @EventHandler
  public void onBreak(BlockChangeByPlayerEvent event) {
    if (event.getCause() instanceof BlockBreakEvent) {
      if (BREAKABLE.matches(event.getBlock().getState()))
        breakBlocks(event.getNewState(), event.getNewState().getLocation());
    }
  }

  private void breakBlocks(BlockState block, Location original) {
    if (original.distance(block.getLocation()) > 10) {
      return;
    }

    for (BlockFace neighbor : BlockFaceUtils.NEIGHBORS) {
      BlockState relative = BlockFaceUtils.getRelative(block, neighbor);
      if (BREAKABLE.matches(relative)) {
        relative.getBlock().breakNaturally();
        breakBlocks(relative, original);
      }
    }
  }
}
