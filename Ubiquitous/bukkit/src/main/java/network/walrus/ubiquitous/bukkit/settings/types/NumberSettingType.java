package network.walrus.ubiquitous.bukkit.settings.types;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.concurrent.Immutable;
import network.walrus.ubiquitous.bukkit.settings.SettingValue;
import network.walrus.ubiquitous.bukkit.settings.types.NumberSettingType.NumberSettingValue;

/**
 * Any type of number setting.
 *
 * @param <N> type of number this setting type can be
 * @author Avicus Network
 */
@Immutable
public final class NumberSettingType<N extends Number>
    implements SettingType<NumberSettingValue<N>, N> {

  private final Function<String, N> function;

  protected NumberSettingType(Function<String, N> function) {
    this.function = function;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<NumberSettingValue<N>> parse(String raw) {
    try {
      N number = this.function.apply(raw);
      return Optional.of(value(number));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public NumberSettingValue<N> value(N raw) {
    return new NumberSettingValue<>(raw);
  }

  /**
   * A value of the number setting type.
   *
   * @param <N> type of number this setting value can be
   */
  @Immutable
  public static class NumberSettingValue<N extends Number> implements SettingValue<N> {

    private final N raw;

    /**
     * Constructor.
     *
     * @param raw value
     */
    public NumberSettingValue(N raw) {
      this.raw = raw;
    }

    @Override
    public N raw() {
      return this.raw;
    }

    @Override
    public String serialize() {
      return String.valueOf(this.raw);
    }
  }
}
