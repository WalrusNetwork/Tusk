package network.walrus.duels;

import java.util.function.Function;
import network.walrus.utils.core.translation.Localizable;

/**
 * All of the types that {@link Duel}s can be.
 *
 * @author Austin Mayes
 */
public interface ArenaType {

  /** Get the function that should be used to create a {@link Duel} of this type. */
  Function<ArenaProperties, Duel> creationFunction();

  /**
   * The name of this arena type, used for UI. This is intentionally not localized since all arena
   * types should be uniform across locales.
   */
  String name();

  /** The description of this arena type. Used in the UI. */
  Localizable[] description();

  /**
   * Determine if users are allowed to spectate duels of this type while they are running. If this
   * is false, non-staff users will not be able to join this arena while the duel is in progress.
   */
  boolean allowsSpectators();

  /**
   * Unique slug of this arena type, for internal use. This is used for backend services such as
   * storage and messaging, and should not change.
   */
  String slug();

  /**
   * The team size of each team of this arena type. Max players is calculated by multiplying this
   * number by 2.
   */
  int teamzSize();
}
