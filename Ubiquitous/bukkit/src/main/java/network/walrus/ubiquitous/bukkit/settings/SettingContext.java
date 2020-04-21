package network.walrus.ubiquitous.bukkit.settings;

/**
 * A setting with a value.
 *
 * @param <R> type of raw value which the setting can hold, and which in turn this object holds
 * @param <S> type of setting this object holds a reference to
 * @author Avicus Netowrk
 */
public class SettingContext<S extends Setting<R>, R> {

  private final S setting;
  private final SettingValue<R> value;

  /**
   * Constructor.
   *
   * @param setting which is being stored
   * @param value of the referenced setting which is being stored
   */
  SettingContext(S setting, SettingValue<R> value) {
    this.setting = setting;
    this.value = value;
  }

  public S getSetting() {
    return setting;
  }

  public SettingValue<R> getValue() {
    return value;
  }
}
