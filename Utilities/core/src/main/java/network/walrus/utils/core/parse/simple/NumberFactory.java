package network.walrus.utils.core.parse.simple;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Class responsible for the implementation of parsers for each number type.
 *
 * @param <T> type of number being parsed
 * @author Overcast Network
 */
public class NumberFactory<T extends Number> {

  private static final Map<Class<? extends Number>, NumberFactory<?>> byType =
      ImmutableMap.<Class<? extends Number>, NumberFactory<?>>builder()
          .put(Byte.class, new NumberFactory<>(Byte.MIN_VALUE, Byte.MAX_VALUE, Byte::valueOf))
          .put(Short.class, new NumberFactory<>(Short.MIN_VALUE, Short.MAX_VALUE, Short::valueOf))
          .put(
              Integer.class,
              new NumberFactory<>(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer::valueOf))
          .put(Long.class, new NumberFactory<>(Long.MIN_VALUE, Long.MAX_VALUE, Long::valueOf))
          .put(
              Float.class,
              new NumberFactory<>(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float::valueOf))
          .put(
              Double.class,
              new NumberFactory<>(
                  Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double::valueOf))
          .build();
  private final Function<String, T> parser;
  private final T negativeInfinity, positiveInfinity;

  private NumberFactory(T negativeInfinity, T positiveInfinity, Function<String, T> parser) {
    this.negativeInfinity = negativeInfinity;
    this.positiveInfinity = positiveInfinity;
    this.parser = parser;
  }

  /** @return all number types which can be parsed by the pre-defined factories */
  public static Set<Class<? extends Number>> numberTypes() {
    return byType.keySet();
  }

  /** @see #factoryType(TypeToken). */
  public static <T extends Number> TypeToken<NumberFactory<T>> factoryType(Class<T> numberType) {
    return factoryType(TypeToken.of(numberType));
  }

  /**
   * Create a {@link TypeToken} representing a number factory which is responsible for parsing
   * objects of type {@link T}.
   *
   * @param numberType of the factory
   * @param <T> type of number being parsed
   * @return token representing a factory which can parse {@link T}s
   */
  public static <T extends Number> TypeToken<NumberFactory<T>> factoryType(
      TypeToken<T> numberType) {
    return new TypeToken<NumberFactory<T>>() {}.where(new TypeParameter<T>() {}, numberType);
  }

  /**
   * Get a number factory which can handle parsing for objects of type {@link T}.
   *
   * @param type of number the factory should parse
   * @param <T> type of numbers being parsed
   * @return number factory which can parse {@link T}s
   */
  public static <T extends Number> NumberFactory<T> get(Class<T> type) {
    final NumberFactory<T> factory = (NumberFactory<T>) byType.get(type);
    if (factory == null) {
      throw new IllegalArgumentException("No NumberFactory for type " + type.getName());
    }
    return factory;
  }

  /**
   * Parse an instance of {@link T} from a string, with support for infinite notations.
   *
   * @param text to parse
   * @return number represented by the string
   * @throws NumberFormatException if the text contains invalid characters
   */
  public T parse(String text) throws NumberFormatException {
    if ("oo".equals(text)) {
      return infinity(true);
    } else if ("-oo".equals(text)) {
      return infinity(false);
    } else {
      return parseFinite(text);
    }
  }

  /**
   * Parse an instance of {@link T} from a string, not handling infinite notations.
   *
   * @param text to parse
   * @return number represented by the string
   * @throws NumberFormatException if the text contains invalid characters
   */
  public T parseFinite(String text) throws NumberFormatException {
    return parser.apply(text);
  }

  /**
   * Return the signed or unhinged infinite value of {@link T}.
   *
   * @param sign marking the infinite direction as negative
   * @return infinity value
   */
  public T infinity(boolean sign) {
    return sign ? positiveInfinity : negativeInfinity;
  }

  /**
   * Determine if {@link T} is an infinite value.
   *
   * @param value to check
   * @return if {@link T} is infinite
   */
  public boolean isInfinite(T value) {
    return positiveInfinity.equals(value) || negativeInfinity.equals(value);
  }

  /**
   * Determine if {@link T} is a finite value.
   *
   * @param value to check
   * @return if {@link T} is finite
   */
  public boolean isFinite(T value) {
    return !isInfinite(value);
  }
}
