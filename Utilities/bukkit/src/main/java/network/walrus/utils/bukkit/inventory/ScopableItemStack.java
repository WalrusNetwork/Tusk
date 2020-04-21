package network.walrus.utils.bukkit.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import network.walrus.utils.bukkit.color.ColorProvider;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * A layer on top of ItemStack that modifies its ItemMeta based on a provided player.
 *
 * @author Avicus Network
 */
public class ScopableItemStack {

  private final ItemStack itemStack;
  private final Optional<ColorProvider> color;
  private Optional<LocalizedConfigurationProperty> customName;
  private Optional<List<LocalizedConfigurationProperty>> lore;

  /**
   * Constructor which initializes all custom data as empty.
   *
   * @param itemStack base item stack
   */
  public ScopableItemStack(ItemStack itemStack) {
    this.customName = Optional.empty();
    this.lore = Optional.empty();
    this.itemStack = itemStack;
    this.color = Optional.empty();
  }

  /**
   * Create an exact copy of a {@link ScopableItemStack}.
   *
   * @param item to copy
   */
  public ScopableItemStack(ScopableItemStack item) {
    this.customName = item.customName;
    this.lore = item.lore;
    this.itemStack = item.itemStack.clone();
    this.color = item.color;
  }

  /**
   * Constructor.
   *
   * @param itemStack base item stack to apply this data to
   * @param customName custom name of the item
   * @param lore lore of the item
   * @param color provider used to set the {@link org.bukkit.DyeColor} of color-able items.
   */
  public ScopableItemStack(
      ItemStack itemStack,
      Optional<LocalizedConfigurationProperty> customName,
      Optional<List<LocalizedConfigurationProperty>> lore,
      Optional<ColorProvider> color) {
    this.customName = customName;
    this.lore = lore;
    this.itemStack = itemStack;
    this.color = color;
  }

  /**
   * Set the custom name of this stack.
   *
   * @param name of the item
   * @return this instance with the updated information
   */
  public ScopableItemStack name(LocalizedConfigurationProperty name) {
    this.customName = Optional.ofNullable(name);
    return this;
  }

  /**
   * Set the lore for this stack.
   *
   * @param lore lines for the item
   * @return this instance with the updated information
   */
  public ScopableItemStack lore(LocalizedConfigurationProperty... lore) {
    this.lore = Optional.of(Arrays.asList(lore));
    return this;
  }

  public ItemStack getBaseItemStack() {
    return this.itemStack;
  }

  public ItemStack getItemStack() {
    return getItemStack(Optional.empty());
  }

  /** {@link #getItemStack(Optional)} with an always present player. */
  public ItemStack getItemStack(Player player) {
    return getItemStack(Optional.of(player));
  }

  private ItemStack getItemStack(Optional<Player> player) {
    ItemStack item = this.itemStack.clone();
    ItemMeta meta = item.getItemMeta();

    if (player.isPresent()) {
      this.customName.ifPresent(n -> meta.setDisplayName(n.render(player.get())));
      this.lore.ifPresent(
          l -> {
            List<String> list = new ArrayList<>();
            for (LocalizedConfigurationProperty s : l) {
              String render = s.render(player.get());
              list.add(render);
            }
            meta.setLore(list);
          });
    } else {
      this.customName.ifPresent(n -> meta.setDisplayName(n.translateDefault()));
      this.lore.ifPresent(
          l -> {
            List<String> list = new ArrayList<>();
            for (LocalizedConfigurationProperty localizedConfigurationProperty : l) {
              String translateDefault = localizedConfigurationProperty.translateDefault();
              list.add(translateDefault);
            }
            meta.setLore(list);
          });
    }

    boolean colorable =
        (item.getType() == Material.WOOL
            || item.getType() == Material.STAINED_CLAY
            || item.getType() == Material.STAINED_GLASS
            || item.getType() == Material.STAINED_GLASS_PANE
            || item.getType() == Material.CARPET);

    // Primary color
    if (this.color.isPresent()) {
      Color primary = this.color.get().getColor(player);
      if (meta instanceof LeatherArmorMeta) {
        LeatherArmorMeta leather = (LeatherArmorMeta) meta;
        leather.setColor(primary);
      } else if (colorable) {
        item.setDurability(this.color.get().getDyeColor(player).getWoolData());
      }
    }

    item.setItemMeta(meta);
    return item;
  }

  /**
   * Run a more complex equality check given a player.
   *
   * @param player to scope the stacks for
   * @param other to check for equality to this object
   * @return if the two stacks match when scoped to the player
   */
  public boolean equals(Player player, ItemStack other) {
    ScopableItemStack otherScopable =
        new ScopableItemStack(other, Optional.empty(), Optional.empty(), this.color);
    ItemStack otherItem = otherScopable.getItemStack(player);

    ItemStack item = getItemStack(player);

    return otherItem.isSimilar(item);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof ScopableItemStack) {
      ScopableItemStack test = (ScopableItemStack) other;
      return test.getItemStack().isSimilar(getItemStack());
    }
    return false;
  }
}
