package network.walrus.games.uhc.facets.chat;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.chat.ChatFacet;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.facets.groups.UHCGroupsManager;
import network.walrus.games.uhc.facets.groups.UHCTeam;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.TeamChat;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.command.exception.TranslatableCommandWarningException;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Commands for sending specialized messages to team chat.
 *
 * @author Austin Mayes
 */
public class TeamChatCommands extends FacetCommandContainer<ChatFacet> {

  private final ImmutableMap<Material, ChatColor> ORE_COLORS =
      ImmutableMap.<Material, ChatColor>builder()
          .put(Material.COAL_ORE, ChatColor.DARK_GRAY)
          .put(Material.IRON_ORE, ChatColor.WHITE)
          .put(Material.GOLD_ORE, ChatColor.GOLD)
          .put(Material.REDSTONE_ORE, ChatColor.RED)
          .put(Material.EMERALD_ORE, ChatColor.GREEN)
          .put(Material.LAPIS_ORE, ChatColor.BLUE)
          .put(Material.QUARTZ_ORE, ChatColor.WHITE)
          .put(Material.DIAMOND_ORE, ChatColor.AQUA)
          .build();
  private final ImmutableMap<Material, String> SINGULAR_ORES =
      ImmutableMap.<Material, String>builder()
          .put(Material.COAL_ORE, "Coal")
          .put(Material.IRON_ORE, "Iron")
          .put(Material.GOLD_ORE, "Gold")
          .put(Material.REDSTONE_ORE, "Redstone")
          .put(Material.EMERALD_ORE, "Emerald")
          .put(Material.LAPIS_ORE, "Lapis")
          .put(Material.QUARTZ_ORE, "Quartz")
          .put(Material.DIAMOND_ORE, "Diamond")
          .build();
  private final ImmutableMap<Material, String> PLURAL_ORES =
      ImmutableMap.<Material, String>builder()
          .put(Material.COAL_ORE, "Coal")
          .put(Material.IRON_ORE, "iron")
          .put(Material.GOLD_ORE, "Gold")
          .put(Material.REDSTONE_ORE, "Redstone")
          .put(Material.EMERALD_ORE, "Emeralds")
          .put(Material.LAPIS_ORE, "Lapis")
          .put(Material.QUARTZ_ORE, "Quartz")
          .put(Material.DIAMOND_ORE, "Diamonds")
          .build();
  private final UHCGroupsManager groupsManager;

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public TeamChatCommands(FacetHolder holder, ChatFacet facet) {
    super(holder, facet);
    this.groupsManager = holder.getFacetRequired(UHCGroupsManager.class);
  }

  private void ensureOnTeam(Player player) throws CommandException {
    Group group = this.groupsManager.getGroup(player);
    if (!(group instanceof UHCTeam)) {
      throw new TranslatableCommandErrorException(UHCMessages.NOT_ON_TEAM);
    }
  }

  /**
   * Message the ores you have to your teammate(s)
   *
   * @throws CommandException if the player isn't on a team
   */
  @Command(
      aliases = {"pmores", "pmo", "sendores", "so", "messageores", "mo"},
      desc = "Message the ores you have to your teammate(s)")
  public void ores(@Sender Player player) throws CommandException {
    ensureOnTeam(player);
    Map<Material, AtomicInteger> ores = Maps.newHashMap();
    for (ItemStack stack : player.getInventory().getContents()) {
      if (stack == null) {
        continue;
      }
      Material material = stack.getType();
      if (material.name().contains("ORE")) {
        ores.putIfAbsent(material, new AtomicInteger());
        ores.get(material).addAndGet(stack.getAmount());
      }
    }
    if (ores.isEmpty()) {
      throw new TranslatableCommandWarningException(UHCMessages.NO_ORES);
    }

    getFacet().chatToTeam(player, generateOresMessage(ores));
  }

  /**
   * Message where you are in the world to your teammate(s)
   *
   * @throws CommandException if the player isn't on a team
   */
  @Command(
      aliases = {"pmcoords", "pmc", "messagecoords", "mc", "messagecords", "pmcords"},
      desc = "Message where you are in the world to your teammate(s)")
  public void cords(@Sender Player player) throws CommandException {
    ensureOnTeam(player);
    Location location = player.getLocation();
    UnlocalizedFormat format = new UnlocalizedFormat("I am at {0}, {1}, {2} in {3}");
    Localizable worldName = new UnlocalizedText(location.getWorld().getName(), TeamChat.WORLD_NAME);
    TextStyle numStyle = TeamChat.CORD;
    getFacet()
        .chatToTeam(
            player,
            format.with(
                ChatColor.WHITE,
                new LocalizedNumber(location.getBlockX(), numStyle),
                new LocalizedNumber(location.getBlockY(), numStyle),
                new LocalizedNumber(location.getBlockZ(), numStyle),
                worldName));
  }

  private Localizable generateOresMessage(Map<Material, AtomicInteger> ores) {
    StringBuilder formatRaw = new StringBuilder("I have ");
    for (int i = 0; i < ores.size() * 2; i++) {
      formatRaw.append("{").append(i).append("} ");
      i++;
      formatRaw.append("{").append(i).append("}, ");
    }
    UnlocalizedFormat format =
        new UnlocalizedFormat(formatRaw.toString().substring(0, formatRaw.toString().length() - 2));
    Localizable[] args = new Localizable[ores.size() * 2];
    int i = 0;
    for (Entry<Material, AtomicInteger> entry : ores.entrySet()) {
      int amount = entry.getValue().get();
      String name = PLURAL_ORES.get(entry.getKey());
      if (amount == 1) {
        name = SINGULAR_ORES.get(entry.getKey());
      }
      args[i] = new LocalizedNumber(amount, TeamChat.ORES_NUMBER);
      i++;
      args[i] = new UnlocalizedText(name, TextStyle.ofColor(ORE_COLORS.get(entry.getKey())));
      i++;
    }
    return format.with(TeamChat.ORES_MESSAGE, args);
  }
}
