package network.walrus.utils.bukkit.visual.display;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.utils.bukkit.visual.Sidebar;
import network.walrus.utils.bukkit.visual.Sidebar.Constants;
import network.walrus.utils.bukkit.visual.renderable.Renderable;
import network.walrus.utils.bukkit.visual.renderable.StaticRenderable;
import network.walrus.utils.bukkit.visual.renderable.TargetedRenderable;
import network.walrus.utils.bukkit.visual.renderable.animated.StaticAnimatedRenderable;
import network.walrus.utils.bukkit.visual.renderable.animated.TargetedAnimatedRenderable;
import network.walrus.utils.core.chat.ChatUtils;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A specific set of elements displayed in a specific order on a {@link
 * org.bukkit.scoreboard.Scoreboard} with a title.
 *
 * <p>Everything in this class is cached, to avoid multiple calls to the update methods of each
 * {@link Renderable}, The cache is populated on the first {@link #render(Player)} call, and can be
 * manually updated with the {@link #update(Player, Renderable)} method. To save memory, when a
 * player is no longer using this pane, the {@link #purgeCache(Player)} method should be called.
 * WHen the pane is being discarded, the {@link #purgeCache()} method should be called.
 *
 * @author Austin Mayes
 */
public class DisplayPane {

  /** Elements in this pane, in order. */
  private final List<Renderable> elements = Lists.newArrayList();
  /** Cache of elements in the format of renderable ID - player UUID - cached value */
  private final Table<String, UUID, String[]> elementCache =
      Tables.newCustomTable(Maps.newConcurrentMap(), Maps::newConcurrentMap);
  /** The manager this pane is working inside of. */
  private final DisplayManager manager;
  private final Timing sendTimer;
  /** The title of the scoreboard. */
  private Renderable scoreboardTitle;
  private int secondsToShow;

  /**
   * Constructor.
   *
   * @param manager this pane is working inside of
   * @param secondsToShow how long to show the pane for during alternation
   */
  public DisplayPane(DisplayManager manager, int secondsToShow) {
    this.manager = manager;
    this.secondsToShow = secondsToShow;
    this.sendTimer = Timings.of(manager.owner(), "Sidebar send");
  }

  /**
   * Constructor.
   *
   * @param manager this pane is working inside of
   */
  public DisplayPane(DisplayManager manager) {
    this(manager, 15);
  }

  /**
   * Adds a scoreboard renderable to the next position on the scoreboard
   *
   * @param element new scoreboard renderable
   */
  public void addElement(Renderable element) {
    elements.add(element);
  }

  /**
   * Clear the elements, and purge the cache. This will clear the whole pane but will not be visible
   * until the next {@link #render(Player)}.
   */
  public void purge() {
    elements.clear();
    purgeCache();
  }

  /** Purge all data in the cache. */
  public void purgeCache() {
    elementCache.clear();
  }

  /**
   * Purge all data in the cache related to a specific player.
   *
   * @param player to purge
   */
  public void purgeCache(Player player) {
    elementCache.column(player.getUniqueId()).clear();
  }

  /**
   * Purge all data related to a certain renderable ID.
   *
   * @param id to purge data for
   */
  public void purgeCache(String id) {
    elementCache.row(id).clear();
  }

  /**
   * Purge all data related to a certain renderable ID being shown to a certain player.
   *
   * @param id to purge data for
   * @param player to purge data for
   */
  public void purgeCache(String id, Player player) {
    elementCache.remove(id, player.getUniqueId());
  }

  /**
   * Add an renderable that is just an unchanging string
   *
   * @param id the renderable id
   * @param message the renderable message
   */
  public void addElement(String id, Localizable message) {
    addElement(
        new StaticRenderable(id) {
          @Override
          public Localizable[] text() {
            return new Localizable[] {message};
          }
        });
  }

  /** Add a single line break to the scoreboard. */
  public void addSpacer() {
    addElement("spacer", new UnlocalizedText(""));
  }

  /**
   * Get the title renderable of the scoreboard.
   *
   * @return the title
   */
  protected Renderable getTitle() {
    return scoreboardTitle;
  }

  /**
   * Set the current scoreboard title.
   *
   * @param title the new scoreboard title
   */
  public void setTitle(Renderable title) {
    scoreboardTitle = title;
    for (UUID u : elementCache.columnKeySet()) {
      elementCache.put(title.id(), u, renderElement(title, Bukkit.getPlayer(u)));
    }
  }

  /**
   * Set the current scoreboard title.
   *
   * @param title the new scoreboard title
   */
  public void setTitle(Localizable title) {
    Renderable renderable =
        new StaticRenderable("title") {
          @Override
          public Localizable[] text() {
            return new Localizable[] {title};
          }
        };
    setTitle(renderable);
  }

  /**
   * Set the current scoreboard title.
   *
   * @param title the new scoreboard title
   */
  public void setTitle(String title) {
    Renderable renderable =
        new StaticRenderable("title") {
          @Override
          public Localizable[] text() {
            return new Localizable[] {new UnlocalizedText(title)};
          }
        };
    setTitle(renderable);
  }

  /**
   * Set the current scoreboard title.
   *
   * @param title the new scoreboard title
   */
  public void setTitle(BaseComponent title) {
    setTitle(title.toLegacyText());
  }

  /**
   * Get all elements being displayed on the board in order.
   *
   * @return all elements on the board
   */
  public Collection<Renderable> getElements() {
    return elements;
  }

  /**
   * Clear the player's {@link Sidebar}, and add all of the elements to the board. If the renderable
   * is in the cache, the cached version will be put on the scoreboard. If the renderable is not
   * cached, the update method of the renderable will be called, the result will be added to the
   * cache, and the value will be added to the board.
   *
   * @param player to render the elements for
   */
  public void render(Player player) {
    Sidebar sidebar = manager.getSidebar(player);

    Bukkit.getScheduler()
        .runTaskAsynchronously(
            manager.owner(),
            () -> {
              Renderable titleElement = getTitle();
              String[] titleTmp = elementCache.get(titleElement.id(), player);
              // Update the title in the cache
              if (titleTmp == null) {
                titleTmp = renderElement(titleElement, player);
                elementCache.put(titleElement.id(), player.getUniqueId(), titleTmp);
              }
              final String[] title = titleTmp;

              String[] rows = new String[14]; // rows to be displayed
              int index = 1; // Start at one because all arrays start at 1 in this context
              for (Renderable element : elements) {
                if (index >= rows.length) {
                  break;
                }

                String[] cached = elementCache.get(element.id(), player.getUniqueId());
                // Update the row in the cache
                if (cached == null) {
                  cached = renderElement(element, player);
                  elementCache.put(element.id(), player.getUniqueId(), cached);
                }
                // Copy the cached result into the rows array
                System.arraycopy(cached, 0, rows, index, cached.length);
                index = index + element.maxLines();
              }
              new BukkitRunnable() {
                @Override
                public void run() {
                  // Update the actual sidebar
                  try (Timing timing = sendTimer.startClosable()) {
                    for (int i = 1; i <= Constants.MAX_ROWS; i++) {
                      if (i < rows.length) {
                        sidebar.setRow(rows.length, i, rows[i]);
                      } else {
                        sidebar.setRow(rows.length, i, null);
                      }
                    }
                    // Update title
                    sidebar.setTitle(title[0]);
                  }
                }
              }.runTask(manager.owner());
            });
  }

  /**
   * Render a specific renderable in the context of a player.
   *
   * @param element to render
   * @param player to render to
   * @return the rendered renderable
   */
  private String[] renderElement(Renderable element, Player player) {
    String[] valueRaw = null; // The raw string value of the targeted elements, already localized
    Localizable[] value = null; // The localized result of the other elements

    try {
      // Animated
      if (element instanceof TargetedAnimatedRenderable) {
        valueRaw =
            ((TargetedAnimatedRenderable) element)
                .text(
                    player,
                    ((TargetedAnimatedRenderable) element)
                        .currentFrame()); // Frame is updated in the manager task
      } else if (element instanceof StaticAnimatedRenderable) {
        value = ((StaticAnimatedRenderable) element).text(); // Frame is updated in the manager task
        // Non-Animated
      } else if (element instanceof TargetedRenderable) {
        valueRaw = ((TargetedRenderable) element).text(player);
      } else if (element instanceof StaticRenderable) {
        value = ((StaticRenderable) element).text();
      } else {
        throw new UnsupportedOperationException(
            "Tried to get data from unknown frame type: " + element.getClass().getSimpleName());
      }
    } catch (NullPointerException exception) {
      Bukkit.getLogger().severe("Null pointer when getting message from '" + element.id() + "'");
      exception.printStackTrace();
    }

    if (value == null && valueRaw == null) {
      valueRaw = new String[] {}; // Assume null is empty
    }

    if (valueRaw == null) {
      // Translate the value array for the given player
      valueRaw =
          Arrays.stream(value)
              .map(
                  v -> {
                    if (v == null) {
                      return null;
                    }
                    return ChatUtils.cleanColorCodes(v.render(player).toLegacyText());
                  })
              .toArray(String[]::new);
    }

    return valueRaw;
  }

  /**
   * Update an renderable for a specific player.
   *
   * @param player to update for
   * @param element to update
   */
  public void update(Player player, Renderable element) {
    elementCache.put(element.id(), player.getUniqueId(), renderElement(element, player));
  }

  /** @return seconds the pane should show when alternating */
  public int getSecondsToShow() {
    return secondsToShow;
  }

  /**
   * Get the maximum row count that this bar can be if all data arrays are full.
   *
   * @return the maximum row count for this bar
   */
  protected int getSize() {
    int size = 0;

    for (Renderable element : elements) {
      if (element != null) {
        size = size + element.maxLines();
      }
    }

    return size;
  }
}
