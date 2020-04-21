package network.walrus.ubiquitous.bukkit.settings.types;

/**
 * An enumeration of common setting types.
 *
 * @author Avicus Network
 */
public class SettingTypes {

  /** A {@link Boolean} setting type. */
  public static final BooleanSettingType BOOLEAN = new BooleanSettingType();
  /** A {@link Double} setting type. */
  public static final NumberSettingType<Double> DOUBLE =
      new NumberSettingType<>(Double::parseDouble);
  /** A {@link Float} setting type. */
  public static final NumberSettingType<Float> FLOAT = new NumberSettingType<>(Float::parseFloat);
  /** A {@link Integer} setting type. */
  public static final NumberSettingType<Integer> INTEGER =
      new NumberSettingType<>(Integer::parseInt);
  /** A {@link Long} setting type. */
  public static final NumberSettingType<Long> LONG = new NumberSettingType<>(Long::parseLong);

  /**
   * Create an {@link EnumSettingType enum setting type} for the specified enum.
   *
   * @param enumClass the enum class
   * @param <T> the enum type
   * @return the enum setting type
   */
  public static <T extends Enum<T>> EnumSettingType<T> enumOf(Class<T> enumClass) {
    return new EnumSettingType<>(enumClass);
  }
}
