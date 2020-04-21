package network.walrus.utils.parsing.world;

import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * Wrapper object to represent something that exists for a specific {@link WorldSource}.
 *
 * @author Austin Mayes
 */
public interface Sourced {

  /**
   * Get the name which should be used in the UI. This is intentionally not localized since names
   * should be consistent throughout.
   */
  String name();

  /** The slugified name used for internal use. */
  String slugify();

  /** Source that this object was created from. */
  WorldSource source();
}
