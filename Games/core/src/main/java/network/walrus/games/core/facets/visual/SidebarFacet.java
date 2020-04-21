package network.walrus.games.core.facets.visual;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import network.walrus.games.core.events.competitor.PlayerChangeCompetitorEvent;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.GameTask;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.bukkit.visual.Sidebar;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.Renderable;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Main class responsible for showing {@link Sidebar}s to players and assigning them the correct
 * {@link DisplayPane} when they change groups.
 *
 * @author Austin Mayes
 */
public class SidebarFacet extends Facet implements Listener {

  public static BiFunction<DisplayManager, Optional<Competitor>, PaneGroup> PANE_CREATE_FUNCTION;
  public final DisplayManager displayManager;
  private final GameRound holder;
  private final HashMap<Competitor, PaneGroup> panes = Maps.newHashMap();
  private final Multimap<PaneGroup, UUID> currentPanes = HashMultimap.create();
  private final GameTask tickTask;
  private final Set<String> updateOnTick;
  private final GameTask alternator;
  private PaneGroup spectatorsPane;

  /** @param holder that this facet is operating inside of */
  public SidebarFacet(FacetHolder holder) {
    this.holder = (GameRound) holder;
    this.displayManager = UbiquitousBukkitPlugin.getInstance().getDisplayManager();
    this.updateOnTick = Sets.newHashSet();
    this.updateOnTick.add("game-time");

    this.tickTask =
        GameTask.of(
            "Sidebar tick",
            () -> {
              for (String s : updateOnTick) {
                displayManager.update(s);
              }
            });

    this.alternator =
        GameTask.of(
            "Sidebar alternate",
            () -> {
              for (PaneGroup pane : panes.values()) {
                pane.checkAlternate(this.displayManager);
              }

              if (spectatorsPane != null) spectatorsPane.checkAlternate(this.displayManager);
            });
  }

  /** Need to update bars a tick later since other facets switch players to a new scoreboard. */
  @EventHandler(priority = EventPriority.MONITOR)
  public void init(RoundOpenEvent event) {
    GameTask.of(
            "Sidebar reset",
            () -> {
              displayManager.clearBars();
              for (Player player : Bukkit.getOnlinePlayers()) {
                displayManager.update(player);
              }
            })
        .later(20);
  }

  @Override
  public void enable() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      displayManager.update(player);
    }
    alternator.repeat(5, 20);
    this.tickTask.repeatAsync(0, 1);
  }

  @Override
  public void disable() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      displayManager.update(player);
    }
    alternator.reset();
    tickTask.reset();
  }

  /**
   * Have a {@link Renderable} updated every tick
   *
   * @param id to update each tick
   */
  public void updateOnTick(String id) {
    updateOnTick.add(id);
  }

  /** Assign players to the correct panes. */
  @EventHandler
  public void onGroupChange(PlayerChangedGroupEvent event) {
    if (event.getGroup().isSpectator()) {
      if (spectatorsPane == null) {
        spectatorsPane = createPane(Optional.empty());
      }

      transfer(event.getPlayer(), spectatorsPane);
    }
  }

  /** Assign players to the correct panes. */
  @EventHandler
  public void onCompChange(PlayerChangeCompetitorEvent event) {
    if (event.getCompetitorTo().isPresent()) {
      PaneGroup pane =
          panes.computeIfAbsent(event.getCompetitorTo().get(), c -> createPane(Optional.of(c)));
      transfer(event.getPlayer(), pane);
    }
  }

  /** Cleanup when a player quits. */
  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    PaneGroup current = getCurrent(event.getPlayer());
    if (current == null) {
      return;
    }

    current.removePlayer(event.getPlayer());
    currentPanes.remove(current, event.getPlayer().getUniqueId());
  }

  /**
   * Re-creates the pane used for the specified competitor.
   *
   * @param competitor whose pane should be re-created
   */
  public void recreatePane(Competitor competitor) {
    panes.put(competitor, createPane(Optional.of(competitor)));
  }

  /** Re-creates the pane used for spectators. */
  public void recreateSpectatorPane() {
    spectatorsPane = createPane(Optional.empty());
  }

  /**
   * Refreshes the player's pane with the spectator pane.
   *
   * @param player whose pane to update
   */
  public void refreshSpectatorPane(Player player) {
    if (spectatorsPane == null) {
      spectatorsPane = createPane(Optional.empty());
    }
    transfer(player, spectatorsPane);
  }

  /**
   * Refreshes the player's pane with the pane for the specified competitor.
   *
   * @param player whose pane to update
   * @param competitor for which the pane should be calculated
   */
  public void refreshPane(Player player, Competitor competitor) {
    transfer(player, panes.computeIfAbsent(competitor, c -> createPane(Optional.of(c))));
  }

  /**
   * Get the current group that a player is using
   *
   * @param player to get the group for
   * @return group the player is using
   */
  public PaneGroup getCurrent(Player player) {
    return getCurrent(player.getUniqueId());
  }

  /**
   * Get the current group that a player is using
   *
   * @param uuid to get the group for
   * @return group the player is using
   */
  public PaneGroup getCurrent(UUID uuid) {
    for (Entry<PaneGroup, UUID> entry : currentPanes.entries()) {
      if (entry.getValue().equals(uuid)) {
        return entry.getKey();
      }
    }
    return null;
  }

  private void transfer(Player player, PaneGroup newGroup) {
    if (currentPanes.get(newGroup).contains(player.getUniqueId())) return;

    PaneGroup current = getCurrent(player);

    if (current != null) {
      current.transfer(player, newGroup, this.displayManager);
    } else {
      newGroup.setDefaults(player, displayManager);
    }
    currentPanes.get(current).remove(player.getUniqueId());
    currentPanes.put(newGroup, player.getUniqueId());
  }

  private PaneGroup createPane(Optional<Competitor> competitor) {
    if (PANE_CREATE_FUNCTION == null) {
      throw new RuntimeException("Tried to create a pane with no pane creation function.");
    }

    return PANE_CREATE_FUNCTION.apply(displayManager, competitor);
  }

  /** @see DisplayManager#update(String) */
  public void update(String id) {
    displayManager.update(id);
  }
}
