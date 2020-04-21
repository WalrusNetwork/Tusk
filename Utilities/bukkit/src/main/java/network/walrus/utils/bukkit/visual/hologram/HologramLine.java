package network.walrus.utils.bukkit.visual.hologram;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import network.walrus.utils.bukkit.NMSUtils.FakeArmorStand;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Represents a single line of a {@link Hologram}. Default Bungee color codes can be applied to the
 * text of a HologramLine.
 *
 * @author Dalton Smith
 */
public class HologramLine {

  private final double HEIGHT = 0.23;
  private final Hologram parent;
  private Localizable text;
  private Map<UUID, FakeArmorStand> stands = Maps.newHashMap();

  /**
   * Constructor.
   *
   * @param text to render on the line
   * @param parent hologram which contains this line
   */
  public HologramLine(Localizable text, Hologram parent) {
    this.text = text;
    this.parent = parent;
  }

  /**
   * Returns the text this HologramLine contains.
   *
   * @return The text for this line
   */
  public Localizable text() {
    return text;
  }

  /**
   * Returns the {@link Hologram} this HologramLine belongs to.
   *
   * @return the parent {@link Hologram}
   */
  public Hologram parent() {
    return parent;
  }

  /**
   * Sets the text for this HologramLine to the given Localizable
   *
   * @param text Localizable to set text to
   */
  public void text(Localizable text) {
    this.text = text;
  }

  /**
   * Spawns the Line at the given location
   *
   * @param world world in which to spawn the Line
   * @param viewer viewer of the line
   * @param x x coordinate at which to spawn the Line
   * @param y y coordinate at which to spawn the Line
   * @param z z coordinate at which to spawn the Line
   */
  public void spawn(World world, Player viewer, double x, double y, double z) {
    FakeArmorStand entity =
        stands.computeIfAbsent(
            viewer.getUniqueId(), (v) -> new FakeArmorStand(world, text().toLegacyText(viewer)));
    entity.spawn(viewer, new Location(world, x, y, z));
  }

  /** Removes the Entity for this Line */
  public void despawn(Player player) {
    if (stands.containsKey(player.getUniqueId())) {
      stands.get(player.getUniqueId()).destroy(player);
      stands.remove(player.getUniqueId());
    }
  }

  /**
   * Returns the height of the line
   *
   * @return Default line height of 0.23 blocks
   */
  public double height() {
    return HEIGHT;
  }
}
