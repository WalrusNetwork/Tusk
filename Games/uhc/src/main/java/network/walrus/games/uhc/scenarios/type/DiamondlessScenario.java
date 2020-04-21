package network.walrus.games.uhc.scenarios.type;

import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.material.MaterialData;

/**
 * Scenario which disables the mining of diamonds.
 *
 * @author Austin Mayes
 */
public class DiamondlessScenario extends Scenario {

  @Override
  public String name() {
    return "Diamondless";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_DIAMONDLESS;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.DIAMOND);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo("/u/clumping", "https://www.reddit.com/u/clumping");
  }

  @EventHandler
  public void onItemSpawn(ItemSpawnEvent event) {
    if (event.getEntity().getItemStack().getType() == Material.DIAMOND) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onItemMove(InventoryMoveItemEvent event) {
    if (event.getItem().getType() == Material.DIAMOND) {
      event.setCancelled(true);
    }
  }
}
