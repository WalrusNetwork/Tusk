package network.walrus.ubiquitous.bukkit.lobby.facets.parkour;

import java.util.Set;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.bukkit.visual.hologram.HologramManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A single stage of a {@link Parkour}.
 *
 * @author Austin Mayes
 */
public class ParkourStage {

  private final String id;
  private final LocalizedConfigurationProperty name;
  private final BoundedRegion bounds;
  private final BoundedRegion start;
  private final BoundedRegion end;
  private final Set<PotionEffectType> effects;
  private final double completionReward;

  /**
   * @param id used for persistent storage
   * @param name shown to players
   * @param bounds used to determine the area players should be in to not be considered a failure
   * @param start point of this stage
   * @param end point of this stage
   * @param effects applied to players when they are in this stage
   * @param completionReward given to players who complete this stage
   */
  public ParkourStage(
      String id,
      LocalizedConfigurationProperty name,
      BoundedRegion bounds,
      BoundedRegion start,
      BoundedRegion end,
      Set<PotionEffectType> effects,
      double completionReward) {
    this.id = id;
    this.name = name;
    this.bounds = bounds;
    this.start = start;
    this.end = end;
    this.effects = effects;
    this.completionReward = completionReward;
  }

  /**
   * Register holograms describing this stage.
   *
   * @param manager to register the holograms with
   * @param world to spawn the holograms in
   */
  public void registerStands(HologramManager manager, World world) {
    // TODO: Holograms for start/end and checkpoints
  }

  /**
   * Mark a player as currently in this stage.
   *
   * @param player who is entering the stage
   */
  public void enter(Player player) {
    for (PotionEffectType e : effects) {
      player.addPotionEffect(new PotionEffect(e, Integer.MAX_VALUE, 1, true, false));
    }
  }

  /**
   * Mark a player as no longer being in this stage for any reason.
   *
   * @param player who is leaving the stage
   */
  public void leave(Player player) {
    for (PotionEffectType effect : effects) {
      player.removePotionEffect(effect);
    }
  }

  public BoundedRegion getStart() {
    return start;
  }

  public BoundedRegion getEnd() {
    return end;
  }

  /**
   * Determine if the specified {@link Location} is out of bounds for this parkour.
   *
   * @param location to check
   * @return if the location is out of bounds
   */
  public boolean isOutOfBounds(Location location) {
    return !bounds.contains(location);
  }
}
