package network.walrus.utils.core.text;

import network.walrus.utils.core.translation.LocaleBundle;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * Represents a format that takes arguments for localization with {@link LocalizedText}s.
 *
 * @author Avicus Network
 */
public class LocalizedFormat implements LocalizableFormat<LocalizedText> {

  private final LocaleBundle bundle;
  private final String key;

  /**
   * Constructor.
   *
   * @param bundle to pull message definitions from
   * @param key corresponding to the message in the bundle
   */
  public LocalizedFormat(LocaleBundle bundle, String key) {
    this.bundle = bundle;
    this.key = key;
  }

  @Override
  public LocalizedText with(TextStyle style, Localizable... arguments) {
    return new LocalizedText(this.bundle, this.key, style, arguments);
  }

  @Override
  public LocalizedText with(Localizable... arguments) {
    return new LocalizedText(this.bundle, this.key, arguments);
  }

  public String getKey() {
    return key;
  }
}
