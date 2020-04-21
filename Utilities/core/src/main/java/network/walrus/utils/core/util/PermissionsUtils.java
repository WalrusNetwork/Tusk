package network.walrus.utils.core.util;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Utilities for working with permissions.
 *
 * @author Austin Mayes
 */
public class PermissionsUtils {

  /**
   * Decode a user-defined permission string to a plugin-readable value.
   *
   * @param perm to decode
   * @return pair containing the raw permission and the desired value
   */
  public static Pair<String, Boolean> decodePermission(String perm) {
    if (perm.startsWith("-")) {
      return Pair.of(perm.split("-")[1], false);
    }

    return Pair.of(perm, true);
  }
}
