package network.walrus.games.core.facets.visual;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

enum DisplayMode {
  STATIC,
  ALTERNATE
}

/**
 * Simple container class holding a set of {@link DisplayPane}s which can be alternated through or
 * statically chosen based on player preference.
 *
 * @author Austin Mayes
 */
public class PaneGroup {

  private final Map<String, DisplayPane> byId = Maps.newLinkedHashMap();
  private final LinkedList<String> animationOrder = Lists.newLinkedList();
  private final Set<UUID> toAlternate = Sets.newHashSet();
  private final Map<UUID, String> staticPanes = Maps.newHashMap();
  private final Map<UUID, DisplayMode> alternatePreferences = Maps.newHashMap();
  private byte frame;
  private boolean alternateDefault = false;
  private int alternateLeft;

  /** @param panes which make up this group */
  public PaneGroup(Pair<String, DisplayPane>... panes) {
    for (Pair<String, DisplayPane> pane : panes) {
      register(pane.getLeft(), pane.getRight());
    }

    frame = (byte) (animationOrder.size() > 1 ? 1 : 0);
    alternateLeft = panes[0].getValue().getSecondsToShow();
  }

  private void register(String id, DisplayPane pane) {
    byId.put(id, pane);
    animationOrder.add(id);
  }

  /** @return if this pane should alternate by default */
  public boolean shouldAlternateByDefault() {
    return alternateDefault;
  }

  /** @param alternating if this facet should alternate by default */
  public void setAlternateDefault(boolean alternating) {
    this.alternateDefault = alternating;
  }

  /**
   * Update if a pane should alternate for a player based on current defaults.
   *
   * @param player to update the pane for
   * @param displayManager of the panes
   */
  public void refreshAlternating(Player player, DisplayManager displayManager) {
    if (alternatePreferences.containsKey(player)) {
      return;
    }

    if (shouldAlternateByDefault()) {
      startAlternating(player, displayManager);
    } else {
      stopAlternating(player, displayManager);
    }
  }

  void checkAlternate(DisplayManager manager) {
    if (--alternateLeft == 0) {
      DisplayPane pane = byId.get(animationOrder.get(frame));
      alternateLeft = pane.getSecondsToShow();
      alternate(manager);
    }
  }

  /**
   * Alternate between display panes for all players which have them on
   *
   * @param manager used to send pane updates
   */
  void alternate(DisplayManager manager) {
    if (animationOrder.size() < 2) return;
    DisplayPane pane = byId.get(animationOrder.get(frame));
    for (UUID u : toAlternate) {
      Player p = Bukkit.getPlayer(u);
      if (p == null) continue;

      manager.setFrame(pane, p);
    }
    frame++;
    if (frame > animationOrder.size() - 1) frame = 0;
  }

  private void stopAlternating(Player player, DisplayManager manager) {
    toAlternate.remove(player.getUniqueId());
    setPane(player, animationOrder.get(frame), manager);
  }

  private void startAlternating(Player player, DisplayManager manager) {
    toAlternate.add(player.getUniqueId());
    manager.setFrame(byId.get(animationOrder.get(frame == 0 ? frame : frame - 1)), player);
    staticPanes.remove(player.getUniqueId());
  }

  void setPreference(Player player, DisplayMode mode, DisplayManager manager) {
    alternatePreferences.put(player.getUniqueId(), mode);
    if (mode == DisplayMode.ALTERNATE) {
      startAlternating(player, manager);
    } else {
      stopAlternating(player, manager);
    }
  }

  /**
   * Determine if this group has a pane with the specified ID
   *
   * @param id to look for
   * @return if the group has the specified pane
   */
  public boolean has(String id) {
    return byId.containsKey(id);
  }

  /**
   * Set a player to a specific frame and leave them there
   *
   * @param player to set frame for
   * @param id id of the frame to set the player to
   * @param manager used to send display updates
   */
  private void setPane(Player player, String id, DisplayManager manager) {
    staticPanes.put(player.getUniqueId(), id);
    manager.setFrame(byId.get(id), player);
  }

  /**
   * Switch to the next registered pane for a specific player
   *
   * @param player to toggle frames for
   * @param manager used to send display updates
   * @return ID of the new pane
   */
  String next(Player player, DisplayManager manager) {
    int current = animationOrder.indexOf(staticPanes.get(player.getUniqueId())) + 1;
    if (current >= animationOrder.size()) current = 0;
    String id = animationOrder.get(current);
    setPane(player, id, manager);
    return id;
  }

  /**
   * Set a player to the default frame (first one) of this group
   *
   * @param player to set frame for
   * @param manager used to send display updates
   */
  void setDefaultPane(Player player, DisplayManager manager) {
    setPane(player, byId.keySet().iterator().next(), manager);
  }

  void setDefaults(Player player, DisplayManager manager) {
    setDefaultPane(player, manager);
    if (shouldAlternateByDefault()) {
      startAlternating(player, manager);
    }
  }

  /** @return a list of all registered pane IDs */
  String list() {
    StringBuilder builder = new StringBuilder();
    for (String s : byId.keySet()) {
      builder.append(s).append(", ");
    }
    return builder.substring(0, builder.toString().length() - 2);
  }

  /**
   * Transfer player data from this pane to a new one
   *
   * @param player who's data is being transferee
   * @param group to transfer the data to
   * @param manager used to send display updates
   */
  void transfer(Player player, PaneGroup group, DisplayManager manager) {
    boolean isAlternating = toAlternate.contains(player.getUniqueId());
    if (!isAlternating && !staticPanes.containsKey(player.getUniqueId())) return;

    // Cleanup previous pane
    Optional<String> current = Optional.empty();
    if (isAlternating) {
      toAlternate.remove(player.getUniqueId());
    } else {
      current = Optional.of(staticPanes.remove(player.getUniqueId()));
    }

    // To new pane
    DisplayMode mode;
    if (alternatePreferences.containsKey(player.getUniqueId())) {
      mode = alternatePreferences.remove(player.getUniqueId());
      group.alternatePreferences.put(player.getUniqueId(), mode);
    } else {
      mode = group.shouldAlternateByDefault() ? DisplayMode.ALTERNATE : DisplayMode.STATIC;
    }

    if (mode == DisplayMode.ALTERNATE) {
      group.startAlternating(player, manager);
    } else {
      if (current.isPresent() && group.has(current.get())) {
        group.setPane(player, current.get(), manager);
      } else {
        group.setDefaultPane(player, manager);
      }
    }
  }

  void removePlayer(Player player) {
    boolean isAlternating = toAlternate.contains(player.getUniqueId());
    if (!isAlternating && !staticPanes.containsKey(player.getUniqueId())) return;

    toAlternate.remove(player.getUniqueId());
    staticPanes.remove(player.getUniqueId());
    alternatePreferences.remove(player.getUniqueId());
  }
}
