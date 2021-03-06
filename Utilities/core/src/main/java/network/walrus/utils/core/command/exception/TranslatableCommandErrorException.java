package network.walrus.utils.core.command.exception;

import network.walrus.utils.core.color.NetworkColorConstants.Commands;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A command exception that can be translated later on to be sent to players, and is already
 * formatted as an error.
 *
 * @author kashike
 */
public class TranslatableCommandErrorException extends AbstractTranslatableCommandException {

  /** @param format of the exception */
  public TranslatableCommandErrorException(LocalizedFormat format) {
    super(format);
  }

  /**
   * @param format of the exception
   * @param args for the format
   */
  public TranslatableCommandErrorException(LocalizedFormat format, Localizable... args) {
    super(format, args);
  }

  @Override
  public TextStyle getStyle() {
    return Commands.ERROR;
  }
}
