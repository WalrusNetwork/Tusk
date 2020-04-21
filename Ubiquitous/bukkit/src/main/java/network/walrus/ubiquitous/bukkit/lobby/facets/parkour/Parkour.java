package network.walrus.ubiquitous.bukkit.lobby.facets.parkour;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.bukkit.visual.hologram.HologramManager;
import network.walrus.utils.core.color.NetworkColorConstants.Commands;
import network.walrus.utils.core.translation.Messages;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * A set of stages which {@link Player}s must complete in a specified order.
 *
 * <p>The {@link #enter(Player)}, {@link #leave(Player)}, {@link #advance(Player)}, and {@link
 * #failed(Player)} callbacks should exclusively be used when interacting with players so that all
 * data mappings remain up to date and so that stage-specific callbacks are executed in the correct
 * order.
 *
 * <p>{@link Player}s can only be in a single {@link ParkourStage} at once, and will always be
 * removed from their current one before they are added to the next one during advancement.
 *
 * @author Austin Mayes
 */
public class Parkour {

  private static final Random RANDOM = new Random();

  private final String id;
  private final LocalizedConfigurationProperty name;
  private final List<ParkourStage> stages;
  private final boolean multipleCompletions;
  private final RespawnPolicy respawnPolicy;

  private final Map<UUID, Integer> currentStages = Maps.newHashMap();

  /**
   * @param id used for persistent storage
   * @param name shown to players
   * @param stages that need to be completed, in order
   * @param multipleCompletions if the parkour can be completed again if a player has already
   *     completed it
   * @param respawnPolicy policy used to determine where players should resoawn when they fail
   */
  public Parkour(
      String id,
      LocalizedConfigurationProperty name,
      List<ParkourStage> stages,
      boolean multipleCompletions,
      RespawnPolicy respawnPolicy) {
    this.id = id;
    this.name = name;
    this.stages = stages;
    this.multipleCompletions = multipleCompletions;
    this.respawnPolicy = respawnPolicy;
  }

  /**
   * Register holograms describing the parkour.
   *
   * @param manager to register the holograms with
   * @param world to spawn the holograms in
   */
  public void registerStands(HologramManager manager, World world) {
    // TODO: Holograms for start and end
    stages.forEach(s -> s.registerStands(manager, world));
  }

  /** @return the first stage's starting point */
  public BoundedRegion entrance() {
    return stage(0).getStart();
  }

  /**
   * Mark a player as being in this parkour.
   *
   * @param player who is entering the parkour
   */
  public void enter(Player player) {
    if (isParticipating(player))
      throw new IllegalStateException("Player is already participating in this parkour!");

    // TODO: API
    if (false && !multipleCompletions) {
      // Can't use message
      return;
    }

    setStage(player, 0);
  }

  /**
   * Remove a player from this parkour.
   *
   * @param player who is leaving
   */
  public void leave(Player player) {
    if (!isParticipating(player))
      throw new IllegalStateException("Player is not participating in this parkour!");

    getStage(player).leave(player);
    currentStages.remove(player.getUniqueId());
  }

  /**
   * Move a player to the next stage of the parkour.
   *
   * <p>If the player is on the last stage, they will be removed from the parkour.
   *
   * @param player to advance
   */
  public void advance(Player player) {
    int nextIndex = currentStages.get(player.getUniqueId()) + 1;
    if (nextIndex > stages.size() - 1) {
      leave(player);
      return;
    }
    setStage(player, nextIndex);
  }

  private void setStage(Player player, int stage) {
    ParkourStage current = getStage(player);
    if (current != null) current.leave(player);
    this.currentStages.put(player.getUniqueId(), stage);
    stage(stage).enter(player);
  }

  /**
   * Callback executed when a player falls out of bounds while participating in this parkour.
   *
   * <p>This will respawn them in the appropriate location based on the configured {@link
   * RespawnPolicy}.
   *
   * @param player who failed
   */
  public void failed(Player player) {
    if (!isParticipating(player))
      throw new IllegalStateException("Player is not participating in this parkour!");

    switch (respawnPolicy) {
      case START:
        teleport(player, stage(0).getStart());
        break;
      case LAST_STAGE:
        teleport(player, getStage(player).getStart());
        break;
    }
  }

  /**
   * Determine if a player is currently participating in this parkour.
   *
   * @param player to check
   * @return if the player is participating
   */
  public boolean isParticipating(Player player) {
    return currentStages.containsKey(player.getUniqueId());
  }

  private ParkourStage stage(int index) {
    Validate.isTrue(index >= 0 && index <= stages.size() - 1, "Stage out of bounds");
    return stages.get(index);
  }

  /**
   * Get the stage of the parkour that a player is currently at.
   *
   * @param player to get stage for
   * @return the current stage that the specified player is at, or {@code null} if the player is not
   *     participating currently
   */
  public ParkourStage getStage(Player player) {
    if (!isParticipating(player)) return null;

    return stage(currentStages.get(player.getUniqueId()));
  }

  protected void teleport(Player player, BoundedRegion region) {
    try {
      player.teleport(region.getRandomPosition(RANDOM).toLocation(player.getWorld()));
    } catch (PositionUnavailableException e) {
      player.sendMessage(Messages.INTERNAL_ERROR.with(Commands.ERROR));
      e.printStackTrace();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Parkour parkour = (Parkour) o;
    return Objects.equal(id, parkour.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  /** Policy used to determine where players will be placed upon failure. */
  public enum RespawnPolicy {
    LAST_STAGE,
    START
  }
}
