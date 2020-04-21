package network.walrus.utils.core.util;

import java.time.Duration;
import java.util.List;

/**
 * Various string utilities.
 *
 * @author Avicus Network
 */
public final class StringUtils {

  /**
   * Convert a string to superscript. NOTE: This only works for numbers and basic math symbols (and
   * spaces/periods).
   *
   * @param text to format
   */
  public static String superScript(String text) {
    char[] chars = text.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      switch (chars[i]) {
          // Ignored
        case '.':
        case ' ':
          break;
          // Math
        case '+':
          chars[i] = '\u207a';
          break;
        case '-':
          chars[i] = '\u207b';
          break;
        case '=':
          chars[i] = '\u207c';
          break;
        case '(':
          chars[i] = '\u207d';
          break;
        case ')':
          chars[i] = '\u207e';
          break;
          // Numbers
        case '2':
          chars[i] = '\u00b2';
          break;
        case '3':
          chars[i] = '\u00b3';
          break;
        default:
          chars[i] = (char) (chars[i] - '0' + '\u2070');
          break;
      }
    }
    return String.valueOf(chars);
  }

  /**
   * Convert a string to subscript. NOTE: This only works for numbers and basic math symbols (and
   * spaces/periods).
   *
   * @param text to format
   */
  public static String subScript(String text) {
    char[] chars = text.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      switch (chars[i]) {
          // Ignored
        case '.':
        case ' ':
          break;
          // Math
        case '+':
          chars[i] = '\u208a';
          break;
        case '-':
          chars[i] = '\u208b';
          break;
        case '=':
          chars[i] = '\u208c';
          break;
        case '(':
          chars[i] = '\u208d';
          break;
        case ')':
          chars[i] = '\u208e';
          break;
          // Numbers
        default:
          chars[i] = (char) (chars[i] - '0' + '\u2080');
          break;
      }
    }
    return String.valueOf(chars);
  }

  /**
   * Convert an integer representing total seconds into a stringified clock format.
   *
   * @param seconds to convert to a clock format
   * @return hh:mm:ss representation of the number of seconds passed into the method
   */
  public static String secondsToClock(long seconds) {
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long secs = seconds % 60;

    if (hours == 0) {
      return String.format("%02d:%02d", minutes, secs);
    }

    return String.format("%02d:%02d:%02d", hours, minutes, secs);
  }

  /**
   * Converts a duration into a stringified clock format
   *
   * @param duration to convert to clock format
   * @return hh:mm:ss representation of the number of seconds passed into the method
   */
  public static String durationToClock(Duration duration) {
    return secondsToClock(duration.getSeconds());
  }

  /**
   * Join a collection of {@link T}s with a specific delimiter using a supplied {@link Stringify}
   * parser.
   *
   * @param parts to join
   * @param delimeter used to separate the parts
   * @param stringify used to convert the parts into strings
   * @param <T> type of parts being joined
   * @return a string containing the result of {@link Stringify#on(Object)} performed on each member
   *     of the collection separated by the supplied delimiter.
   */
  public static <T> String join(List<T> parts, String delimeter, Stringify<T> stringify) {
    String text = "";
    for (T part : parts) {
      text += stringify.on(part);
      if (parts.indexOf(part) != parts.size() - 1) {
        text += delimeter;
      }
    }
    return text;
  }

  public interface Stringify<T> {

    /**
     * @param object to convert to a string
     * @return string representation of {@link T}
     */
    String on(T object);
  }
}
