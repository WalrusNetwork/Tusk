package network.walrus.utils.core.command.exception;

import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.translation.Messages;
import network.walrus.utils.core.util.Paginator;

/**
 * An exception that is thrown when a user supplies an invalid page to a paginated command result.
 *
 * @author Austin Mayes
 */
public class InvalidPaginationPageException extends TranslatableCommandErrorException {

  /** @param paginator which caused the exception */
  public InvalidPaginationPageException(Paginator<?> paginator) {
    super(Messages.ERRORS_INVALID_PAGE, new LocalizedNumber(paginator.getPageCount()));
  }
}
