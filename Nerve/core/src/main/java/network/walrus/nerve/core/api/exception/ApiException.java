package network.walrus.nerve.core.api.exception;

import com.apollographql.apollo.api.Error;
import java.util.List;

/**
 * Thrown when an API call has an exceptional response.
 *
 * @author Austin Mayes
 */
public class ApiException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message which provides more info about why the exception occurred
   */
  public ApiException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message which provides more info about why the exception occurred
   * @param cause of the base error
   */
  public ApiException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param cause of the base error
   */
  public ApiException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor.
   *
   * @param errors API errors
   */
  public ApiException(List<Error> errors) {
    super(flattenErrors(errors));
  }

  private static String flattenErrors(List<Error> errors) {
    StringBuilder flatError = new StringBuilder();
    flatError.append("API had following errors: ");
    for (Error error : errors) {
      flatError.append(error.message() + "\n");
    }

    return flatError.toString();
  }
}
