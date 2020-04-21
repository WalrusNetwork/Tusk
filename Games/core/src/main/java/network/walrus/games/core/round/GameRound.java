package network.walrus.games.core.round;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.events.round.RoundCloseEvent;
import network.walrus.games.core.events.round.RoundLoadEvent;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.games.core.round.states.Stateful;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.core.util.GameTask;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.world.MapSourceWorldProvider;
import network.walrus.utils.parsing.world.PlayerContainer;
import network.walrus.utils.parsing.world.WorldProvider;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A single round of a specific {@link GameMap}. These are created by the map objects themselves,
 * and only serve the purpose of handling the data for the single round.
 *
 * <p>These hold parsed {@link Facet}s which add features to the map. These facets only live as long
 * as the round object does.
 *
 * @author Austin Mayes
 */
public abstract class GameRound extends FacetHolder implements Stateful {

  private final GameMap map;
  private RoundState state;
  private Instant start;
  private Instant end;

  /**
   * Constructor.
   *
   * @param map which this round is for
   */
  public GameRound(GameMap map) {
    this(
        map,
        GamesPlugin.instance,
        GamesPlugin.instance.mapLogger(),
        map,
        new MapSourceWorldProvider(
            map.source(), "rounds/round-" + UUID.randomUUID().toString().substring(0, 5)));
  }

  /**
   * @param map which this round is for
   * @param plugin to register commands and listeners with
   * @param logger to log errors and info to
   * @param source which this holder is for
   * @param worldProvider used to create and load the world
   */
  public GameRound(
      GameMap map,
      Plugin plugin,
      Logger logger,
      FacetConfigurationSource source,
      WorldProvider<? extends PlayerContainer> worldProvider) {
    super(plugin, logger, source, worldProvider);
    this.map = map;
    setState(RoundState.IDLE);
    if (getWorldProvider() instanceof MapSourceWorldProvider) {
      ((MapSourceWorldProvider) getWorldProvider())
          .setCreationCallback((c) -> EventUtil.call(new RoundLoadEvent(this, c)));
    }
  }

  /** @return the map that this round is playing on */
  public GameMap map() {
    return this.map;
  }

  /** End the game. */
  public void end() {
    setState(RoundState.FINISHED);
    if (GamesPlugin.instance.getConfig().getBoolean("shutdown-after-end", true)) {
      GameTask.of("Round end shutdown", Bukkit::shutdown).later(20 * 20);
    }
  }

  @Override
  public RoundState setState(RoundState state) {
    if (state.playing() && start == null) {
      start = Instant.now();
    } else if (state.finished() && end == null) {
      end = Instant.now();
    }

    RoundState current = getState();
    this.state = state;
    EventUtil.call(
        new RoundStateChangeEvent(this, Optional.ofNullable(current), Optional.ofNullable(state)));
    return current;
  }

  public Duration getPlayingDuration() {
    if (start != null) {
      return Duration.between(start, end == null ? Instant.now() : end);
    }
    return Duration.ZERO;
  }

  @Override
  public void loadFacets() {
    super.loadFacets();
    EventUtil.call(new RoundOpenEvent(this));
  }

  @Override
  public void unloadFacets() {
    EventUtil.call(new RoundCloseEvent(this));
    super.unloadFacets();
  }

  /** @see GroupsManager#playingPlayers() */
  public Set<Player> playingPlayers() {
    return getFacetRequired(GroupsManager.class).playingPlayers();
  }

  @Override
  public RoundState getState() {
    return this.state;
  }
}
