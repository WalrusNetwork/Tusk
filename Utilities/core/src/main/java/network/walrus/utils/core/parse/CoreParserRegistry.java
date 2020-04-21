package network.walrus.utils.core.parse;

import com.google.common.collect.Maps;
import java.util.Map;
import network.walrus.utils.core.parse.complex.PreparedNumberActionParser;
import network.walrus.utils.core.parse.simple.BooleanParser;
import network.walrus.utils.core.parse.simple.DurationParser;
import network.walrus.utils.core.parse.simple.EnumParser;
import network.walrus.utils.core.parse.simple.ListParser;
import network.walrus.utils.core.parse.simple.NumberActionParser;
import network.walrus.utils.core.parse.simple.NumberComparatorParser;
import network.walrus.utils.core.parse.simple.NumberFactory;
import network.walrus.utils.core.parse.simple.URLParser;
import network.walrus.utils.core.parse.simple.VersionParser;
import network.walrus.utils.core.parse.simple.number.BaseNumberParser;
import network.walrus.utils.core.parse.simple.number.NumberParser;
import network.walrus.utils.core.parse.simple.number.PercentParser;
import network.walrus.utils.core.parse.simple.number.RangeParser;

/**
 * Global registry of all simple and complex parsers which can be used during configuration parsing.
 *
 * @author Austin Mayes
 */
public class CoreParserRegistry {

  private static final Map<Class<? extends Number>, NumberParser> NUMBER_PARSERS =
      Maps.newHashMap();
  private static final Map<Class<? extends Number>, RangeParser> RANGE_PARSERS = Maps.newHashMap();

  static {
    for (Class<? extends Number> n : NumberFactory.numberTypes()) {
      NumberFactory factory = NumberFactory.get(n);
      NUMBER_PARSERS.put(n, new NumberParser(factory));
      if (n.isAssignableFrom(Comparable.class)) {
        RANGE_PARSERS.put(n, new RangeParser(factory));
      }
    }
  }

  private static final BaseNumberParser BASE_NUMBER_PARSER = new BaseNumberParser();
  private static final BooleanParser BOOLEAN_PARSER = new BooleanParser();
  private static final DurationParser DURATION_PARSER = new DurationParser();
  private static final ListParser LIST_PARSER = new ListParser();
  private static final NumberActionParser NUMBER_ACTION_PARSER = new NumberActionParser();
  private static final NumberComparatorParser NUMBER_COMPARATOR_PARSER =
      new NumberComparatorParser();
  private static final URLParser URL_PARSER = new URLParser();
  private static final VersionParser VERSION_PARSER =
      new VersionParser(integerParser(), LIST_PARSER);
  private static final PreparedNumberActionParser PREPARED_NUMBER_ACTION_PARSER =
      new PreparedNumberActionParser(BASE_NUMBER_PARSER, NUMBER_ACTION_PARSER);
  private static final PercentParser PERCENT_PARSER = new PercentParser();

  /**
   * Create a new enum parser which has the sole responsibility for parsing enums of type {@link T}.
   *
   * @param enumClazz which should be parsed
   * @param <T> type of enum being parsed
   * @return parser which parses {@link T}s
   */
  public static <T extends Enum<T>> EnumParser<T> ofEnum(Class<T> enumClazz) {
    return new EnumParser<>(enumClazz);
  }

  /** @return number parser for byte values */
  public static NumberParser<Byte> byteParser() {
    return NUMBER_PARSERS.get(Byte.class);
  }

  /** @return number parser for short values */
  public static NumberParser<Short> shortParser() {
    return NUMBER_PARSERS.get(Short.class);
  }

  /** @return number parser for integer values */
  public static NumberParser<Integer> integerParser() {
    return NUMBER_PARSERS.get(Integer.class);
  }

  /** @return number parser for long values */
  public static NumberParser<Long> longParser() {
    return NUMBER_PARSERS.get(Long.class);
  }

  /** @return number parser for float values */
  public static NumberParser<Float> floatParser() {
    return NUMBER_PARSERS.get(Float.class);
  }

  /** @return number parser for double values */
  public static NumberParser<Double> doubleParser() {
    return NUMBER_PARSERS.get(Double.class);
  }

  /** @return range parser for ranges of bytes */
  public static RangeParser<Byte> byteRangeParser() {
    return RANGE_PARSERS.get(Byte.class);
  }

  /** @return range parser for ranges of shorts */
  public static RangeParser<Short> shortRangeParser() {
    return RANGE_PARSERS.get(Short.class);
  }

  /** @return range parser for ranges of integers */
  public static RangeParser<Integer> integerRangeParser() {
    return RANGE_PARSERS.get(Integer.class);
  }

  /** @return range parser for ranges of longs */
  public static RangeParser<Long> longRangeParser() {
    return RANGE_PARSERS.get(Long.class);
  }

  /** @return range parser for ranges of floats */
  public static RangeParser<Float> floatRangeParser() {
    return RANGE_PARSERS.get(Float.class);
  }

  /** @return range parser for ranges of doubles */
  public static RangeParser<Double> doubleRangeParser() {
    return RANGE_PARSERS.get(Double.class);
  }

  /** @return parser for values which can be any number type */
  public static BaseNumberParser baseNumberParser() {
    return BASE_NUMBER_PARSER;
  }

  /** @return parser for booleans */
  public static BooleanParser booleanParser() {
    return BOOLEAN_PARSER;
  }

  /** @return parser for durations */
  public static DurationParser durationParser() {
    return DURATION_PARSER;
  }

  /** @return parser for lists of any type */
  public static ListParser listParser() {
    return LIST_PARSER;
  }

  /** @return parser for number actions */
  public static NumberActionParser numberActionParser() {
    return NUMBER_ACTION_PARSER;
  }

  /** @return parser for number comparators */
  public static NumberComparatorParser numberComparatorParser() {
    return NUMBER_COMPARATOR_PARSER;
  }

  /** @return parser for URLs */
  public static URLParser urlParser() {
    return URL_PARSER;
  }

  /** @return parser for semantic versions */
  public static VersionParser versionParser() {
    return VERSION_PARSER;
  }

  /** @return parser for prepared number actions */
  public static PreparedNumberActionParser preparedNumberActionParser() {
    return PREPARED_NUMBER_ACTION_PARSER;
  }

  /** @return parser for percentage values */
  public static PercentParser percentParser() {
    return PERCENT_PARSER;
  }
}
