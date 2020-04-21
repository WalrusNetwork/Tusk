package network.walrus.ubiquitous.bukkit.settings.types;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.concurrent.Immutable;
import network.walrus.ubiquitous.bukkit.settings.SettingValueToggleable;
import network.walrus.ubiquitous.bukkit.settings.types.EnumSettingType.EnumSettingValue;

/**
 * Any type of enumerator setting.
 *
 * @param <E> type of enum this setting is for
 * @author Avicus Network
 */
@Immutable
public class EnumSettingType<E extends Enum> implements SettingType<EnumSettingValue<E>, E> {

  private final Class<E> type;
  private final Map<String, E> constants;

  /**
   * Constructor.
   *
   * @param type of enum this setting is for
   */
  protected EnumSettingType(Class<E> type) {
    this.type = type;

    ImmutableMap.Builder<String, E> builder = ImmutableMap.builder();

    E[] constants = this.type.getEnumConstants();
    for (E constant : constants) {
      builder.put(constant.name().toLowerCase(), constant);
    }

    this.constants = builder.build();
  }

  @Override
  public Optional<EnumSettingValue<E>> parse(String raw) {
    raw = raw.toLowerCase();

    E result = null;
    E close = null;

    for (Map.Entry<String, E> entry : this.constants.entrySet()) {
      if (entry.getKey().equals(raw)) {
        result = entry.getValue();
        break;
      } else if (entry.getKey().startsWith(raw)) {
        close = entry.getValue();
      }
    }

    if (result == null) {
      result = close;
    }

    return Optional.ofNullable(result).map(this::value);
  }

  @Override
  public EnumSettingValue<E> value(E raw) {
    return new EnumSettingValue<>(this.type, raw);
  }

  /** */
  @Immutable
  public static class EnumSettingValue<E extends Enum> implements SettingValueToggleable<E> {

    private final Class<E> type;
    private final E raw;

    /**
     * Constructor.
     *
     * @param type of enum this value is for
     * @param raw value of the setting
     */
    public EnumSettingValue(Class<E> type, E raw) {
      this.type = type;
      this.raw = raw;
    }

    @Override
    public E raw() {
      return this.raw;
    }

    @Override
    public String serialize() {
      return this.raw.name().toLowerCase();
    }

    @Override
    public E next() {
      E[] constants = this.type.getEnumConstants();
      return constants[(this.raw.ordinal() + 1) % constants.length];
    }
  }
}
