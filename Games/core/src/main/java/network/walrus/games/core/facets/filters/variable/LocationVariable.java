package network.walrus.games.core.facets.filters.variable;

import network.walrus.games.core.facets.filters.Variable;
import org.bukkit.Location;

/**
 * The location variable contains information about the location in the world where the action is
 * taking place.
 *
 * @author Avicus Network
 */
public class LocationVariable implements Variable {

  private final Location location;

  /**
   * Constructor.
   *
   * @param location of the object
   */
  public LocationVariable(Location location) {
    this.location = location;
  }

  public Location getLocation() {
    return location;
  }
}
