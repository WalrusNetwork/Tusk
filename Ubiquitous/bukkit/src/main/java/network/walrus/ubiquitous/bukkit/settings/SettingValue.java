package network.walrus.ubiquitous.bukkit.settings;

/**
 * Represents a type of which a setting can be stored.
 *
 * @param <R> type of value that this wrapper can contain
 * @author Avicus Network
 */
public interface SettingValue<R> {

  /** Gets the actual value. */
  R raw();

  /** Serializes the value. */
  String serialize();
}
