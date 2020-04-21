package network.walrus.utils.core.command.exception;

import app.ashcon.intake.exception.TranslatableCommandException;
import network.walrus.common.text.PersonalizedComponent;
import network.walrus.utils.core.color.NetworkColorConstants.Commands;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.LocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A command exception that can be translated later on to be sent to players.
 *
 * @author kashike
 */
public abstract class AbstractTranslatableCommandException extends TranslatableCommandException {

  private final LocalizedFormat format;
  private final Localizable[] args;

  /**
   * Constructor
   *
   * @param format of the exception
   */
  public AbstractTranslatableCommandException(LocalizedFormat format) {
    this(format, Localizable.EMPTY);
  }

  /**
   * Constructor
   *
   * @param format of the exception
   * @param args for the format
   */
  public AbstractTranslatableCommandException(LocalizedFormat format, Localizable... args) {
    this.format = format;
    this.args = args;
  }

  /**
   * Format an exception with coloring and arguments to be sent to players.
   *
   * @param exception to format
   * @return a formatted {@link LocalizedText} from the exception
   */
  public static LocalizedText format(AbstractTranslatableCommandException exception) {
    final LocalizedText text;

    if (exception.args.length == 0) {
      text = exception.format.with();
    } else {
      text = exception.format.with(exception.args);
    }

    text.style().inherit(exception.getStyle());
    return text;
  }

  /**
   * Format an error text from a format.
   *
   * @param format to create an error text for
   * @return a formatted {@link LocalizedText} from the format
   */
  public static LocalizedText error(LocalizedFormat format) {
    return error(format, Localizable.EMPTY);
  }

  /**
   * Format an error text from a format and arguments.
   *
   * @param format to create an error text for
   * @param args to supply to the format
   * @return a formatted {@link LocalizedText} from the format
   */
  public static LocalizedText error(LocalizedFormat format, Localizable... args) {
    LocalizedText text = format.with(Commands.ERROR, args);
    return text;
  }

  @Override
  public PersonalizedComponent getComponent() {
    return format(this);
  }

  /** @return the style of the exception */
  public abstract TextStyle getStyle();
}
