package network.walrus.games.octc.ctw.wools;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.core.facets.objectives.locatable.LocatableUpdateDistanceEvent;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.ctw.wools.events.WoolPickupEvent;
import network.walrus.games.octc.ctw.wools.events.WoolPlaceEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric.Type;
import network.walrus.utils.bukkit.listener.EventUtil;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.CTW;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.CTW.PickUp;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.CTW.Errors;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Listener which keeps tracks of all events that potentially have an effect on {@link
 * WoolObjective}s.
 *
 * @author Avicus Network
 */
@SuppressWarnings("JavaDoc")
public class WoolListener extends FacetListener<WoolsFacet> {

  private final List<WoolObjective> wools;
  private final SidebarFacet sidebarFacet;
  private final GroupsManager manager;

  /**
   * @param holder which this listener is operating inside of
   * @param facet to pull wool data from
   */
  public WoolListener(FacetHolder holder, WoolsFacet facet) {
    super(holder, facet);
    this.wools = facet.getWools();
    this.sidebarFacet = holder.getFacetRequired(SidebarFacet.class);
    this.manager = holder.getFacetRequired(GroupsManager.class);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (WoolObjective wool : this.wools) {
      wool.setTouchedRecently(event.getPlayer(), false);
    }
  }

  @EventHandler
  public void onPlayerChangeTeam(PlayerChangedGroupEvent event) {
    for (WoolObjective wool : this.wools) {
      wool.setTouchedRecently(event.getPlayer(), false);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPickupItem(PlayerPickupItemEvent event) {
    ItemStack item = event.getItem().getItemStack();
    if (item == null) {
      return;
    }

    for (WoolObjective wool : this.wools) {
      if (!wool.getMatcher().matches(item.getData())) {
        continue;
      }

      Optional<Competitor> comp = manager.getCompetitorOf(event.getPlayer());
      if (wool.canComplete(comp) && wool.isTouchRelevant(event.getPlayer())) {
        wool.setTouchedRecently(event.getPlayer(), true);

        WoolPickupEvent call = new WoolPickupEvent(wool, event.getPlayer());
        EventUtil.call(call);
        sidebarFacet.update(WoolDisplay.woolSlug(wool));
        break;
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void removeNonWoolsFromEnderChest(InventoryCloseEvent event) {
    Inventory inventory = event.getInventory();
    if (inventory.getType() != InventoryType.ENDER_CHEST) {
      return;
    }

    Optional<WoolObjective> objective = getFacet().getObjectiveForWoolChest(inventory);
    if (!objective.isPresent()) {
      return;
    }

    boolean violation = false;
    int overfilledWools = 0;
    List<ItemStack> extraItems = Lists.newArrayList();
    byte woolData = objective.get().getColor().getWoolData();

    // Detect extraneous items
    for (int i = 0; i < inventory.getSize(); i++) {
      ItemStack item = inventory.getItem(i);

      if (item != null && item.getType() == Material.WOOL && item.getDurability() == woolData) {
        // Check stack size, overflow
        if (item.getAmount() > 1) {
          overfilledWools += item.getAmount() - 1;
          item.setAmount(1);
        }
      } else if (item != null && item.getType() != Material.AIR) {
        // Illegal item
        inventory.clear(i);
        extraItems.add(item);
      }
    }

    // Fill air slots with extra wools
    overfilledWools = objective.get().fillWoolInventory(inventory, overfilledWools);

    if (extraItems.size() <= 0 && overfilledWools <= 0) {
      return;
    }

    // Handle extraneous items
    ItemStack woolItem = new ItemStack(Material.WOOL, 1, woolData);
    if (overfilledWools > 0) {
      woolItem.setAmount(overfilledWools);
      extraItems.add(woolItem);
    }

    event
        .getPlayer()
        .sendMessage(OCNMessages.WOOL_CHEST_ILLEGAL_ITEMS.with(Errors.CHEST_ILLEGAL_ITEMS));

    Collection<ItemStack> itemsToDrop =
        event.getPlayer().getInventory().addItem(extraItems.toArray(new ItemStack[] {})).values();
    for (ItemStack itemStack : itemsToDrop) {
      event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), itemStack);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryClickMonitor(InventoryClickEvent event) {
    ItemStack item = event.getCurrentItem();
    Inventory inventory = event.getInventory();
    if (item == null) {
      return;
    }

    InventoryHolder holder = event.getInventory().getHolder();

    Player player = (Player) event.getWhoClicked();
    Competitor competitor =
        getHolder().getFacetRequired(GroupsManager.class).getCompetitorOf(player).orElse(null);

    if (competitor == null) {
      return;
    }

    boolean can = false;

    for (WoolObjective wool : this.wools) {
      if (can) {
        break;
      }

      if (!wool.getMatcher().matches(item.getData())) {
        continue;
      }

      if (!wool.canComplete(competitor) && inventory.getType() == InventoryType.ENDER_CHEST) {
        event.setCancelled(true);
        continue;
      } else if (!wool.canComplete(competitor) && inventory.getType() != InventoryType.ENDER_CHEST) {
        event.setCancelled(false);
        continue;
      } else {
        can = true;
        event.setCancelled(false);
      }

      if (wool.isPlaced()) {
        event
            .getWhoClicked()
            .sendMessage(OCNMessages.WOOL_ALREADY_PLACED.with(Errors.ALREADY_PLACED));
        event.setCancelled(true);
        continue;
      }

      if (wool.isTouchRelevant((Player) event.getWhoClicked())) {
        wool.setTouchedRecently((Player) event.getWhoClicked(), true);

        WoolPickupEvent call = new WoolPickupEvent(wool, player);
        EventUtil.call(call);
        sidebarFacet.update(WoolDisplay.woolSlug(wool));
        break;
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWoolPickup(WoolPickupEvent event) {
    WoolObjective wool = (WoolObjective) event.getObjective();
    GroupsManager groups = getHolder().getFacetRequired(GroupsManager.class);
    Group playerGroup = groups.getGroup(event.getPlayer());
    Spectators spectators = groups.getSpectators();

    List<Player> toMessage = new ArrayList<>();
    toMessage.addAll(playerGroup.getPlayers());
    toMessage.addAll(spectators.getPlayers());

    Localizable woolText = wool.getName().toText(wool.getChatColor());
    Localizable who =
        new UnlocalizedText(event.getPlayer().getName(), playerGroup.getColor().style());
    Localizable broadcast = wool.getTouchMessage().with(who, woolText);

    for (Player player : toMessage) {
      player.sendMessage(broadcast);
    }
    playSound(event.getPlayer());
  }

  private void playSound(Player player) {
    Optional<Competitor> comp = manager.getCompetitorOf(player);
    manager.playScopedSound(
            player, PickUp.SELF, PickUp.TEAM, PickUp.ENEMY, PickUp.SPECTATOR);
    PickUp.SELF.play(player);
    for (Player other : getHolder().players()) {
      if (other.getUniqueId().equals(player.getUniqueId())) {
        continue;
      }
      Group group = manager.getGroup(other);
      if (group.isSpectator()) {
        PickUp.SPECTATOR.play(other);
      }
    }
    comp.ifPresent(
            c -> {
              for (Player other : c.getPlayers()) {
                if (player.getUniqueId().equals(other.getUniqueId())) {
                  continue;
                }

                PickUp.TEAM.play(player);
              }
            });
  }

  // This is to bypass listeners which want to cancel a wool placement
  @EventHandler(priority = EventPriority.LOWEST)
  public void blockChangeByPlayerBypass(BlockChangeByPlayerEvent event) {
    for (WoolObjective wool : getFacet().getWools()) {
      if (wool.getDestination().contains(event.getBlock())) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    Block block = event.getBlock();

    for (WoolObjective wool : this.wools) {
      if (wool.getSource().isPresent() && wool.getSource().get().contains(block)) {
        if (block.getType() == Material.CHEST
            || block.getType() == Material.ENDER_CHEST
            || block.getType() == Material.DISPENSER
            || block.getType() == Material.MOB_SPAWNER) {
          event.setCancelled(true);
        }
        break;
      }

      if (!wool.getDestination().contains(block)) {
        continue;
      }

      if (event.isToAir() || wool.isCompleted()) {
        event.setCancelled(true);
        break;
      } else {
        event.setCancelled(true);

        if (!(event instanceof BlockChangeByPlayerEvent)) {
          break;
        }

        Player player = ((BlockChangeByPlayerEvent) event).getPlayer();
        Competitor competitor =
            getHolder().getFacetRequired(GroupsManager.class).getCompetitorOf(player).orElse(null);

        if (competitor == null) {
          return;
        }

        if (!wool.canComplete(competitor)) {
          player.sendMessage(OCNMessages.WOOL_WRONG_WOOL.with(Errors.WRONG_WOOL));
          CTW.Errors.WRONG_WOOL.play(player);
          break;
        }

        if (!wool.getMatcher().matches(event.getNewState())) {
          player.sendMessage(
              OCNMessages.WOOL_BAD_WOOL.with(
                  Errors.BAD_WOOL, wool.getName().toText(wool.getChatColor())));
          CTW.Errors.BAD_WOOL.play(player);
          break;
        }

        wool.place(player);

        WoolPlaceEvent call = new WoolPlaceEvent(wool, player);
        EventUtil.call(call);
        sidebarFacet.update(WoolDisplay.woolSlug(wool));
        event.setCancelled(false);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof BlockState)) {
      return;
    }

    Player player = (Player) event.getPlayer();

    if (getHolder().getFacetRequired(GroupsManager.class).isObservingOrDead(player)) {
      return;
    }

    Block block = ((BlockState) event.getInventory().getHolder()).getBlock();
    Inventory inventory = event.getInventory();

    // Only refill if no one is looking
    if (inventory.getViewers().size() > 1) {
      return;
    }

    for (WoolObjective wool : this.wools) {
      if (!wool.isRefillable(block)) {
        continue;
      }

      Map<Integer, ItemStack> refill = wool.getRefill(block).get();

      GameTask refillTask =
          GameTask.of(
              "Wool refill",
              () -> {
                int refilled = inventory.getContents().length;
                for (Integer slot : refill.keySet()) {
                  if (refilled > wool.getMaxRefill()) {
                    break;
                  }
                  if (inventory.getItem(slot) == null) {
                    inventory.setItem(slot, refill.get(slot).clone());
                    refilled++;
                  }
                }
              });
      if (wool.getRefillDelay().isPresent()) {
        refillTask.later((int) wool.getRefillDelay().get().getSeconds() * 20);
      } else {
        refillTask.now();
      }
    }
  }

  @EventHandler
  public void onPrepareItemCraft(PrepareItemCraftEvent event) {
    ItemStack result = event.getRecipe().getResult();
    InventoryHolder holder = event.getInventory().getHolder();

    if (!(holder instanceof Player) || result == null) {
      return;
    }

    Player player = (Player) holder;

    for (WoolObjective wool : this.wools) {
      if (wool.isCraftable()) {
        continue;
      }

      if (wool.getMatcher().matches(result.getData())) {
        Localizable name = wool.getName().toText();

        player.sendMessage(OCNMessages.WOOL_CANNOT_CRAFT.with(Errors.CANNOT_CRAFT, name));
        event.getInventory().setResult(null);
        break;
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onMove(PlayerCoarseMoveEvent event) {
    for (WoolObjective w : this.wools) {
      if (w.getDistanceCalculationMetricType(event.getPlayer()) == Type.PLAYER
          && w.updateDistance(event.getPlayer(), event.getTo())) {
        EventUtil.call(new LocatableUpdateDistanceEvent(w));
        sidebarFacet.update(WoolDisplay.woolSlug(w));
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onKill(PlayerDeathByPlayerEvent event) {
    for (WoolObjective w : this.wools) {
      if (w.getDistanceCalculationMetricType(event.getCause()) == Type.KILL
          && w.updateDistance(event.getCause(), event.getLocation())) {
        EventUtil.call(new LocatableUpdateDistanceEvent(w));
        sidebarFacet.update(WoolDisplay.woolSlug(w));
      }

      if (w.canComplete(manager.getCompetitorOf(event.getCause()))) {
        w.increaseWoolAllowance(event.getCause());
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onBlock(BlockChangeByPlayerEvent event) {
    for (WoolObjective w : this.wools) {
      if (w.getDistanceCalculationMetricType(event.getPlayer()) == Type.BLOCK
          && w.getMatcher().matches(event.getBlock().getState())
          && w.updateDistance(event.getPlayer(), event.getBlock().getLocation())) {
        EventUtil.call(new LocatableUpdateDistanceEvent(w));
        sidebarFacet.update(WoolDisplay.woolSlug(w));
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
  public void onEnderChestOpen(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        || event.getClickedBlock().getType() != Material.ENDER_CHEST) {
      return;
    }

    for (WoolObjective w : this.wools) {
      if (w.getSource().isPresent() && w.getSource().get().contains(event.getClickedBlock())) {
        event.setCancelled(true);
        event.getPlayer().openInventory(w.getWoolInventory(event.getPlayer()));
        return;
      }
    }
  }
}
