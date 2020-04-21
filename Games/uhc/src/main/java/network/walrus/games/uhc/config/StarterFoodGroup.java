package network.walrus.games.uhc.config;

import network.walrus.games.uhc.UHCMessages;
import network.walrus.ubiquitous.bukkit.config.ConfigEntry;
import network.walrus.ubiquitous.bukkit.config.ConfigGroup;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Config;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Specialised config group for starter food since the appearance of the group matches the value.
 *
 * @author Rafi Baum
 */
public class StarterFoodGroup extends ConfigGroup {

  private final ConfigEntry<Pair<Material, Integer>> entry;

  /** @param entry representing the starter food config entry */
  public StarterFoodGroup(ConfigEntry<Pair<Material, Integer>> entry) {
    super(UHCMessages.CONFIG_STARTER_FOOD, Material.COOKED_BEEF, entry);
    this.entry = entry;
  }

  @Override
  public ItemStack generateItem(CommandSender sender) {
    ItemStack item = new ItemStack(entry.get().getKey(), entry.get().getValue());

    ItemMeta meta = item.getItemMeta();
    StringBuilder name = new StringBuilder();

    meta.setDisplayName(
        entryPairFormat
            .with(UHCMessages.CONFIG_STARTER_FOOD.with(Config.GROUP), entry.getFormatted())
            .render(sender)
            .toLegacyText());
    meta.addItemFlags(
        ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(meta);

    return item;
  }
}
