package network.walrus.utils.bukkit.visual.hologram;

import java.util.ArrayList;
import java.util.List;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * A Hologram is made up of one or more lines, which appear as floating name tags. Keep in mind that
 * the location of a Hologram represents the top-middle point. Also, remember that Holograms will
 * always be perpendicular to the player's view.
 *
 * @author Dalton Smith
 */
public class Hologram {

  private List<HologramLine> lines;
  private Location location;
  private double spaceBetweenLines;

  /**
   * Constructor.
   *
   * @param location to place the hologram at in the world
   * @param spaceBetweenLines space between hologram lines
   */
  public Hologram(Location location, double spaceBetweenLines) {
    lines = new ArrayList<>();
    this.location = location;
    this.location.add(0, -2.5, 0);
    this.spaceBetweenLines = spaceBetweenLines;
  }

  /**
   * Constructor.
   *
   * @param location to place the hologram at in the world
   * @param lines to put on display
   * @param spaceBetweenLines space between hologram lines
   */
  public Hologram(Location location, List<Localizable> lines, double spaceBetweenLines) {
    List<HologramLine> list = new ArrayList<>();
    for (Localizable line : lines) {
      HologramLine hologramLine = toLine(line);
      list.add(hologramLine);
    }
    this.lines = list;
    this.location = location;
    this.location.add(0, -2.5, 0);
    this.spaceBetweenLines = spaceBetweenLines;
  }

  /**
   * Appends a new HologramLine to the end of the Hologram.
   *
   * @param line Localizable to add to the hologram
   */
  public void appendLine(Localizable line) {
    lines.add(new HologramLine(line, this));
  }

  /**
   * Inserts a HologramLine at the given index.
   *
   * @param index index at which the new line will be added
   * @param line Localizable to add to the hologram
   */
  public void insertLine(int index, Localizable line) {
    lines.add(index, new HologramLine(line, this));
  }

  /**
   * Sets the line at the given index to the given Localizable
   *
   * @param index index of the HologramLine to set
   * @param line Localizable to set the HologramLine to
   */
  public void setLine(int index, Localizable line) {
    lines.get(index).text(line);
  }

  /**
   * Removes a line from the Hologram at the given index.
   *
   * @param index index of the HologramLine to be removed
   * @return the HologramLine that was removed
   */
  public HologramLine removeLine(int index) {
    return lines.remove(index);
  }

  /** Clears all lines from the Hologram */
  public void clearLines() {
    lines.clear();
  }

  /**
   * Returns the HologramLine at the given index.
   *
   * @param index index of the HologramLine to be returned
   * @return the Hologram line at the provided index
   */
  public HologramLine getLine(int index) {
    return lines.get(index);
  }

  /**
   * Returns the current size of this Hologram.
   *
   * @return the number of lines the Hologram currently holds
   */
  public int size() {
    return lines.size();
  }

  /**
   * Returns the x coordinate of this Hologram.
   *
   * @return X coordinate of this Hologram
   */
  public int getX() {
    return location.getBlockX();
  }

  /**
   * Returns the y coordinate of this Hologram.
   *
   * @return Y coordinate of this Hologram
   */
  public int getY() {
    return location.getBlockY();
  }

  /**
   * Returns the z coordinate of this Hologram.
   *
   * @return Z coordinate of this Hologram
   */
  public int getZ() {
    return location.getBlockZ();
  }

  /**
   * Returns the world in which this Hologram is located.
   *
   * @return the world this Hologram is currently in
   */
  public World world() {
    return location.getWorld();
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  /**
   * Provides the location of this Hologram
   *
   * @return the location of the Hologram
   */
  public Location location() {
    return location;
  }

  /** Spawns entities for each line in this Hologram. */
  public void spawnEntities(Player player) {
    despawnEntities(player);

    boolean first = true;
    double currentY = getY();

    for (HologramLine line : lines) {
      currentY -= line.height();

      if (first) {
        first = false;
      } else {
        currentY -= spaceBetweenLines;
      }

      line.spawn(world(), player, getX(), currentY, getZ());
    }
  }

  /** Despawns all entities for this Hologram */
  public void despawnEntities(Player player) {
    for (HologramLine line : lines) {
      line.despawn(player);
    }
  }

  private HologramLine toLine(Localizable line) {
    return new HologramLine(line, this);
  }
}
