package network.walrus.utils.bukkit.parse.complex;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import network.walrus.utils.bukkit.color.ColorProvider;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import network.walrus.utils.bukkit.item.ItemAttributesUtils;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.ComplexParser;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.simple.ListParser;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * Parses {@link ScopableItemStack}s from {@link Node}s.
 *
 * @author Avicus Network
 */
public class ItemParser implements ComplexParser<ScopableItemStack> {

  private static final Collection<Material> COLORABLE =
      Arrays.asList(
          Material.WOOL,
          Material.STAINED_CLAY,
          Material.STAINED_GLASS,
          Material.STAINED_GLASS_PANE,
          Material.CARPET);
  public static ColorProvider teamColorProvider;
  private final SimpleParser<Duration> durationParser;
  private final SimpleParser<Number> numberParer;
  private final SimpleParser<Boolean> booleanParser;
  private final SimpleParser<Integer> integerParser;
  private final ListParser listParser;
  private final SimpleParser<PotionType> potionTypeParser;
  private final SimpleParser<DyeColor> dyeColorParser;
  private final SimpleParser<PatternType> patternParser;
  private final SimpleParser<Material> materialParser;
  private final SimpleParser<ItemFlag> itemFlagParser;
  private final SimpleParser<Color> colorParser;
  private final SimpleParser<LocalizedConfigurationProperty> localeParser;

  /**
   * Constructor.
   *
   * @param durationParser used to parse durations
   * @param numberParer used to parse numbers of any type
   * @param booleanParser used to parse booleans
   * @param integerParser used to parse integers
   * @param listParser used to parse lists of any type
   * @param potionTypeParser used to parse potion effect types
   * @param dyeColorParser used to parse dye colors
   * @param patternParser used to parse banner patterns
   * @param materialParser used to parse materials
   * @param itemFlagParser used to parse item flags
   * @param colorParser used to parse colors
   * @param localeParser used to parse localized data
   */
  public ItemParser(
      SimpleParser<Duration> durationParser,
      SimpleParser<Number> numberParer,
      SimpleParser<Boolean> booleanParser,
      SimpleParser<Integer> integerParser,
      ListParser listParser,
      SimpleParser<PotionType> potionTypeParser,
      SimpleParser<DyeColor> dyeColorParser,
      SimpleParser<PatternType> patternParser,
      SimpleParser<Material> materialParser,
      SimpleParser<ItemFlag> itemFlagParser,
      SimpleParser<Color> colorParser,
      SimpleParser<LocalizedConfigurationProperty> localeParser) {
    this.durationParser = durationParser;
    this.numberParer = numberParer;
    this.booleanParser = booleanParser;
    this.integerParser = integerParser;
    this.listParser = listParser;
    this.potionTypeParser = potionTypeParser;
    this.dyeColorParser = dyeColorParser;
    this.patternParser = patternParser;
    this.materialParser = materialParser;
    this.itemFlagParser = itemFlagParser;
    this.colorParser = colorParser;
    this.localeParser = localeParser;
  }

  @Override
  public ScopableItemStack parse(Node node) throws ParsingException {
    return parseItemStack(node);
  }

  /**
   * Parse a scopable item stack from the supplied node.
   *
   * @param node to parse data from
   * @return the parsed stack
   */
  public ScopableItemStack parseItemStack(Node node) {
    // material
    Material material = materialParser.parseRequired(node.attribute("material"));
    return parseItemStack(material, node);
  }

  /**
   * Parse potion data from a node and apply it to the supplied item stack.
   *
   * @param base stack to apply data to
   * @param meta of the base item to add effects to
   * @param node to parse the meta from
   * @return potion meta parsed from the node
   */
  public PotionMeta parsePotion(ItemStack base, ItemMeta meta, Node<?> node) {
    if (node.hasChild("effects")) {
      for (Node el : node.childRequired("effects").children("effect")) {
        PotionEffect effect = parsePotionEffect(el);

        if (!(meta instanceof PotionMeta)) {
          throw new ParsingException(el, "Item effects can only be applied to potion items.");
        }

        PotionMeta potion = (PotionMeta) meta;
        potion.addCustomEffect(effect, true);
      }
    } else if (node.hasChild("effect")) {
      throw new ParsingException(node, "Item effects must be wrapped in corresponding sub-tags.");
    }

    if (node.attribute("potion").isValuePresent()) {
      if (!(meta instanceof PotionMeta)) {
        throw new ParsingException(
            node, "Potion type attribute can only be applied to potion items.");
      }

      PotionType type = potionTypeParser.parseRequired(node.attribute("potion"));
      base.setDurability((short) type.getDamageValue());
    }

    if (node.attribute("splash").isValuePresent()) {
      if (!(meta instanceof PotionMeta)) {
        throw new ParsingException(
            node, "Potion splash attribute can only be applied to potion items.");
      }

      Potion potion = Potion.fromItemStack(base);
      boolean splash = booleanParser.parseRequired(node.attribute("splash"));
      potion.setSplash(splash);
      base.setDurability(potion.toDamageValue());
    }
    return (PotionMeta) meta;
  }

  /**
   * Parse a full scopable stack with the desired material from the supplied node.
   *
   * @param material that the stack should be
   * @param node to parse the stack from
   * @return the parsed stack
   */
  public ScopableItemStack parseItemStack(Material material, Node<?> node) {
    // damage
    short damage = numberParer.parse(node.attribute("damage")).orElse(0).shortValue();

    // amount
    int amount = integerParser.parse(node.attribute("amount")).orElse(1);

    ItemStack item = new ItemStack(material, amount, damage);
    ItemMeta meta = item.getItemMeta();

    // name
    Optional<LocalizedConfigurationProperty> name = Optional.empty();
    if (node.hasAttribute("name")) {
      name = localeParser.parse(node.attribute("name"));
    }

    // lore
    Optional<List<LocalizedConfigurationProperty>> lore = Optional.empty();
    Optional<? extends Node<?>> loreChild = node.child("lore");
    if (loreChild.isPresent()) {
      lore = Optional.of(new ArrayList<>());
      for (Node child : loreChild.get().children()) {
        lore.get().add(localeParser.parseRequired(child.text()));
      }
    }

    // unbreakable
    boolean unbreakable = booleanParser.parse(node.attribute("unbreakable")).orElse(false);
    if (item.getType().getMaxDurability() != 0 || unbreakable) {
      meta.spigot().setUnbreakable(true);
    }

    // flags
    Optional<List<StringHolder>> flags = listParser.parseList(node.attribute("flags"), ";", true);
    for (StringHolder value : flags.orElse(Collections.emptyList())) {
      ItemFlag flag = itemFlagParser.parseRequired(value);
      meta.addItemFlags(flag);
    }

    // color
    if (node.attribute("color").isValuePresent()) {
      if (meta instanceof LeatherArmorMeta) {
        Color color = colorParser.parseRequired(node.attribute("color"));
        ((LeatherArmorMeta) meta).setColor(color);
      } else if (meta instanceof BannerMeta) {
        DyeColor color = dyeColorParser.parseRequired(node.attribute("color"));
        ((BannerMeta) meta).setBaseColor(color);
      } else if (COLORABLE.contains(material)) {
        DyeColor dyeColor = dyeColorParser.parseRequired(node.attribute("color"));
        item.setDurability(dyeColor.getWoolData());
      } else {
        throw new ParsingException(
            node, "Color can only be applied to leather armor, banners, or colorable blocks.");
      }
    }

    // team-color
    boolean useTeamColor = booleanParser.parse(node.attribute("team-color")).orElse(false);
    Optional<ColorProvider> teamColor = Optional.empty();
    if (useTeamColor) {
      teamColor = Optional.of(teamColorProvider);
    }

    if (node.hasChild("enchantments")) {
      for (Node el : node.childRequired("enchantments").children("enchantment")) {
        Enchantment enchantment =
            Enchantment.getByName(el.text().asRequiredString().toUpperCase().replace(" ", "_"));

        if (enchantment == null) {
          throw new ParsingException(el, "Unknown enchantment.");
        }

        int level = integerParser.parse(el.attribute("level")).orElse(1);

        meta.addEnchant(enchantment, level, true);
      }
    } else if (node.hasChild("enchantment")) {
      throw new ParsingException(
          node, "Item enchantments and effects now must be wrapped in corresponding sub-tags.");
    }

    if (meta instanceof EnchantmentStorageMeta) {
      for (Node el : node.children("stored-enchantment")) {
        Enchantment enchantment =
            Enchantment.getByName(el.text().asRequiredString().toUpperCase().replace(" ", "_"));

        if (enchantment == null) {
          throw new ParsingException(el, "Unknown enchantment.");
        }

        int level = integerParser.parse(el.attribute("level")).orElse(1);

        ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, level, true);
      }
    }

    if (meta instanceof PotionMeta) {
      meta = parsePotion(item, meta, node);
    }

    // banner pattern
    List<? extends Node<?>> patterns = node.children("pattern");
    Collections.reverse(patterns);
    for (Node el : patterns) {
      DyeColor color = dyeColorParser.parseRequired(el.attribute("color"));
      PatternType type = patternParser.parseRequired(el.text());
      if (meta instanceof BannerMeta) {
        ((BannerMeta) meta).addPattern(new Pattern(color, type));
      } else {
        throw new ParsingException(el, "Patterns can only be applied to banners.");
      }
    }

    boolean locked = booleanParser.parse(node.attribute("locked")).orElse(false);
    if (locked) {
      ItemAttributesUtils.setLock(meta, true);
    }

    boolean unsharable = booleanParser.parse(node.attribute("unsharable")).orElse(false);
    if (unsharable) {
      ItemAttributesUtils.setNotSharable(meta, true);
    }

    boolean haunted = booleanParser.parse(node.attribute("death-drop")).orElse(true);
    if (!haunted) {
      ItemAttributesUtils.setShouldDeathDrop(meta, false);
    }

    item.setItemMeta(meta);

    return new ScopableItemStack(item, name, lore, teamColor);
  }

  /**
   * Parse a potion effect from the contents of a node.
   *
   * @param node to parse an effect from
   * @return the parsed effect
   */
  public PotionEffect parsePotionEffect(Node node) {
    PotionEffectType type =
        PotionEffectType.getByName(node.text().asRequiredString().toUpperCase().replace(' ', '_'));
    if (type == null) {
      throw new ParsingException(node, "Effect not found.");
    }
    int amplifier = integerParser.parse(node.attribute("amplifier")).orElse(1);
    Optional<Duration> duration = durationParser.parse(node.attribute("duration"));

    int ticks;
    if (duration.isPresent()) {
      ticks = (int) duration.get().getSeconds() * 20;
    } else {
      ticks = Integer.MAX_VALUE;
    }

    return new PotionEffect(type, ticks, amplifier - 1);
  }
}
