package network.walrus.games.uhc.scenarios.type;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.events.player.PlayerSpawnCompleteEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Scenario which turns give players gear to enchant on start.
 *
 * @author Austin Mayes
 */
public class InfiniteEnchanterScenario extends Scenario {

  private final Set<UUID> handled = Sets.newHashSet();

  @Override
  public String name() {
    return "Infinite Enchanter";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_INFINITE_ENCHANTER;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.ENCHANTMENT_TABLE);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo("/u/XeR0x4", "https://www.reddit.com/u/XeR0x4");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onSpawnComplete(PlayerSpawnCompleteEvent event) {
    if (event.getGroup() instanceof Competitor
        && (UHCManager.instance.getUHC().getState().starting()
            || UHCManager.instance.getUHC().getState().playing())) {
      if (handled.contains(event.getPlayer().getUniqueId())) {
        return;
      }

      handled.add(event.getPlayer().getUniqueId());
      event
          .getPlayer()
          .getInventory()
          .addItem(
              new ItemStack(Material.BOOKSHELF, 64),
              new ItemStack(Material.BOOKSHELF, 64),
              new ItemStack(Material.ANVIL, 64),
              new ItemStack(Material.ENCHANTMENT_TABLE, 64),
              new ItemStack(Material.LAPIS_BLOCK, 64));
      event.getPlayer().setLevel(5000);
    }
  }
}
