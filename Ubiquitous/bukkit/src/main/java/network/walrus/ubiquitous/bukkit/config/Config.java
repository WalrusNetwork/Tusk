package network.walrus.ubiquitous.bukkit.config;

import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * Config class which is used to represent settings that can be changed during runtime.
 *
 * @author Rafi Baum
 */
public class Config implements Listener {

  private final List<ConfigEntry<?>> entries;
  private final List<ConfigGroup> groups;
  private final Set<Inventory> inventories;
  private final Localizable title;
  private final Set<Integer> spacers;

  /**
   * @param entries of the config
   * @param groups of the config
   * @param title of the config
   */
  public Config(List<ConfigEntry<?>> entries, List<ConfigGroup> groups, Localizable title) {
    this.entries = entries;
    this.groups = groups;
    this.title = title;
    this.inventories = Sets.newHashSet();
    this.spacers = Sets.newTreeSet();
    Bukkit.getPluginManager().registerEvents(this, UbiquitousBukkitPlugin.getInstance());
  }

  /**
   * Add a spacer at the specified position in the config inventory
   *
   * @param row to add a spacer at
   * @param col to add a spacer at
   */
  public void addSpacer(int row, int col) {
    addSpacer(row * 9 + col);
  }

  /**
   * Add a spacer at the specified position in the config inventory
   *
   * @param pos to add the spacer at
   */
  public void addSpacer(int pos) {
    spacers.add(pos);
  }

  /** @return a list of components explaining all values of this config */
  public List<Localizable> print() {
    List<Localizable> res = Lists.newArrayList();
    UnlocalizedFormat entryFormat = new UnlocalizedFormat("{0}: {1}");

    for (ConfigEntry<?> entry : entries) {
      res.add(
          entryFormat.with(
              entry.getName().with(NetworkColorConstants.Config.OPTION), entry.getFormatted()));
    }

    return res;
  }

  /**
   * Shows a player the config UI
   *
   * @param player to show the config UI
   */
  public void showConfigUI(Player player) {
    String title = this.title.render(player).toLegacyText();
    if (title.length() > 32) {
      title = title.substring(0, 32);
    }

    Inventory inventory = Bukkit.createInventory(null, (groups.size() + 8) / 9 * 9, title);

    Iterator<ConfigGroup> groupIterator = this.groups.iterator();

    for (int row = 0; row < groups.size() / 9 + spacers.size() + 1; row++) {
      int start = Math.max(0, 4 - (groups.size() - row * 9) / 2);
      for (int col = start; col < 9 && groupIterator.hasNext(); col++) {
        if (spacers.contains(row * 9 + col)) continue;
        inventory.setItem(row * 9 + col, groupIterator.next().generateItem(player));
      }
    }

    inventories.add(inventory);
    player.openInventory(inventory);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (inventories.contains(event.getClickedInventory())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    inventories.remove(event.getInventory());
  }
}
