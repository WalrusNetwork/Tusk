package network.walrus.games.core.facets.portals;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.bukkit.points.AngleProvider;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.bukkit.region.modifiers.TranslateRegion;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Portals;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

/**
 * An object which moves players from one position to another inside of a {@link org.bukkit.World}.
 *
 * @author Austin Mayes
 */
public class Portal {

  private final Set<UUID> recentUses = Sets.newHashSet();
  private final Random RANDOM = new Random();
  private final Region region;
  private final Region destination;
  private final Optional<Filter> filter;
  private final boolean observers;
  private final Optional<AngleProvider> yaw;
  private final Optional<AngleProvider> pitch;
  private final boolean bidirectional;
  private final boolean sound;
  private final boolean smooth;

  /**
   * @param region where the portal can be entered
   * @param destination that the portal outputs to
   * @param filter used to check if the portal should act on players
   * @param observers if observers can use this portal
   * @param yaw provider used to set the player's yaw when they are teleported
   * @param pitch provider used to set the player's pitch when they are teleported
   * @param bidirectional if the portal can be used from both the source and destination
   * @param sound if the teleport sound should be sent to all surrounding players
   * @param smooth if the player should be sent smoothly to the destination
   */
  Portal(
      Region region,
      Region destination,
      Optional<Filter> filter,
      boolean observers,
      Optional<AngleProvider> yaw,
      Optional<AngleProvider> pitch,
      boolean bidirectional,
      boolean sound,
      boolean smooth) {
    this.region = region;
    this.destination = destination;
    this.filter = filter;
    this.observers = observers;
    this.yaw = yaw;
    this.pitch = pitch;
    this.bidirectional = bidirectional;
    this.sound = sound;
    this.smooth = smooth;
  }

  Region getRegion() {
    return region;
  }

  Region getDestination() {
    return destination;
  }

  private void send(FacetHolder holder, Player player, Direction direction)
      throws PositionUnavailableException {
    Location from = player.getLocation();

    // Determine where to send the player
    Vector dest;
    if (direction == Direction.FORWARD) {
      dest =
          destination instanceof TranslateRegion
              ? player
                  .getLocation()
                  .toVector()
                  .clone()
                  .add(((TranslateRegion) destination).offset())
              : destination.getRandomPosition(RANDOM);
    } else {
      dest =
          destination instanceof TranslateRegion
              // If relative, use inverse of the above
              ? player
                  .getLocation()
                  .toVector()
                  .clone()
                  .subtract(((TranslateRegion) destination).offset())
              // If not, just send them to somewhere in the source
              : region.getRandomPosition(RANDOM);
    }

    // Set new yaw/pitch for players, or fall back to current if providers are not present
    float yaw = this.yaw.map(y -> y.getAngle(dest)).orElse(from.getYaw());
    float pitch = this.pitch.map(y -> y.getAngle(dest)).orElse(from.getPitch());

    // Sound check #1
    if (sound) {
      // Only play sound for surrounding players since player will be gone
      for (Player listener : holder.players()) {
        if (!player.equals(listener) && listener.canSee(player)) {
          Portals.OTHER.play(listener, player.getLocation());
        }
      }
    }

    // Mark that the player is being teleported
    recentUses.add(player.getUniqueId());

    // Smooth teleports use the relative API
    if (smooth) {
      // Even though we are using the relative API, we still need to set an exact position
      player.teleportRelative(
          dest.clone().subtract(from.toVector()),
          yaw - from.getYaw(),
          pitch - from.getPitch(),
          PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
    }
    // Not smooth, use default TP api
    else {
      player.teleport(
          dest.toLocation(player.getWorld(), yaw, pitch),
          PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
    }

    // Reset fall distance
    player.setFallDistance(0);

    // Sound check #2
    if (sound) {
      // Play for player this time since they are here
      for (Player listener : holder.players()) {
        if (listener.canSee(player)) {
          if (player.getUniqueId().equals(listener.getUniqueId())) {
            Portals.SELF.play(listener);
          } else {
            Portals.OTHER.play(listener, player.getLocation());
          }
        }
      }
    }

    // Player has been teleported, remove them from recents
    recentUses.remove(player.getUniqueId());
  }

  /**
   * Attempt to teleport a player using this portal.
   *
   * @param holder that the portal is acting in
   * @param event which is being checked
   * @return if the player was teleported to the location
   */
  boolean attemptTeleport(FacetHolder holder, PlayerCoarseMoveEvent event) {
    // Player was recently teleported, ignore
    if (recentUses.contains(event.getPlayer().getUniqueId())) {
      return false;
    }

    Location from = event.getFrom();
    Location to = event.getTo();

    // Is the player entering the source region
    boolean enteringSource = region.contains(to) && !region.contains(from);
    // Only entering the exit region in this case if bi-direction support is enabled
    boolean enteringDest = bidirectional && destination.contains(to) && !destination.contains(from);
    boolean in = enteringSource || enteringDest;

    // Not even going into any transport region
    if (!in) {
      return false;
    }

    // By this point we know if the portal supports two-way directions
    Direction direction = enteringSource ? Direction.FORWARD : Direction.BACK;

    GroupsManager manager = holder.getFacetRequired(GroupsManager.class);

    // Block observers if they can't use this portal
    if (!observers && manager.isObserving(event.getPlayer())) {
      return false;
    }

    // Check filter, only if one is defined
    if (this.filter.isPresent()) {
      FilterContext context = new FilterContext();
      context.add(new PlayerVariable(event.getPlayer()));
      if (this.filter.get().test(context).fails()) {
        return false;
      }
    }
    // Attempt to teleport the player
    try {
      send(holder, event.getPlayer(), direction);
    } catch (PositionUnavailableException e) {
      // Couldn't find a random position, alert map devs and allow other portals to try
      GamesPlugin.instance
          .mapLogger()
          .warning(
              "Failed to generate portal random position for "
                  + event.getPlayer().getName()
                  + " heading "
                  + direction.name());
      return false;
    }

    // Everything worked
    return true;
  }

  enum Direction {
    FORWARD,
    BACK
  }
}
