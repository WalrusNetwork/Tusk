package network.walrus.ubiquitous.bukkit.settings;

/**
 * Represents a type of setting that can be toggled or set without a value.
 *
 * @param <R> type of value that this wrapper can contain
 * @author Avicus Network
 */
public interface SettingValueToggleable<R> extends SettingValue<R> {

  /** Retrieve the next raw data after this value. */
  R next();
}
