package network.walrus.ubiquitous.bukkit.settings;

import com.google.common.collect.ArrayListMultimap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import network.walrus.ubiquitous.bukkit.settings.types.SettingType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Stores any number of settings for UUIDs.
 *
 * @author Avicus Network
 */
public class SettingStore {

  private final ArrayListMultimap<UUID, SettingContext> settings;

  /** Constructor. */
  public SettingStore() {
    this.settings = ArrayListMultimap.create();
  }

  /**
   * Set a setting without calling an event.
   *
   * @see #set(UUID, Setting, Object, boolean)
   */
  public <R> R set(UUID key, Setting<R> setting, R value) {
    return set(key, setting, value, true);
  }

  /**
   * Update the value of a setting for a UUID, and optionally call a {@link SettingModifyEvent}.
   *
   * @param key user the setting is for
   * @param setting to update
   * @param value to set the setting to for the specific user
   * @param callEvent if the setting change should fire an event
   * @param <R> type of setting value being changed
   * @return new value of the setting
   */
  public <R> R set(UUID key, Setting<R> setting, R value, boolean callEvent) {
    List<SettingContext> list = this.settings.get(key);
    for (SettingContext context : list) {
      if (context.getSetting().equals(setting)) {
        Bukkit.getLogger()
            .fine(
                String.format(
                    "[Settings] Removing %s from the store for %s (WAS: %s)",
                    setting.getId(), key, context.getValue().raw()));
        this.settings.values().remove(context);
        break;
      }
    }

    SettingContext context = new SettingContext<>(setting, setting.getType().value(value));
    this.settings.put(key, context);
    Player player = Bukkit.getPlayer(key);
    if (player != null && callEvent) {
      Bukkit.getPluginManager().callEvent(new SettingModifyEvent(context, player));
      Bukkit.getLogger()
          .fine(
              String.format(
                  "[Settings] Adding '%s' to the store for %s (IS: %s)",
                  setting.getId(), player.getName(), value));
    } else {
      Bukkit.getLogger()
          .fine(
              String.format(
                  "[Settings] Adding '%s' to the store for %s (IS: %s)",
                  setting.getId(), key, value));
    }
    return value;
  }

  /**
   * Set a collection of settings for a UUID, but don't fire a {@link SettingModifyEvent}.
   *
   * @see #set(UUID, Map, Collection, boolean)
   */
  public void set(UUID key, Map<String, String> parse, Collection<Setting> settings) {
    set(key, parse, settings, false);
  }

  /**
   * Set a collection of settings for a UUID using a map of parsable setting IDs and values.
   *
   * @param key user the setting is for
   * @param parse map of setting id -> value which will be parsed using {@link Setting#getId()} and
   *     {@link SettingType#parse(String)}, respectively.
   * @param settings which can be parsed and set
   * @param callEvent if the setting change should fire an event
   */
  @SuppressWarnings("unchecked")
  public void set(
      UUID key, Map<String, String> parse, Collection<Setting> settings, boolean callEvent) {
    for (Entry<String, String> entry : parse.entrySet()) {
      String id = entry.getKey();
      String value = entry.getValue();

      settings.stream()
          .filter(setting -> setting.getId().equals(id))
          .forEach(
              setting -> {
                Optional<SettingValue> parsed = setting.getType().parse(value);
                Object raw = parsed.isPresent() ? parsed.get().raw() : setting.getDefaultValue();

                this.set(key, setting, raw, callEvent);
              });
    }
  }

  /** {@link #set(UUID, Map, Collection)} with multiple UUIDs. */
  public void set(Map<UUID, Map<String, String>> parse, Collection<Setting> settings) {
    for (Entry<UUID, Map<String, String>> entry : parse.entrySet()) {
      set(entry.getKey(), entry.getValue(), settings);
    }
  }

  /**
   * Return the value of a particular setting for a specific key. If the key has no data for the
   * requested setting, {@link Setting#getDefaultValue()} will be used instead.
   *
   * @param key to retrieve settings by
   * @param setting to get the value for
   * @param <R> type of value the setting should return
   * @return value the key has for the requested setting, or the default one if no data is stored
   */
  @SuppressWarnings("unchecked")
  public <R> R get(UUID key, Setting<R> setting) {
    List<SettingContext> set = this.settings.get(key);
    for (SettingContext context : set) {
      if (context.getSetting().equals(setting)) {
        return (R) context.getValue().raw();
      }
    }
    Bukkit.getLogger()
        .fine(
            String.format(
                "[Settings] Retrieving default value for '%s' (%s) for %s",
                setting.getId(), setting.getDefaultValue(), key));
    set(key, setting, setting.getDefaultValue());
    return setting.getDefaultValue();
  }

  /**
   * Get all settings stored for the given key, in the format of setting ID -> value.
   *
   * @param key to retrieve settings by
   * @return map of id -> value for all settings associated with the given key
   */
  public Map<String, String> get(UUID key) {
    Map<String, String> values = new LinkedHashMap<>();
    for (SettingContext context : this.settings.get(key)) {
      String id = context.getSetting().getId();
      String value = context.getValue().serialize();
      values.put(id, value);
    }
    return values;
  }

  /** @return map of uuid -> (setting id -> value) for all data in the store */
  public Map<UUID, Map<String, String>> get() {
    Map<UUID, Map<String, String>> values = new LinkedHashMap<>();
    for (UUID key : this.settings.keySet()) {
      values.put(key, get(key));
    }
    return values;
  }

  /**
   * If the supplied setting has a type which supports a {@link SettingValueToggleable}, toggle to
   * the next value for a given user. If the setting does not support this, return {@link
   * Optional#empty()}.
   *
   * @param key user the setting is for
   * @param setting to toggle
   * @param <R> type of value the setting can be toggled between
   * @return new value if changed, otherwise empty (indicating the setting isn't toggleable)
   */
  @SuppressWarnings("unchecked")
  public <R> Optional<R> toggle(UUID key, Setting<R> setting) {
    R current = get(key, setting);

    SettingValue<R> value = setting.getType().value(current);

    if (value instanceof SettingValueToggleable) {
      SettingValueToggleable toggle = (SettingValueToggleable) value;
      R next = (R) toggle.next();
      Bukkit.getLogger()
          .info(
              String.format(
                  "[Settings] Toggling '%s' from '%s' to '%s' for %s",
                  setting.getId(), toggle.raw(), toggle.next(), key));
      set(key, setting, next);
      return Optional.of(next);
    }

    return Optional.empty();
  }

  /** Checks if the underlying data stored is equal. */
  @Override
  public boolean equals(Object object) {
    if (object instanceof SettingStore) {
      SettingStore store = (SettingStore) object;
      return store.get().equals(get());
    }
    return false;
  }

  /** An expensive hashCode() implementation. */
  @Override
  public int hashCode() {
    return get().hashCode();
  }

  /** A pretty Map string. */
  @Override
  public String toString() {
    return this.get().toString();
  }
}
