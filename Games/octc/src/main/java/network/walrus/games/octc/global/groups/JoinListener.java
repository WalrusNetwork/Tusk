package network.walrus.games.octc.global.groups;

import network.walrus.common.CommandSender;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.events.player.PlayerSpawnBeginEvent;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.OCNGameManager;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.OCNPermissions;
import network.walrus.nerve.bukkit.event.PermissionsLoadedEvent;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.events.player.PlayerJoinDelayedEvent;
import network.walrus.ubiquitous.bukkit.inventory.WalrusInventory;
import network.walrus.ubiquitous.bukkit.inventory.items.InventoryItemBuilder;
import network.walrus.utils.bukkit.item.ItemTag;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.Groups;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.Groups.GUI;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Class which provides users with a graphical way for joining matches.
 *
 * @author Rafi Baum
 */
public class JoinListener extends FacetListener<OCNGroupsManager> {

  private static final ItemTag.Boolean JOIN_SWORD = new ItemTag.Boolean("join-sword", false);
  private static final ItemTag.Boolean PICK_HELMET = new ItemTag.Boolean("pick-helmet", false);

  private final int pickerRows;
  private final WalrusInventory joinInventory;

  /**
   * Constructor.
   *
   * @param holder
   * @param facet
   */
  public JoinListener(FacetHolder holder, OCNGroupsManager facet) {
    super(holder, facet);
    this.pickerRows = (facet.getGroups().size() - 1) / 9 + 1;
    this.joinInventory =
        UbiquitousBukkitPlugin.getInstance()
            .getInventoryManager()
            .createInventory(
                pickerRows,
                builder -> {
                  builder.setName(OCNMessages.PICKER_GUI_TITLE.with(GUI.PICKER_TITLE));
                  for (Group group : facet.getGroups()) {
                    if (group.isSpectator()) {
                      continue;
                    }

                    builder.addItem(createJoinItem(group));
                  }
                });
  }

  private ItemStack createJoinSword(CommandSender sender) {
    ItemStack joinSword = new ItemStack(Material.IRON_SWORD);

    ItemMeta meta = joinSword.getItemMeta();
    meta.setDisplayName(OCNMessages.JOIN_SWORD_NAME.with(GUI.JOIN_SWORD_NAME).toLegacyText(sender));
    JOIN_SWORD.set(meta, true);

    joinSword.setItemMeta(meta);
    return joinSword;
  }

  private ItemStack createPickerHelmet(CommandSender sender) {
    ItemStack pickerHelmet = new ItemStack(Material.LEATHER_HELMET);

    ItemMeta meta = pickerHelmet.getItemMeta();
    meta.setDisplayName(
        OCNMessages.PICKER_HELMET_NAME.with(GUI.HELMET_PICKER_NAME).toLegacyText(sender));
    PICK_HELMET.set(meta, true);

    pickerHelmet.setItemMeta(meta);
    return pickerHelmet;
  }

  @EventHandler
  public void onSpawn(PlayerSpawnBeginEvent event) {
    if (!event.isGiveKit()) {
      return;
    }

    giveKit(event.getPlayer(), event.getGroup());
  }

  @EventHandler
  public void onPermissionsLoaded(PermissionsLoadedEvent event) {
    if (event.getPlayer().hasPermission(OCNPermissions.JOIN_PICK) && !event.getPlayer().isOp()) {
      Group group = getFacet().getGroup(event.getPlayer());
      if (!group.isSpectator()) {
        return;
      }

      Inventory inventory = event.getPlayer().getInventory();
      for (int i = 1; i < 8; i++) {
        ItemStack item = inventory.getItem(i);
        inventory.clear(i);
        inventory.setItem(i + 1, item);
      }

      event.getPlayer().getInventory().setItem(1, createPickerHelmet(event.getPlayer()));
    }
  }

  private void giveKit(Player player, Group group) {
    if (!group.isSpectator()) {
      return;
    }

    player.getInventory().setItem(0, createJoinSword(player));

    if (player.hasPermission(OCNPermissions.JOIN_PICK)) {
      player.getInventory().setItem(1, createPickerHelmet(player));
    }
  }

  @EventHandler
  public void onClick(PlayerInteractEvent event) {
    ItemStack clickItem = event.getItem();

    if (JOIN_SWORD.get(clickItem)) {
      Group group = getFacet().getGroup(event.getPlayer());
      getFacet().join(event.getPlayer(), group);
      event.setCancelled(true);
    } else if (PICK_HELMET.get(clickItem)) {
      joinInventory.open(event.getPlayer());
      event.setCancelled(true);
    }
  }

  private InventoryItemBuilder createJoinItem(Group group) {
    InventoryItemBuilder item =
        InventoryItemBuilder.createItem(
            (inventory, player) -> {
              ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
              LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
              TextStyle style = TextStyle.ofBold().color(group.getColor().getChatColor());
              meta.setDisplayName(group.getName().toText(style).toLegacyText(player));
              meta.setColor(group.getColor().getColor());
              helmet.setItemMeta(meta);
              return helmet;
            });

    item.onClick(
        (inventory, player) -> {
          Group playerGroup = getFacet().getGroup(player);
          getFacet().join(player, playerGroup, group);
          player.closeInventory();
        });

    return item;
  }

  @EventHandler
  public void onJoin(PlayerJoinDelayedEvent event) {
    RoundState state = OCNGameManager.instance.getCurrentMatch().getState();
    if (state.finished()) {
      return;
    }

    event.getPlayer().sendMessage(OCNMessages.JOIN_HINT.with(Groups.JOIN_HINT));
  }

  @EventHandler
  public void onStart(RoundOpenEvent event) {
    GameTask.of(
            "give-hint",
            () ->
                getHolder().getContainer().broadcast(OCNMessages.JOIN_HINT.with(Groups.JOIN_HINT)))
        .runTaskLater(GamesPlugin.instance, 1);
  }
}
