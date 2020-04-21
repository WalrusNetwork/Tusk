package network.walrus.games.uhc.scenarios.type;

import java.util.Random;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathByPlayerEvent;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * When a player kills a bat, there's a 95% chance of dropping a Golden Apple, and a 5% chance of
 * killing the player.
 *
 * @author Austin Mayes
 */
public class BatsScenario extends Scenario {

  private final Random RANDOM = new Random();

  @Override
  public String name() {
    return "Bats";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_BATS;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.COAL_BLOCK);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo("/u/ElectronicWiz", "https://redd.it/4p23u1");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void entityKill(EntityDeathByPlayerEvent event) {
    if (event.getEntityType() != EntityType.BAT) {
      return;
    }

    if (RANDOM.nextDouble() >= .95) {
      event.getCause().setHealth(0);
    } else {
      event
          .getLocation()
          .getWorld()
          .dropItemNaturally(event.getLocation(), new ItemStack(Material.GOLDEN_APPLE));
    }
  }
}
