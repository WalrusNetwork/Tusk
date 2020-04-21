package network.walrus.utils.core.translation;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import network.walrus.utils.core.text.LegacyText;
import network.walrus.utils.core.text.LocalizedFormat;

/**
 * Base class for all classes which provided references to {@link LocalizedFormat}s.
 *
 * <p>In order for the {@link #printUndefined(Logger)} method to function, each extending class must
 * add all of it's translations to {@link #USED_MESSAGES} in a static block and ensure that the
 * class is initialized during plugin initialization.
 *
 * @author Austin Mayes
 */
public abstract class MessageReferenceHolder {

  protected static final List<FormatInformation> USED_MESSAGES = Lists.newArrayList();

  protected static LocalizedFormat get(String path) {
    return GlobalLocalizations.INSTANCE.getBundle().getFormat(path);
  }

  protected static LegacyText getLegacy(String path) {
    return GlobalLocalizations.INSTANCE.getBundle().getLegacyText(path);
  }

  protected static LocalizedFormat[] getAll(String prefix) {
    int i = 1;
    Locale defaultLocale =
        GlobalLocalizations.INSTANCE.getBundle().getDefaultLocale().orElse(Locale.ENGLISH);
    List<LocalizedFormat> formats = Lists.newArrayList();
    while (GlobalLocalizations.INSTANCE.getBundle().has(defaultLocale, prefix + "." + i)) {
      formats.add(GlobalLocalizations.INSTANCE.getBundle().getFormat(prefix + "." + i));
      i++;
    }
    return formats.toArray(new LocalizedFormat[0]);
  }

  protected static LegacyText[] getAllLegacy(String prefix) {
    int i = 1;
    Locale defaultLocale =
        GlobalLocalizations.INSTANCE.getBundle().getDefaultLocale().orElse(Locale.ENGLISH);
    List<LegacyText> strings = Lists.newArrayList();
    while (GlobalLocalizations.INSTANCE.getBundle().has(defaultLocale, prefix + "." + i)) {
      strings.add(GlobalLocalizations.INSTANCE.getBundle().getLegacyText(prefix + "." + i));
      i++;
    }
    return strings.toArray(new LegacyText[0]);
  }

  /** Print all translations which reference non-existent keys to the provided logger. */
  public static void printUndefined(Logger logger) {
    for (FormatInformation i : USED_MESSAGES) {
      if (!i.present()) {
        logger.severe(
            "Translation \""
                + i.identifier()
                + "\" ("
                + i.desc
                + ") is missing! Arguments: "
                + i.argString());
      }
    }
  }

  protected abstract static class FormatInformation {
    final String desc;
    final List<String> arguments = Lists.newArrayList();

    public FormatInformation(String desc) {
      this.desc = desc;
    }

    public FormatInformation argument(String string) {
      arguments.add(string);
      return this;
    }

    String argString() {
      if (arguments.isEmpty()) {
        return "none";
      }
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < arguments.size(); i++) {
        String arg = arguments.get(i);
        builder.append("{").append(i).append("} - ");
        builder.append(arg);
        builder.append(" | ");
      }
      return builder.toString().substring(0, builder.toString().length() - 2);
    }

    protected abstract String identifier();

    protected abstract boolean present();
  }

  protected static class GroupFormatInformation extends FormatInformation {
    final String prefix;
    final LocalizedFormat[] array;

    public GroupFormatInformation(String prefix, LocalizedFormat[] array, String desc) {
      super(desc);
      this.prefix = prefix;
      this.array = array;
    }

    @Override
    protected boolean present() {
      return array.length > 0;
    }

    @Override
    protected String identifier() {
      return prefix + ".[any number greater than 0]";
    }
  }

  protected static class MessageInformation extends FormatInformation {

    final String key;

    public MessageInformation(LocalizedFormat format, String desc) {
      super(desc);
      this.key = format.getKey();
    }

    @Override
    protected boolean present() {
      return GlobalLocalizations.INSTANCE.getBundle().has(Locale.ENGLISH, key);
    }

    public MessageInformation argument(String string) {
      arguments.add(string);
      return this;
    }

    @Override
    protected String identifier() {
      return this.key;
    }
  }

  protected static class LegacyInformation extends FormatInformation {

    final String key;

    public LegacyInformation(LegacyText format, String desc) {
      super(desc);
      this.key = format.getKey();
    }

    @Override
    protected boolean present() {
      return GlobalLocalizations.INSTANCE.getBundle().has(Locale.ENGLISH, key);
    }

    @Override
    protected String identifier() {
      return this.key;
    }
  }

  protected static class LegacyGroupInformation extends FormatInformation {
    final String prefix;
    final LegacyText[] array;

    public LegacyGroupInformation(String prefix, LegacyText[] array, String desc) {
      super(desc);
      this.prefix = prefix;
      this.array = array;
    }

    @Override
    protected boolean present() {
      return array.length > 0;
    }

    @Override
    protected String identifier() {
      return prefix + ".[any number greater than 0]";
    }
  }
}
