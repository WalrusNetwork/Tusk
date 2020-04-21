package network.walrus.ubiquitous.bukkit.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import network.walrus.ubiquitous.bukkit.settings.types.SettingType;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.command.CommandSender;

/**
 * Represents a configurable option, a setting silly!
 *
 * @param <R> type of value that this setting can have
 * @author Avicus Network
 */
public class Setting<R> {

  private final String id;
  private final SettingType<?, R> type;
  private final R defaultValue;
  private final Localizable name;
  private final List<Localizable> aliases;
  private final Localizable summary;
  private final Optional<Localizable> description;

  /**
   * Constructor with no long description or aliases.
   *
   * @param id of the setting, used for reference and storage
   * @param type of setting value which this setting can be chosen from
   * @param defaultValue to be selected when the setting is initialized for this first time for a
   *     player
   * @param name of the setting to display to the player
   * @param summary of what this setting changes, in a sentence or less
   * @param <V> value type which this setting is made up of
   */
  public <V extends SettingValue<R>> Setting(
      String id, SettingType<V, R> type, R defaultValue, Localizable name, Localizable summary) {
    this(id, type, defaultValue, name, Collections.emptyList(), summary, Optional.empty());
  }

  /**
   * Constructor with no long description.
   *
   * @param id of the setting, used for reference and storage
   * @param type of setting value which this setting can be chosen from
   * @param defaultValue to be selected when the setting is initialized for this first time for a
   *     player
   * @param name of the setting to display to the player
   * @param aliases that this setting can also be referenced by in commands and UI
   * @param summary of what this setting changes, in a sentence or less
   * @param <V> value type which this setting is made up of
   */
  public <V extends SettingValue<R>> Setting(
      String id,
      SettingType<V, R> type,
      R defaultValue,
      Localizable name,
      List<Localizable> aliases,
      Localizable summary) {
    this(id, type, defaultValue, name, aliases, summary, Optional.empty());
  }

  /**
   * Constructor.
   *
   * @param id of the setting, used for reference and storage
   * @param type of setting value which this setting can be chosen from
   * @param defaultValue to be selected when the setting is initialized for this first time for a
   *     player
   * @param name of the setting to display to the player
   * @param aliases that this setting can also be referenced by in commands and UI
   * @param summary of what this setting changes, in a sentence or less
   * @param description going into more detail of the effects of this setting
   * @param <V> value type which this setting is made up of
   */
  public <V extends SettingValue<R>> Setting(
      String id,
      SettingType<V, R> type,
      R defaultValue,
      Localizable name,
      List<Localizable> aliases,
      Localizable summary,
      Optional<Localizable> description) {
    this.id = id;
    this.type = type;
    this.defaultValue = defaultValue;
    this.name = name;
    this.aliases = aliases;
    this.summary = summary;
    this.description = description;
  }

  /**
   * Given a collection of settings to search, a locale to perform translation with, and a search
   * query, attempt to find a {@link Setting} which has the name or aliases which match the supplied
   * query. If no settings match, {@link Optional#empty()} will be returned instead.
   *
   * @param locale used to render aliases
   * @param query which is being used to search for a specific setting
   * @param settings to search inside of
   * @return a setting who's name or aliases match the query when translated using the locale
   */
  public static Optional<Setting> search(
      CommandSender sender, String query, List<Setting> settings) {
    for (Setting<?> setting : settings) {
      List<String> names = setting.getAllAliases(sender);
      for (String name : names) {
        if (name.equalsIgnoreCase(query)) {
          return Optional.of(setting);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Get a plaintext list of all of the aliases this setting can be referenced by. This includes the
   * setting name and any additional aliases provided in {@link #aliases}. The provided locale is
   * used to render the name and aliases into generic strings.
   *
   * @param locale used to render aliases
   * @return a collection of aliases translated into plaintext using the supplied locale
   */
  public List<String> getAllAliases(CommandSender sender) {
    List<String> result = new ArrayList<>();
    result.add(this.name.render(sender).toPlainText());
    List<String> list = new ArrayList<>();
    for (Localizable alias : this.aliases) {
      String rendered = alias.render(sender).toPlainText();
      list.add(rendered);
    }
    result.addAll(list);
    return result;
  }

  public String getId() {
    return id;
  }

  public SettingType<?, R> getType() {
    return type;
  }

  public R getDefaultValue() {
    return defaultValue;
  }

  public Localizable getName() {
    return name;
  }

  public List<Localizable> getAliases() {
    return aliases;
  }

  public Localizable getSummary() {
    return summary;
  }

  public Optional<Localizable> getDescription() {
    return description;
  }
}
