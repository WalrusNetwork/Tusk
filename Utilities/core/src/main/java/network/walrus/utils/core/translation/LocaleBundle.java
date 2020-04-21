package network.walrus.utils.core.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import network.walrus.utils.core.text.LegacyText;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.LocalizedText;

/**
 * A collection of {@link LocaleStrings} that allows retrieval by language.
 *
 * @author Avicus Network
 */
public class LocaleBundle {

  private List<LocaleStrings> locales;
  private LocaleStrings defStrings = null;

  /** Constructor. */
  public LocaleBundle() {
    this(new ArrayList<>());
  }

  /**
   * Constructor.
   *
   * @param locales which can be queried for each locale to get messages
   */
  public LocaleBundle(List<LocaleStrings> locales) {
    this.locales = locales;
  }

  /**
   * Constructor.
   *
   * @param locales which can be queried for each locale to get messages
   * @param defaultStrings to fall back on if
   */
  public LocaleBundle(List<LocaleStrings> locales, LocaleStrings defaultStrings) {
    this.locales = locales;
    this.locales.remove(defaultStrings);
    this.locales.add(0, defaultStrings);
  }

  /**
   * @return the fallback locale that will be used if the requested one is not supported, or empty
   *     if there is no fallback locale.
   */
  public Optional<Locale> getDefaultLocale() {
    Optional<LocaleStrings> strings = getDefaultStrings();
    if (strings.isPresent()) {
      return Optional.of(strings.get().getLocale());
    }
    return Optional.empty();
  }

  /**
   * @return the fallback string set that will be used if the requested one is not supported, or
   *     empty if there is no fallback string set.
   */
  public Optional<LocaleStrings> getDefaultStrings() {
    if (this.defStrings != null) {
      return Optional.of(this.defStrings);
    }

    if (this.locales.size() > 0) {
      return Optional.of(this.locales.get(0));
    }
    return Optional.empty();
  }

  /**
   * Make an attempt to get a string collection for the requested locale. If a collection does not
   * exist for the supplied locale, {@link ##getDefaultStrings} will be returned instead.
   *
   * @param locale to search for
   * @return a string collection for the requested locale
   */
  public Optional<LocaleStrings> getStringsRoughly(Locale locale) {
    LocaleStrings match = null;
    for (LocaleStrings test : this.locales) {
      if (test.getLocale().equals(locale)) {
        return Optional.of(test);
      } else if (test.getLocale().getLanguage().equals(locale.getLanguage())) {
        match = test;
        break;
      }
    }

    if (match != null) {
      return Optional.of(match);
    }

    return getDefaultStrings();
  }

  /**
   * Add a collection of strings to the available set of translations.
   *
   * @param strings to add
   */
  public void add(LocaleStrings strings) {
    this.locales.add(strings);
  }

  /**
   * Get a string for a specified locale by its key. If the string does not exist for the specified
   * locale, and the requested locale is not the default, an attempt will be made to fall back to
   * the {@link #getDefaultStrings()}, if they are present. If all methods of retrieval yield no
   * results, {@link Optional#empty()} will be returned.
   *
   * @param locale to search inside of
   * @param key to search for
   * @return a string for a specified locale by its key
   */
  public Optional<String> get(Locale locale, String key) {
    Optional<LocaleStrings> strings = getStringsRoughly(locale);

    if (!strings.isPresent()) {
      return Optional.empty();
    }

    Optional<String> result = strings.get().get(key);

    if (result.isPresent()) {
      return Optional.of(result.get());
    }

    Optional<LocaleStrings> defStrings = getDefaultStrings();

    if (!defStrings.isPresent() || strings.equals(getDefaultStrings())) {
      return Optional.empty();
    }

    return get(getDefaultLocale().get(), key);
  }

  /**
   * Determine if the requested key is present for the supplied locale.
   *
   * @param locale to search inside of
   * @param key to search for
   * @return if the requested key is present for the supplied locale
   */
  public boolean has(Locale locale, String key) {
    Optional<String> found = get(locale, key);
    return found.isPresent() && !found.get().isEmpty();
  }

  /** @see LocalizedFormat#LocalizedFormat(LocaleBundle, String). */
  public LocalizedFormat getFormat(String key) {
    return new LocalizedFormat(this, key);
  }

  /** @see LocalizedText#LocalizedText(LocaleBundle, String, Localizable...). */
  public LocalizedText getText(String key, Localizable... arguments) {
    return new LocalizedText(this, key, arguments);
  }

  /** @see LegacyText#LegacyText(LocaleBundle, String) */
  public LegacyText getLegacyText(String key) {
    return new LegacyText(this, key);
  }

  /**
   * Set a collection of strings as the ones that should be used in the case where a requested
   * string set is not available.
   *
   * @param defStrings to set as default
   */
  public void setDefStrings(LocaleStrings defStrings) {
    this.defStrings = defStrings;
  }

  /** @return all of the available string sets which can be used for translation */
  public List<LocaleStrings> getLocales() {
    return locales;
  }
}
