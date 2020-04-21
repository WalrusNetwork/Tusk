package network.walrus.ubiquitous.bukkit.display;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import network.walrus.ubiquitous.bukkit.task.BetterRunnable;
import network.walrus.utils.bukkit.visual.Sidebar;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.Renderable;
import network.walrus.utils.bukkit.visual.renderable.animated.AnimatedRenderable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Main object responsible for handling {@link Sidebar} management for all players on the server.
 *
 * <p>This is also responsible for delegating a different {@link Scoreboard} for each player, and
 * handles mass updates. This also handles {@link DisplayPane} allocation for each player, and adds
 * support for a global {@link DisplayPane}.
 *
 * @author Austin Mayes
 */
public class DisplayManagerImpl implements Listener, DisplayManager {

  private static Map<UUID, Sidebar> playerScoreboards = new HashMap<>();
  private final PluginManager manager;
  private final Plugin plugin;
  private DisplayPane globalFrame = null;
  private Multimap<DisplayPane, Player> playerFrames = HashMultimap.create();
  private Map<Renderable, BukkitTask> animatedElement = new HashMap<>();
  private Multimap<String, Renderable> elementsById = HashMultimap.create();

  /**
   * Constructor.
   *
   * @param manager used to register events
   * @param plugin which owns this manager
   */
  public DisplayManagerImpl(PluginManager manager, Plugin plugin) {
    this.manager = manager;
    this.plugin = plugin;
  }

  @Override
  public void init() {
    manager.registerEvents(new InternalListener(), plugin);
  }

  @Override
  public Plugin owner() {
    return plugin;
  }

  @Override
  public void setFrame(DisplayPane frame) {
    Collection<Player> onGlobal = playerFrames.removeAll(globalFrame);
    if (globalFrame != null) {
      globalFrame.purgeCache();
    }
    globalFrame = frame;
    playerFrames.putAll(frame, onGlobal);
    updateElements(frame);
  }

  @Override
  public void updateCache(Player player) {
    DisplayPane frame = getPlayerFrame(player);

    if (frame == null) {
      return;
    }

    frame.purgeCache(player);
  }

  @Override
  public void updateElements(DisplayPane frame) {
    frame.purgeCache();
    for (Renderable element : frame.getElements()) {
      elementsById.put(element.id(), element);

      if (element instanceof AnimatedRenderable && !animatedElement.containsKey(element)) {
        animatedElement.put(
            element,
            ((BetterRunnable)
                    () -> {
                      ((AnimatedRenderable) element).incrementFrame();
                      update(element.id());
                    })
                .runTaskTimer(
                    ((AnimatedRenderable) element).delay(),
                    ((AnimatedRenderable) element).delay(),
                    "animation-" + element.id()));
      }
    }
    for (Player player : playerFrames.get(frame)) {
      frame.render(player);
    }
  }

  @Override
  public void update(String id) {
    if (elementsById.containsKey(id)) {
      for (Entry<DisplayPane, Player> d : playerFrames.entries()) {
        d.getKey().purgeCache(id, d.getValue());
        d.getKey().render(d.getValue());
      }
    }
  }

  @Override
  public void update(Player player) {
    DisplayPane frame = getPlayerFrame(player);
    if (frame != null) {
      frame.purgeCache(player);
      frame.render(player);
    }
  }

  @Override
  public void update(Player player, String id) {
    DisplayPane frame = getPlayerFrame(player);
    if (frame != null) {
      frame.purgeCache(id, player);
      frame.render(player);
    }
  }

  @Override
  public DisplayPane getPlayerFrame(Player player) {
    for (Entry<DisplayPane, Player> c : playerFrames.entries()) {
      if (c.getValue().getUniqueId().equals(player.getUniqueId())) {
        return c.getKey();
      }
    }
    return globalFrame;
  }

  @Override
  public void setFrame(DisplayPane frame, Player player) {
    playerFrames.values().remove(player);
    if (frame != null) {
      playerFrames.put(frame, player);
    } else {
      playerFrames.put(globalFrame, player);
    }
    updateCache(player);
    updateElements(frame);
  }

  @Override
  public Sidebar getSidebar(Player player) {
    return playerScoreboards.computeIfAbsent(
        player.getUniqueId(),
        k -> {
          Sidebar sidebar = new Sidebar(player.getScoreboard());
          sidebar.addIP();
          return sidebar;
        });
  }

  @Override
  public void clearBars() {
    for (Sidebar value : playerScoreboards.values()) {
      value.destroy();
    }
    playerScoreboards.clear();
  }

  private class InternalListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
      // Ensure player is on a unique scoreboard
      if (event
          .getPlayer()
          .getScoreboard()
          .equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        event.getPlayer().setScoreboard(scoreboard);
      }
      if (globalFrame != null) {
        setFrame(globalFrame, event.getPlayer());
        updateCache(event.getPlayer());
        ((BetterRunnable) () -> update(event.getPlayer())).runTaskLater(3, "join-sidebar-update");
      }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
      event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
      playerScoreboards.remove(event.getPlayer().getUniqueId());
      playerFrames.remove(getPlayerFrame(event.getPlayer()), event.getPlayer());
    }
  }
}
