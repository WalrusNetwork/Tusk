package network.walrus.utils.parsing.facet;

import javax.annotation.Nullable;

/**
 * Thrown for facet-related problems that cannot be detected until holder load time.
 *
 * @author Austin Mayes
 */
public class FacetLoadException extends Exception {

  private @Nullable Class<?> module;

  /**
   * Constructor.
   *
   * @param module which is throwing the exception
   * @param message that explains the exceptions
   * @param cause of the base exception
   */
  public FacetLoadException(@Nullable Class<?> module, String message, Throwable cause) {
    super(message, cause);
    this.module = module;
  }

  /**
   * Constructor.
   *
   * @param module which is throwing the exception
   * @param message that explains the exceptions
   */
  public FacetLoadException(@Nullable Class<?> module, String message) {
    this(module, message, null);
  }

  /**
   * Constructor.
   *
   * @param module which is throwing the exception
   */
  public FacetLoadException(@Nullable Class<?> module) {
    this(module, null);
  }

  /** @return the facet which this exception is for */
  public @Nullable Class<?> module() {
    return module;
  }
}
