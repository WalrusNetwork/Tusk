package network.walrus.utils.bukkit.parse;

import network.walrus.utils.bukkit.parse.complex.ItemParser;
import network.walrus.utils.bukkit.parse.simple.BaseComponentParser;
import network.walrus.utils.bukkit.parse.simple.ColorParser;
import network.walrus.utils.bukkit.parse.simple.LocalizedPropertyParser;
import network.walrus.utils.bukkit.parse.simple.MaterialMatcherParsers.Multi;
import network.walrus.utils.bukkit.parse.simple.MaterialMatcherParsers.Single;
import network.walrus.utils.bukkit.parse.simple.VectorParser;
import network.walrus.utils.core.parse.CoreParserRegistry;
import network.walrus.utils.core.translation.GlobalLocalizations;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionType;

/**
 * Bukkit-specific registry of all simple and complex parsers which can be used during configuration
 * parsing.
 *
 * @author Austin Mayes
 */
public class BukkitParserRegistry extends CoreParserRegistry {

  private static final BaseComponentParser BASE_COMPONENT_PARSER = new BaseComponentParser();
  private static final ColorParser COLOR_PARSER = new ColorParser();
  private static final LocalizedPropertyParser LOCALIZED_PROPERTY_PARSER =
      new LocalizedPropertyParser(GlobalLocalizations.INSTANCE.getBundle());
  private static final Single SINGLE_MATERIAL_MATCHER_PARSER =
      new Single(listParser(), ofEnum(Material.class), byteParser());
  private static final Multi MULTI_MATERIAL_MATCHER_PARSER =
      new Multi(listParser(), SINGLE_MATERIAL_MATCHER_PARSER);
  private static final VectorParser VECTOR_PARSER = new VectorParser(doubleParser(), listParser());
  private static final ItemParser ITEM_PARSER =
      new ItemParser(
          durationParser(),
          baseNumberParser(),
          booleanParser(),
          integerParser(),
          listParser(),
          ofEnum(PotionType.class),
          ofEnum(DyeColor.class),
          ofEnum(PatternType.class),
          ofEnum(Material.class),
          ofEnum(ItemFlag.class),
          COLOR_PARSER,
          LOCALIZED_PROPERTY_PARSER);

  /** @return parser used to parse base components */
  public static BaseComponentParser baseComponentParser() {
    return BASE_COMPONENT_PARSER;
  }

  /** @return parser used to parse colors */
  public static ColorParser colorParser() {
    return COLOR_PARSER;
  }

  /** @return parser used to convert and localize nodes */
  public static LocalizedPropertyParser localizedPropertyParser() {
    return LOCALIZED_PROPERTY_PARSER;
  }

  /** @return parser for single material matchers */
  public static Single singleMaterialMatcherParser() {
    return SINGLE_MATERIAL_MATCHER_PARSER;
  }

  /** @return parser for multi material matchers */
  public static Multi multiMaterialMatcherParser() {
    return MULTI_MATERIAL_MATCHER_PARSER;
  }

  /** @return parser for vectors */
  public static VectorParser vectorParser() {
    return VECTOR_PARSER;
  }

  /** @return parser for item stacks */
  public static ItemParser itemParser() {
    return ITEM_PARSER;
  }
}
