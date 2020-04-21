package network.walrus.utils.core.parse.named;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a method used to parse something referenced by string.
 *
 * @author Avicus Network
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NamedParser {

  /** @return identification key representing what the linked method should parse */
  String[] value();
}
