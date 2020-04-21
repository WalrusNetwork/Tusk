package network.walrus.utils.parsing.facet;

import network.walrus.utils.core.versioning.Version;

/**
 * History of all supported protos for games and lobbies.
 *
 * @author Austin Mayes
 */
public class ProtoVersionHistory {

  /** Initial Release */
  public static final Version RELEASE = new Version(2, 0, 0);
  /** Minimum proto supported for facet configurations. */
  public static final Version MIN_PROTO = RELEASE;
  /** Maximum proto supported for facet configurations. */
  public static final Version CURRENT = RELEASE;
}
