package network.walrus.games.core.facets.visual;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.BlankTabItem;
import com.keenant.tabbed.item.PlayerTabItem;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.tablist.TabList;
import com.keenant.tabbed.tablist.TableTabList;
import com.keenant.tabbed.tablist.TableTabList.FillDirection;
import com.keenant.tabbed.tablist.TableTabList.TableBox;
import com.keenant.tabbed.tablist.TableTabList.TableCell;
import com.keenant.tabbed.tablist.TableTabList.TableCorner;
import com.keenant.tabbed.util.Skins;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.events.group.GroupMaxPlayerCountChangeEvent;
import network.walrus.games.core.events.group.GroupRenameEvent;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.games.core.util.GameTask;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.events.player.PlayerJoinDelayedEvent;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.TabList.Size;
import network.walrus.utils.core.color.NetworkColorConstants.Network;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Main class responsible for the rendering of tab lists for all players during OCN matches.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("JavaDoc")
public class TabListFacet extends Facet implements Listener {

  private static final int ROWS = 20;
  private static final int COLUMNS = 4;
  private final GameRound holder;
  private final GameTask onTick;
  private final AtomicBoolean trackingTeamSwitches = new AtomicBoolean(false);
  private final Map<UUID, PlayerNameProvider> providers = Maps.newHashMap();
  private final Queue<Runnable> queuedUpdates = new ConcurrentLinkedQueue<>();
  private List<TableBox> teamBoxes;
  private List<TabItem> blanks;
  private GroupsManager groupsManager;

  /** @param holder the list is being rendered inside of */
  public TabListFacet(FacetHolder holder) {
    this.holder = (GameRound) holder;
    this.onTick =
        GameTask.of(
            "Tablist tick",
            () -> {
              for (Player player : holder.getContainer().players()) {
                updateTitles(player);
              }
              // 4 updates a tick
              for (int i = 0; i < 4; i++) {
                Runnable update = queuedUpdates.poll();
                if (update != null) {
                  update.run();
                } else {
                  break;
                }
              }
            });
  }

  @Override
  public void load() throws FacetLoadException {
    this.blanks = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      this.blanks.add(new BlankTabItem());
    }
    this.onTick.repeatAsync(0, 1);
    this.groupsManager = holder.getFacetRequired(GroupsManager.class);
  }

  @Override
  public void unload() {
    this.onTick.cancel();
  }

  /**
   * Asynchronously update the tab lists for all online players.
   *
   * @param purgeData if the entire tab list should be cleared and re-rendered
   */
  public void update(boolean purgeData) {
    queuedUpdates.clear(); // No need to do old updates
    for (Player player : Bukkit.getOnlinePlayers()) {
      this.queuedUpdates.add(() -> this.update(player, purgeData));
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onMatchOpen(RoundOpenEvent event) {
    this.teamBoxes = createTeamBoxes();
    trackingTeamSwitches.set(false);
    GameTask.of(
            "Tablist reset",
            () -> {
              Tabbed tabbed = UbiquitousBukkitPlugin.getInstance().getTabbed();
              for (Player p : Bukkit.getOnlinePlayers()) {
                if (tabbed.getTabList(p) == null && !isTabLegacy(p)) {
                  tabbed.newTableTabList(p, COLUMNS, 16);
                }
              }
              update(true);
              trackingTeamSwitches.set(true);
            })
        .later(30);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinDelayedEvent event) {
    // delayed by 1 1/2 second for locale to be sent
    new GameTask("Tablist Set: " + event.getPlayer().getName()) {
      @Override
      public void run() {
        if (event.getPlayer().isOnline() && !isTabLegacy(event.getPlayer())) {
          Tabbed tabbed = UbiquitousBukkitPlugin.getInstance().getTabbed();

          if (tabbed.getTabList(event.getPlayer()) == null) {
            tabbed.newTableTabList(event.getPlayer(), COLUMNS, 16);
          }

          update(event.getPlayer(), false);
        }
      }
    }.laterAsync(30);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    update(false);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChangeTeam(PlayerChangedGroupEvent event) {
    if (trackingTeamSwitches.get()) update(false);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onStateChangeEvent(RoundStateChangeEvent event) {
    if (event.getTo().isPresent()
        && !event.getTo().get().starting()
        && !event.getFrom().isPresent()) {
      update(true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onGroupRename(GroupRenameEvent event) {
    update(true);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void groupMaxPlayersChange(final GroupMaxPlayerCountChangeEvent event) {
    this.update(true);
  }

  private boolean isTabLegacy(Player player) {
    return UbiquitousBukkitPlugin.getInstance().getCompatManager().getVersion(player) < 47;
  }

  private List<TableBox> createTeamBoxes() {
    List<TableBox> teamBoxes = new ArrayList<>();

    int i = 0;
    for (Group team : groupsManager.getGroups()) {
      if (team.isSpectator()) {
        continue;
      }
      TableCell left = new TableCell(i, 1);
      TableCell right = new TableCell(i, ROWS - 4);
      teamBoxes.add(new TableBox(left, right));
      i++;
    }

    if (teamBoxes.size() == 1) {
      TableBox box = teamBoxes.iterator().next();
      box.getTopRight().setColumn(COLUMNS - 1);
      box.getBottomRight().setColumn(COLUMNS - 1);
    } else if (teamBoxes.size() == 2) {
      i = 0;
      for (TableBox box : teamBoxes) {
        box.getTopLeft().setColumn(i * 2);
        box.getBottomLeft().setColumn(i * 2);
        box.getTopRight().setColumn(i * 2 + 1);
        box.getBottomRight().setColumn(i * 2 + 1);
        i++;
      }
    } else if (teamBoxes.size() > 4) {
      int size = teamBoxes.size();
      List<List<TableBox>> partitioned = Lists.partition(teamBoxes, size / 4);
      for (List<TableBox> partition : partitioned) {
        int column = partitioned.indexOf(partition);
        int height = ((ROWS - 4) / (partition.size()));
        for (TableBox box : partition) {
          int initialRow = partition.indexOf(box) + 1;
          if (initialRow > 1) {
            initialRow = initialRow + height;
          }

          box.getTopLeft().setRow(initialRow);
          box.getTopLeft().setColumn(column);

          box.getBottomLeft().setRow(initialRow + height);
          box.getBottomLeft().setColumn(column);

          box.getTopRight().setRow(initialRow);
          box.getTopRight().setColumn(column);

          box.getBottomRight().setRow(initialRow + height);
          box.getBottomRight().setColumn(column);
        }
      }
    }

    return teamBoxes;
  }

  private TableTabList getTabList(Player player) {
    TabList tab = UbiquitousBukkitPlugin.getInstance().getTabbed().getTabList(player);
    return (TableTabList) tab;
  }

  /**
   * Update the header and footer for a specific player using current match information.
   *
   * @param player to update titles for
   */
  public void updateTitles(Player player) {
    @Nullable final TableTabList tab = this.getTabList(player);
    if (tab == null) {
      return;
    }

    final GameMap map = this.holder.map();
    final UnlocalizedText mapPart = new UnlocalizedText(map.name());
    mapPart.style().inherit(Games.Maps.NAME).bold(true);
    final UnlocalizedText authorPart = new UnlocalizedText("TODO");
    authorPart.style().inherit(Games.Maps.AUTHOR).bold(true);
    RoundState state = holder.getState();
    TextStyle stateColor = Games.States.IDLE;
    if (state.starting()) {
      stateColor = Games.States.STARTING;
    } else if (state.playing()) {
      stateColor = Games.States.PLAYING;
    } else if (state.finished()) {
      stateColor = Games.States.FINISHED;
    }

    final String time = StringUtils.secondsToClock((int) holder.getPlayingDuration().getSeconds());
    final String header =
        GamesCoreMessages.UI_BY
            .with(Games.Maps.BY, mapPart, authorPart)
            .render(player)
            .toLegacyText();
    BaseComponent footer = new TextComponent("");

    @Nullable String serverName = null; // TODO
    if (serverName != null) {
      footer.addExtra(new UnlocalizedText(serverName, Network.LOCAL_SERVER).render(player));
      footer.addExtra(new UnlocalizedText(" - ", Games.OCN.TabList.NEUTRAL).render(player));
    }
    footer.addExtra(stateColor.apply(time));

    tab.setHeaderFooter(header, footer.toLegacyText());
  }

  /**
   * Update and re-render the tab list for a specific player.
   *
   * @param player to update data for
   * @param purgeData if the entire tab list should be cleared and re-rendered
   */
  public void update(Player player, boolean purgeData) {
    if (!player.isOnline()) {
      return;
    }

    TableTabList tab = getTabList(player);

    if (tab == null) {
      return;
    }

    tab.setBatchEnabled(true);

    if (purgeData) {
      List<TabItem> blanks = new ArrayList<>();
      for (int i = 0; i < ROWS * COLUMNS; i++) {
        blanks.add(new BlankTabItem());
      }
      tab.fill(tab.getBox(), blanks);
    }

    // Spectators
    {
      boolean spec = groupsManager.isSpectator(player);

      List<TabItem> specs = new ArrayList<>();
      if (spec) {
        specs.add(getTabItem(player, player));
      }
      for (Player target : groupsManager.getSpectators().getPlayers()) {
        if (target.getUniqueId().equals(player.getUniqueId()) || !target.isOnline()) {
          continue;
        }

        specs.add(getTabItem(target, player));
      }

      // Fill in any additional space
      specs.addAll(this.blanks);

      // Create the box from the list
      TableBox spectatorBox =
          new TableBox(
              tab.getBox().getBottomLeft().clone().add(0, -2),
              tab.getBox().getBottomRight().clone());
      tab.fill(spectatorBox, specs, TableCorner.BOTTOM_LEFT);
    }

    // Teams
    {
      Iterator<TableBox> availableBoxes = teamBoxes.iterator();
      Group playerGroup = groupsManager.getGroup(player);
      if (!playerGroup.isSpectator()) {
        fillTeamBox(availableBoxes.next(), playerGroup, player, tab, true);
      }
      for (Group group : groupsManager.getGroups()) {
        if (group.id().equalsIgnoreCase(playerGroup.id()) || group.isSpectator()) {
          continue;
        }
        fillTeamBox(availableBoxes.next(), group, player, tab, false);
      }
    }

    tab.batchUpdate();
    try {
      tab.setBatchEnabled(false);
    } catch (RuntimeException e) {
      // We do this because tabbed really isn't thread safe, but we handle it internally.
      if (!e.getMessage().equals("cannot disable batch before batchUpdate() called")) {
        e.printStackTrace();
      }
    }
  }

  private void fillTeamBox(
      TableBox box, Group team, Player viewer, TableTabList tab, boolean first) {
    List<TabItem> items = new ArrayList<>();
    // Ensure player is always first
    if (first) {
      items.add(getTabItem(viewer, viewer));
    }
    for (Player target : team.getPlayers()) {
      if (target.getUniqueId().equals(viewer.getUniqueId()) || !target.isOnline()) {
        continue;
      }
      items.add(getTabItem(target, viewer));
    }
    items.addAll(this.blanks);

    TabItem teamItem = getTabItem(team, viewer);

    tab.set(box.getTopLeft().clone().add(0, -1), teamItem);
    tab.fill(box, items, TableCorner.TOP_LEFT, FillDirection.VERTICAL);
  }

  private TextTabItem getTabItem(Group group, Player viewer) {
    Localizable name = group.getName().toText(group.getColor().style());
    UnlocalizedFormat format = new UnlocalizedFormat("{0}/{1} {2}");
    String text =
        format
            .with(
                Size.DELIMITER,
                new LocalizedNumber(group.size(), Size.CURRENT),
                new LocalizedNumber(group.getMaxPlayers(), Size.MAX),
                name)
            .render(viewer)
            .toLegacyText();
    return new TextTabItem(
        text,
        1000,
        Skins.getDot(org.bukkit.ChatColor.valueOf(group.getColor().getChatColor().name())));
  }

  private PlayerTabItem getTabItem(Player player, Player viewer) {
    return new PlayerTabItem(player, getProvider(viewer));
  }

  private PlayerNameProvider getProvider(Player viewer) {
    return providers.computeIfAbsent(
        viewer.getUniqueId(), (u) -> new PlayerNameProvider(this.holder, viewer));
  }
}
