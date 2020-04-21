package network.walrus.games.core.api.spawns;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import java.util.Optional;
import java.util.Random;
import network.walrus.games.core.GamesPlugin;
import network.walrus.utils.bukkit.points.AngleProvider;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import org.bukkit.util.Vector;

/**
 * Wrapper class to represent a {@link BoundedRegion} with {@link AngleProvider}s for yaw and pitch.
 *
 * @author Avicus Network
 */
public class SpawnRegion {

  private final BoundedRegion region;
  private final Optional<AngleProvider> yaw;
  private final Optional<AngleProvider> pitch;
  private final Timing randomPositionTimer;

  /**
   * Constructor.
   *
   * @param region which this object represents
   * @param yaw provider used to generate yaw values for spawn positions. If empty, the spawn's base
   *     provider will be used to generate values.
   * @param pitch provider used to generate pitch values for spawn positions. If empty, the spawn's
   *     base provider will be used to generate values.
   */
  public SpawnRegion(
      BoundedRegion region, Optional<AngleProvider> yaw, Optional<AngleProvider> pitch) {
    this.region = region;
    this.yaw = yaw;
    this.pitch = pitch;
    this.randomPositionTimer =
        Timings.of(
            GamesPlugin.instance,
            "Spawn region random position (" + this.region.getClass().getSimpleName() + ")");
  }

  /**
   * @throws PositionUnavailableException if the region doesn't generate random positions
   * @see BoundedRegion#getRandomPosition(Random)
   */
  public Vector randomPosition(Random random) throws PositionUnavailableException {
    try (Timing t = randomPositionTimer.startClosable()) {
      return this.region.getRandomPosition(random);
    }
  }

  public Vector getCenter() {
    return this.region.getCenter();
  }

  public BoundedRegion getRegion() {
    return region;
  }

  public Optional<AngleProvider> getYaw() {
    return yaw;
  }

  public Optional<AngleProvider> getPitch() {
    return pitch;
  }
}
