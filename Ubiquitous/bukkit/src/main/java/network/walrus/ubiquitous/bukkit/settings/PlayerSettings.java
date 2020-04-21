package network.walrus.ubiquitous.bukkit.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * A global registry for {@link Setting}s.
 *
 * @author Avicus Network
 */
public class PlayerSettings {

  /** The global setting store. */
  private static final SettingStore STORE = new SettingStore();
  /** The global setting list. */
  private static final List<Setting> SETTINGS = new ArrayList<>();

  /**
   * Gets the global setting store.
   *
   * @return the global setting store
   */
  public static SettingStore store() {
    return STORE;
  }

  /**
   * Gets the global setting list.
   *
   * @return the global setting list
   */
  public static List<Setting> settings() {
    return SETTINGS;
  }

  /**
   * Register a setting to the global setting list.
   *
   * @param setting the setting
   * @param <R> the value type
   */
  public static <R> void register(Setting<R> setting) {
    SETTINGS.add(setting);
  }

  /**
   * Unregister a setting from the global setting list.
   *
   * @param setting the setting
   * @param <R> the value type
   */
  public static <R> void unregister(Setting<R> setting) {
    SETTINGS.remove(setting);
  }

  /**
   * Gets the value of the specified setting for the player, or the setting's default value.
   *
   * @param player the player
   * @param setting the setting
   * @param <R> the value type
   * @return the value
   * @see #get(UUID, Setting)
   */
  public static <R> R get(Player player, Setting<R> setting) {
    return get(player.getUniqueId(), setting);
  }

  /**
   * Gets the value of the specified setting for the player id, or the setting's default value.
   *
   * @param playerId the player id
   * @param setting the setting
   * @param <R> the value type
   * @return the value
   */
  public static <R> R get(UUID playerId, Setting<R> setting) {
    return STORE.get(playerId, setting);
  }
}
