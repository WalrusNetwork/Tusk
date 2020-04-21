package network.walrus.games.core.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.map.MapInfo;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.core.color.NetworkColorConstants.Games.Commands;
import network.walrus.utils.core.color.NetworkColorConstants.Games.Commands.Rules;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.UnlocalizedComponent;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.command.CommandSender;

/**
 * Contains commands which give the sender info about the maps available
 *
 * @author Rafi Baum
 */
public class MapInfoCommands extends FacetCommandContainer<Facet> {

  /** @param holder which this object is inside of */
  public MapInfoCommands(FacetHolder holder) {
    super(holder);
  }

  /**
   * Send map information to a sender
   *
   * @param sender who is requesting the map information
   * @throws CommandException if the current environment isn't a {@link GameRound}
   */
  @Command(
      aliases = {"map", "mapinfo", "info", "currmap", "m", "mi", "cm"},
      desc = "Gives information about the current map")
  public void printInfo(@Sender CommandSender sender) throws CommandException {
    if (!(getHolder() instanceof GameRound)) {
      throw new TranslatableCommandErrorException(GamesCoreMessages.ERROR_ONLY_ROUNDS);
    }

    MapInfo info = ((GameRound) getHolder()).map().mapInfo();

    Localizable header =
        new UnlocalizedFormat("{0} {1}")
            .with(
                TextStyle.create().padded(),
                new UnlocalizedText(info.getName(), Commands.MAP_NAME),
                new UnlocalizedText(info.getVersion().toString(), Commands.MAP_VERSION));
    sender.sendMessage(header);
    if (info.getObjective().isPresent()) {
      sender.sendMessage(
          GamesCoreMessages.INFO_OBJECTIVE.with(Commands.MAP_OBJECTIVE, info.getObjective().get()));
    }

    if (!info.getRules().isEmpty()) {
      TextComponent rules = new TextComponent("");

      for (int i = 0; i < info.getRules().size(); i++) {
        rules.addExtra(Rules.PREFIX.apply("\n  " + (i + 1) + ") "));
        rules.addExtra(Rules.TEXT.apply(info.getRules().get(i)));
      }

      sender.sendMessage(
          GamesCoreMessages.INFO_RULES.with(Rules.HEADER, new UnlocalizedComponent(rules)));
    }
  }
}
