package network.walrus.ubiquitous.bukkit.settings.types;

import java.util.Optional;
import network.walrus.ubiquitous.bukkit.settings.SettingValue;

/**
 * Represents a type of setting.
 *
 * @param <R> type of value that this setting can have
 * @param <V> {@link SettingValue} type which is responsible for managing values of type {@link R}
 * @author Avicus Network
 */
public interface SettingType<V extends SettingValue<R>, R> {

  /**
   * Parses a string.
   *
   * @return Empty if the input is invalid, otherwise the parsed value.
   */
  Optional<V> parse(String raw);

  /** Gets the value instance based on raw data. */
  V value(R raw);
}
