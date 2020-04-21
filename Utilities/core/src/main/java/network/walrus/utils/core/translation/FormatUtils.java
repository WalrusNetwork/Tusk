package network.walrus.utils.core.translation;

import network.walrus.utils.core.text.LocalizableFormat;
import network.walrus.utils.core.text.UnlocalizedFormat;

/**
 * Utilities for creating {@link LocalizableFormat}s.
 *
 * @author Austin Mayes
 */
public class FormatUtils {

  /**
   * Create an Oxford comma list containing placeholders where {@link Localizable}s can be
   * substituted in by the translation API with the number of placeholders being the supplied size.
   *
   * @param size of the list
   * @return format which has placeholders in list form
   */
  public static LocalizableFormat humanList(int size) {
    UnlocalizedFormat format;
    if (size == 1) {
      format = new UnlocalizedFormat("{0}");
    } else {
      String stringFormat = "";

      for (int i = 0; i <= size; ++i) {
        stringFormat = stringFormat + "{" + i + "} ";
        if (i - 1 == size) {
          stringFormat = stringFormat + "and, ";
        } else if (i != size) {
          stringFormat = stringFormat + ", ";
        }
      }

      format = new UnlocalizedFormat(stringFormat);
    }

    return format;
  }
}
