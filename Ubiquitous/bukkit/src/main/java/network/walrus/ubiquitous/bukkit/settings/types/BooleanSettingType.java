package network.walrus.ubiquitous.bukkit.settings.types;

import java.util.Optional;
import javax.annotation.concurrent.Immutable;
import network.walrus.ubiquitous.bukkit.settings.SettingValueToggleable;
import network.walrus.ubiquitous.bukkit.settings.types.BooleanSettingType.BooleanSettingValue;

/**
 * True or false setting.
 *
 * @author Avicus Network
 */
@Immutable
public final class BooleanSettingType implements SettingType<BooleanSettingValue, Boolean> {

  private static final BooleanSettingValue TRUE = new BooleanSettingValue(true);
  private static final BooleanSettingValue FALSE = new BooleanSettingValue(false);

  /** Hidden Constructor. */
  BooleanSettingType() {}

  @Override
  public Optional<BooleanSettingValue> parse(String raw) {
    switch (raw.toLowerCase()) {
      case "true":
      case "on":
      case "yes":
        return Optional.of(TRUE);
      case "false":
      case "off":
      case "no":
        return Optional.of(FALSE);
      default:
        return Optional.empty();
    }
  }

  @Override
  public BooleanSettingValue value(Boolean raw) {
    return new BooleanSettingValue(raw);
  }

  /** The value of a {@link BooleanSettingType} */
  @Immutable
  public static final class BooleanSettingValue implements SettingValueToggleable<Boolean> {

    private final boolean value;

    /**
     * Constructor.
     *
     * @param value of the setting
     */
    public BooleanSettingValue(boolean value) {
      this.value = value;
    }

    @Override
    public Boolean raw() {
      return this.value;
    }

    @Override
    public String serialize() {
      return this.value ? "on" : "off";
    }

    @Override
    public Boolean next() {
      return !this.value;
    }
  }
}
