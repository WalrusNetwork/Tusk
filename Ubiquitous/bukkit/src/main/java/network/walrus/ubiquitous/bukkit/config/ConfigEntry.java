package network.walrus.ubiquitous.bukkit.config;

import java.time.Duration;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Config;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;

/**
 * Represents a config entry within the config and stores data needed to represent the entry in UI.
 *
 * @param <T> config entry type
 * @author Rafi Baum
 */
public class ConfigEntry<T> {

  private final LocalizedFormat name;
  private T value;

  /**
   * @param name of entry to be used in UI
   * @param value default value of config
   */
  public ConfigEntry(LocalizedFormat name, T value) {
    this.name = name;
    this.value = value;
  }

  private static Localizable formatValue(Object value) {
    Localizable formatted;

    if (value instanceof Number) {
      formatted = new LocalizedNumber((Number) value, Config.VALUE);
    } else if (value instanceof Boolean) {
      formatted =
          (Boolean) value
              ? UbiquitousMessages.TRUE.with(Config.VALUE)
              : UbiquitousMessages.FALSE.with(Config.VALUE);
    } else if (value instanceof Duration) {
      formatted =
          new UnlocalizedText(
              StringUtils.secondsToClock((int) ((Duration) value).getSeconds()), Config.VALUE);
    } else if (value instanceof Material) {
      String name = ((Material) value).name();
      name = name.replace('_', ' ').toLowerCase();
      StringBuilder prettyName = new StringBuilder();
      for (String s : name.split(" ")) {
        prettyName.append(s.substring(0, 1).toUpperCase());
        prettyName.append(s.substring(1));
        prettyName.append(' ');
      }

      formatted = new UnlocalizedText(prettyName.toString().trim(), Config.OPTION);
    } else if (value instanceof Pair) {
      Pair pair = (Pair) value;

      formatted =
          new UnlocalizedFormat("{0} ({1})")
              .with(formatValue(pair.getKey()), formatValue(pair.getValue()));
    } else {
      throw new IllegalArgumentException(
          "Format not found for argument of type: " + value.getClass().getSimpleName());
    }

    return formatted;
  }

  /** @return value associated with config entry */
  public T get() {
    return value;
  }

  /** @return value associated with the config entry formatted appropriately */
  public Localizable getFormatted() {
    return formatValue(value);
  }

  /** @param value the new value of this entry */
  public void set(T value) {
    this.value = value;
  }

  /** @return the localized name of the config entry */
  public LocalizedFormat getName() {
    return name;
  }
}
