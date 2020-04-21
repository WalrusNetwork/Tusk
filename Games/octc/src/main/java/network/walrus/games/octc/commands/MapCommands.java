package network.walrus.games.octc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Text;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.octc.OCNGameManager;
import network.walrus.games.octc.OCNMap;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.parsing.world.library.WorldLibrary;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Commands to manage maps during testing.
 *
 * @author Austin Mayes
 */
public class MapCommands {

  private final OCNGameManager manager;

  /** @param manager to use to set maps */
  public MapCommands(OCNGameManager manager) {
    this.manager = manager;
  }

  @Command(
      aliases = {"setmap", "sm"},
      desc = "Set a map.",
      perms = "walrus.setmap")
  public void setMap(@Text String query) throws CommandException {
    Optional<OCNMap> map = manager.getMapManager().search(query);
    if (map.isPresent()) {
      manager.setMap(map.get());
    } else {
      throw new CommandException("Map not found!");
    }
  }

  /** Reload all map XMLs. */
  @Command(
      aliases = {"newmaps", "reloadlibrary", "reloadmaps", "rm", "rl"},
      desc = "Reload all maps",
      perms = "walrus.setmap")
  public void reload(@Sender CommandSender sender) {
    for (WorldLibrary library : (List<WorldLibrary>) manager.getMapManager().getLibraries()) {
      library.build(manager.getMapParser(), GamesPlugin.instance.mapLogger());
    }

    OCNGameManager.instance.getMapSelector().reload();
    sender.sendMessage(Games.Maps.RELOADED + "Libraries reloaded!");
  }

  @Command(
      aliases = {"setmaxplayers"},
      desc = "Set maximum amount of players",
      perms = "walrus.setmaxplayers")
  public void setPlayers(@Sender CommandSender sender, int count) throws CommandException {
    String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
    int oldCount = Bukkit.getMaxPlayers();
    try {
      Object playerList =
          Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".CraftServer")
              .getDeclaredMethod("getHandle", null)
              .invoke(Bukkit.getServer(), null);
      Field maxPlayers = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");
      maxPlayers.setAccessible(true);
      maxPlayers.set(playerList, count);
      sender.sendMessage("Player count set to " + count + " from " + oldCount);
    } catch (Exception e) {
      throw new CommandException("Error while using reflection");
    }
  }
}
