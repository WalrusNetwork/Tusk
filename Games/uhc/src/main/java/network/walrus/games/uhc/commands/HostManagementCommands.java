package network.walrus.games.uhc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.Type;
import app.ashcon.intake.bukkit.parametric.annotation.Fallback;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCPermissions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Commands used to manage current hosts of a single UHC round.
 *
 * @author Austin Mayes
 */
public class HostManagementCommands {

  /** Add a player as a host */
  @Command(aliases = "addhost", desc = "Add a player as a host", perms = UHCPermissions.HOST_MANAGE)
  public void addHost(@Fallback(Type.SELF) Player player) {
    UHCManager.SPECS.remove(player.getUniqueId());
    UHCManager.HOSTS.add(player.getUniqueId());
    player.setPlayerListName(
        ChatColor.DARK_RED + "[Host] " + ChatColor.RESET + player.getDisplayName());
  }

  /** Remove a player as a host */
  @Command(
      aliases = "rmhost",
      desc = "Remove a player as a host",
      perms = UHCPermissions.HOST_MANAGE)
  public void removeHost(@Fallback(Type.SELF) Player player) {
    resetRoles(player);
  }

  /** Add a player as a spectator */
  @Command(
      aliases = "addspec",
      desc = "Add a player as a spectator",
      perms = UHCPermissions.SPEC_MANAGE)
  public void addSpec(@Fallback(Type.SELF) Player player) {
    UHCManager.HOSTS.remove(player.getUniqueId());
    UHCManager.SPECS.add(player.getUniqueId());
    player.setPlayerListName(ChatColor.RED + "[Spec] " + ChatColor.RESET + player.getDisplayName());
  }

  /** Remove a player as a spectator */
  @Command(
      aliases = "rmspec",
      desc = "Remove a player as a spectator",
      perms = UHCPermissions.SPEC_MANAGE)
  public void removeSpec(@Fallback(Type.SELF) Player player) {
    resetRoles(player);
  }

  private void resetRoles(Player player) {
    UHCManager.HOSTS.remove(player.getUniqueId());
    UHCManager.SPECS.remove(player.getUniqueId());
    player.setPlayerListName(null);
  }
}
