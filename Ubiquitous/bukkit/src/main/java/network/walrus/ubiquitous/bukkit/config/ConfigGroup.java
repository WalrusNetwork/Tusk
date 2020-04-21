package network.walrus.ubiquitous.bukkit.config;

import com.google.common.collect.Lists;
import java.util.List;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Config;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedFormat;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Organisational class which groups several related config entries together to be represented by a
 * single item in the config UI.
 *
 * @author Rafi Baum
 */
public class ConfigGroup {

  protected static final UnlocalizedFormat entryPairFormat = new UnlocalizedFormat("{0}: {1}");

  private final LocalizedFormat name;
  private final ItemStack item;
  private final List<ConfigEntry<?>> entries;

  /**
   * @param name of the group
   * @param type to represent the group
   * @param entries belonging to the group
   */
  public ConfigGroup(LocalizedFormat name, Material type, ConfigEntry<?>... entries) {
    this(name, new ItemStack(type), entries);
  }

  /**
   * @param name of the group
   * @param item to represent the group
   * @param entries belonging to the group
   */
  public ConfigGroup(LocalizedFormat name, ItemStack item, ConfigEntry<?>... entries) {
    this.name = name;
    this.item = item;
    this.entries = Lists.newArrayList(entries);
  }

  /**
   * Generates the item that should be used to represent the group
   *
   * @param sender which is viewing this item
   * @return item which contains the group's config entries
   */
  public ItemStack generateItem(CommandSender sender) {
    ItemStack item = this.item.clone();

    ItemMeta meta = item.getItemMeta();
    List<String> lore = Lists.newArrayList();

    for (ConfigEntry<?> entry : this.entries) {
      lore.add(
          entryPairFormat
              .with(entry.getName().with(Config.OPTION), entry.getFormatted())
              .render(sender)
              .toLegacyText());
    }

    meta.setLore(lore);
    meta.setDisplayName(name.with(Config.GROUP).render(sender).toLegacyText());
    meta.addItemFlags(
        ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(meta);

    return item;
  }
}
