package network.walrus.games.core.facets.applicators;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterCache;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Applicators;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * An object which is used to "apply" various objects to a specific {@link Region}.
 *
 * @author Austin Mayes
 */
public class Applicator {

  private static final Cache<UUID, Applicator> ATTEMPT_CACHE =
      CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();
  private final Region region;
  private final Optional<Filter> enter;
  private final Optional<Filter> leave;
  private final FilterCache<Player> enterCache = new FilterCache<>();
  private final FilterCache<Player> leaveCache = new FilterCache<>();
  private final Optional<Filter> blockPlace;
  private final Optional<Filter> blockBreak;
  private final Optional<Filter> use;
  private final Optional<LocalizedConfigurationProperty> message;
  private final Optional<Vector> velocity;
  private final Optional<Kit> kit;

  /**
   * @param region this apply affects
   * @param enter that enter events should be filtered using
   * @param leave that leave events should be filtered using
   * @param blockPlace that block place events should be filtered using
   * @param blockBreak that block break events should be filtered using
   * @param use that use events should be filtered using
   * @param message to be shown to players when filters block events
   * @param velocity to be applied to players who enter the region
   * @param kit to be given to players who enter the region
   */
  public Applicator(
      Region region,
      Optional<Filter> enter,
      Optional<Filter> leave,
      Optional<Filter> blockPlace,
      Optional<Filter> blockBreak,
      Optional<Filter> use,
      Optional<LocalizedConfigurationProperty> message,
      Optional<Vector> velocity,
      Optional<Kit> kit) {
    this.region = region;
    this.enter = enter;
    this.leave = leave;
    this.blockPlace = blockPlace;
    this.blockBreak = blockBreak;
    this.use = use;
    this.message = message;
    this.velocity = velocity;
    this.kit = kit;
  }

  /**
   * Send a player this applicator's fail message if it has one.
   *
   * @param player to send the message to
   */
  public void message(Player player) {
    this.message.ifPresent(
        m -> {
          if (ATTEMPT_CACHE.getIfPresent(player.getUniqueId()) != null) {
            return;
          }
          player.sendMessage(m.toText(Games.Applicators.EVENT_DISALLOWED));
          Applicators.EVENT_CANCELED.play(player);
          ATTEMPT_CACHE.put(player.getUniqueId(), this);
        });
  }

  /**
   * Perform necessary actions needed when a player enters the applicator region
   *
   * @param player who is entering the region
   */
  void onEnter(Player player) {
    velocity.ifPresent(player::setVelocity);
    kit.ifPresent(k -> k.apply(player));
  }

  public Region getRegion() {
    return region;
  }

  public Optional<Filter> getEnter() {
    return enter;
  }

  public Optional<Filter> getLeave() {
    return leave;
  }

  public FilterCache<Player> getEnterCache() {
    return enterCache;
  }

  public FilterCache<Player> getLeaveCache() {
    return leaveCache;
  }

  public Optional<Filter> getBlockPlace() {
    return blockPlace;
  }

  public Optional<Filter> getBlockBreak() {
    return blockBreak;
  }

  public Optional<Filter> getUse() {
    return use;
  }

  public Optional<Vector> getVelocity() {
    return velocity;
  }

  public Optional<Kit> getKit() {
    return kit;
  }
}
