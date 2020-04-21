package network.walrus.utils.bukkit.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utilities for working with {@link ItemMeta}.
 *
 * @author Austin Mayes
 */
public class ItemUtils {

  /**
   * Try to get meta for an item, or return {@link Optional#empty()} if it has no meta.
   *
   * @param item to get meta from
   * @return possible meta of the item
   */
  public static Optional<ItemMeta> tryMeta(ItemStack item) {
    return item.hasItemMeta() ? Optional.of(item.getItemMeta()) : Optional.empty();
  }

  /**
   * Update item meta using a mutator.
   *
   * @param item to update meta for
   * @param mutator to apply to the meta
   */
  public static void updateMeta(ItemStack item, Consumer<ItemMeta> mutator) {
    final ItemMeta meta = item.getItemMeta();
    mutator.accept(meta);
    item.setItemMeta(meta);
  }

  /**
   * Update item meta using a mutator only if the item has meta.
   *
   * @param item to update meta for
   * @param mutator to apply to the meta
   */
  public static void updateMetaIfPresent(@Nullable ItemStack item, Consumer<ItemMeta> mutator) {
    if (item != null && item.hasItemMeta()) {
      updateMeta(item, mutator);
    }
  }

  /**
   * Remove custom tags and non-durability damage from an {@link ItemStack}.
   *
   * @param item to normalize
   * @return item with our custom tags removed
   */
  public static ItemStack normalize(ItemStack item) {
    // Ignore non-data durability
    if (item.getType().getMaxDurability() != 0) {
      item.setDurability((short) 0);
    }

    ItemAttributesUtils.clearAttributes(item.getItemMeta());
    return item;
  }

  /**
   * Creates a book using translatable strings.
   *
   * @param viewer of the book
   * @param title of the book
   * @param content of the book
   * @return the written book
   */
  public static ItemStack createBook(Player viewer, Localizable title, Localizable... content) {
    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta bookMeta = (BookMeta) book.getItemMeta();

    bookMeta.setTitle(title.toLegacyText(viewer));
    bookMeta.setAuthor("Walrus Network");

    List<String> strings = new ArrayList<>();
    for (Localizable string : content) {
      String s = string.toLegacyText(viewer);
      strings.add(s);
    }
    bookMeta.setPages(strings);
    book.setItemMeta(bookMeta);
    return book;
  }

  /**
   * Serialize data from a {@link ItemStack} to a {@link JsonObject}.
   *
   * <p>Specification:
   *
   * <ul>
   *   <li>- type: (string) - enum material name of the stack type
   *   <li>- durability: (short) - durability of the stack
   *   <li>- amount: (int) - the stack size
   *   <li>- flags: (array) - array of {@link ItemFlag#name()}
   *   <li>- name: (string) - custom name of the stack
   *   <li>- lore: (json array) - array of the item's lore
   *   <li>- mods: (json object) - object with keys which are item attribute IDs and values which
   *       are arrays of JSON objects which contain:
   *       <ul>
   *         <li>- name: (string) - name of the mod
   *         <li>- amount: (int) -amount of impact that the mod has
   *         <li>- operation: (string) - Item mod {@link Operation#name()}
   *       </ul>
   *   <li>- enchants: (json array) - Array of strings in the format of "{@link
   *       Enchantment#getId()}:enchantment level"
   * </ul>
   *
   * @param stack to serialize
   * @return json object containing data from the stack
   */
  public static JsonObject toJson(ItemStack stack) {
    JsonObject item = new JsonObject();
    item.addProperty("type", stack.getType().name());
    item.addProperty("durability", stack.getDurability());
    item.addProperty("amount", stack.getAmount());
    ItemMeta meta = stack.getItemMeta();
    JsonArray flags = new JsonArray();
    meta.getItemFlags().forEach(f -> flags.add(new JsonPrimitive(f.name())));
    if (flags.size() > 0) item.add("flags", flags);
    if (meta.hasDisplayName()) item.addProperty("name", meta.getDisplayName());
    if (meta.hasLore()) {
      JsonArray lore = new JsonArray();
      meta.getLore().forEach(l -> lore.add(new JsonPrimitive(l)));
      item.add("lore", lore);
    }
    if (meta.hasAttributeModifiers()) {
      JsonObject mods = new JsonObject();
      for (String attributeId : meta.getModifiedAttributes()) {
        JsonArray mod = new JsonArray();
        Collection<AttributeModifier> attributes = meta.getAttributeModifiers(attributeId);
        for (AttributeModifier attribute : attributes) {
          JsonObject modPart = new JsonObject();
          modPart.addProperty("name", attribute.getName());
          modPart.addProperty("amount", attribute.getAmount());
          modPart.addProperty("operation", attribute.getOperation().name());
          mod.add(modPart);
        }
        mods.add(attributeId, mod);
      }
      item.add("mods", mods);
    }
    if (meta.hasEnchants()) {
      JsonArray enchants = new JsonArray();
      meta.getEnchants().forEach((e, l) -> enchants.add(new JsonPrimitive(e.getId() + ":" + l)));
      item.add("enchants", enchants);
    }
    return item;
  }

  /**
   * Convert a {@link JsonObject} to an {@link ItemStack} using the specification defined in {@link
   * #toJson(ItemStack)}.
   *
   * @param object containing the data used to construct the stack
   * @return an item stack parsed from the JSON
   */
  public static ItemStack fromJson(JsonObject object) {
    Material type = Material.valueOf(object.get("type").getAsString());
    short durability = object.get("durability").getAsShort();
    int amount = object.get("amount").getAsInt();
    ItemStack stack = new ItemStack(type, amount, durability);
    ItemMeta meta = stack.getItemMeta();

    if (object.has("flags")) {
      JsonArray flags = object.get("flags").getAsJsonArray();
      flags.forEach(f -> meta.addItemFlags(ItemFlag.valueOf(f.getAsString())));
    }
    if (object.has("name")) meta.setDisplayName(object.get("name").getAsString());
    if (object.has("lore")) {
      List<String> lore = Lists.newArrayList();
      object.get("lore").getAsJsonArray().forEach(l -> lore.add(l.getAsString()));
      meta.setLore(lore);
    }
    if (object.has("mods")) {
      Map<String, List<AttributeModifier>> itemMods = Maps.newHashMap();
      {
        JsonObject mods = object.get("mods").getAsJsonObject();
        mods.entrySet()
            .forEach(
                e -> {
                  String id = e.getKey();
                  JsonArray mod = e.getValue().getAsJsonArray();
                  List<AttributeModifier> modList = Lists.newArrayList();
                  for (JsonElement part : mod) {
                    JsonObject modPart = part.getAsJsonObject();
                    String name = modPart.get("name").getAsString();
                    Operation operation = Operation.valueOf(modPart.get("operation").getAsString());
                    double modAmt = modPart.get("amount").getAsDouble();
                    modList.add(new AttributeModifier(name, modAmt, operation));
                  }
                  itemMods.put(id, modList);
                });
      }
      itemMods.forEach(
          (id, mods) -> {
            for (AttributeModifier mod : mods) {
              meta.addAttributeModifier(id, mod);
            }
          });
    }
    if (object.has("enchants")) {
      object
          .get("enchants")
          .getAsJsonArray()
          .forEach(
              e -> {
                int id = Integer.valueOf(e.getAsString().split(":")[0]);
                int level = Integer.valueOf(e.getAsString().split(":")[1]);
                meta.addEnchant(new EnchantmentWrapper(id), level, true);
              });
    }

    stack.setItemMeta(meta);
    return stack;
  }
}
