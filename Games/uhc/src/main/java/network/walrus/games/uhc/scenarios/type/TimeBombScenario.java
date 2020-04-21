package network.walrus.games.uhc.scenarios.type;

import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Scenario which causes chests to blow up on player death after a delay.
 *
 * @author Austin Mayes
 */
public class TimeBombScenario extends Scenario {

  /** Spawn the chest */
  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    spawnChest(
        event.getPlayer().getInventory().getContents(),
        event.getPlayer().getInventory().getArmorContents(),
        event.getLocation());
    event.getDrops().clear();
  }

  /** Spawn the chest */
  @EventHandler
  public void onDeath(TaggedPlayerDeathEvent event) {
    spawnChest(
        event.getPlayer().getInventoryContents(),
        event.getPlayer().getArmorContents(),
        event.getLocation());
    event.getDrops().clear();
  }

  private void spawnChest(ItemStack[] items, ItemStack[] armor, Location location) {
    location.add(1, 0, 1);
    location.getBlock().setType(Material.CHEST);
    location.clone().add(1, 0, 0).getBlock().setType(Material.CHEST);
    location.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
    location.clone().add(1, 1, 0).getBlock().setType(Material.AIR);

    DoubleChestInventory chest =
        (DoubleChestInventory) ((Chest) location.getBlock().getState()).getInventory();
    for (ItemStack item : armor) {
      if (item == null) continue;
      chest.addItem(item);
    }

    for (ItemStack item : items) {
      if (item == null) continue;
      chest.addItem(item);
    }

    GameTask.of(
            "Timebomb boom",
            () -> {
              location.getWorld().playSound(location, Sound.AMBIENCE_THUNDER, 3, 1.3f);
              location.getWorld().spigot().strikeLightning(location, true);
              location.getWorld().createExplosion(location, 7f);
            })
        .later(30 * 20);
  }

  @Override
  public String name() {
    return "TimeBomb";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_TIME_BOMB;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.CHEST);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo("/u/Tman1829765", "https://www.reddit.com/user/Tman1829765");
  }
}
