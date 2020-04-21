package network.walrus.games.uhc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import com.google.api.client.util.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.stats.StatsFacet;
import network.walrus.games.core.facets.stats.trackers.KillTracker;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.UHCWorldSource;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.KillTop;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Rules;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.command.CommandSender;

/**
 * Commands used to view basic game information.
 *
 * @author Austin Mayes
 */
public class GameInformationCommands extends FacetCommandContainer {

  /** @param holder which this object is inside of */
  public GameInformationCommands(FacetHolder holder) {
    super(holder);
  }

  /**
   * View the rules
   *
   * @param sender who is requesting the rules
   * @throws CommandException if the round is not set up yet
   */
  @Command(
      aliases = {"rules", "uhcrules", "ur", "gamerules", "gr", "rulelist"},
      desc = "View the rules")
  public void rules(@Sender CommandSender sender) throws CommandException {
    if (!(getHolder() instanceof UHCRound)) {
      throw new TranslatableCommandErrorException(GamesCoreMessages.ERROR_ONLY_ROUNDS);
    }

    List<LocalizedConfigurationProperty> rules = ((UHCWorldSource) getHolder().getSource()).rules();
    sender.sendMessage(
        UHCMessages.RULES_HEADER.with(Rules.HEADER_TEXT.padded().padStyle(Rules.HEADER_LINE)));
    for (LocalizedConfigurationProperty r : rules) {
      Localizable text = r.toText();
      text.style().inherit(Rules.RULE);
      sender.sendMessage(text);
    }
  }

  /**
   * View the top killers in the match
   *
   * @param sender who is seeing the top kills
   * @throws TranslatableCommandErrorException if the round hasn't started yet
   */
  @Command(
      aliases = {"kt", "killtop", "tk", "topkills"},
      desc = "Get the top 5 killers in the match")
  public void getKillTop(@Sender CommandSender sender) throws TranslatableCommandErrorException {
    if (!(getHolder() instanceof UHCRound)) {
      throw new TranslatableCommandErrorException(GamesCoreMessages.ERROR_ONLY_ROUNDS);
    }

    Map<Competitor, AtomicInteger> kills =
        getHolder()
            .getFacetRequired(StatsFacet.class)
            .getTracker(KillTracker.class)
            .get()
            .getSortedKills();
    if (kills.size() == 0) {
      sender.sendMessage(UHCMessages.KILL_TOP_NONE.with(KillTop.NO_KILLS));
      return;
    }

    UnlocalizedFormat topKillEntry = new UnlocalizedFormat("{0}. {1}: {2}");
    List<Localizable> topKills = Lists.newArrayList();

    Iterator<Entry<Competitor, AtomicInteger>> killsIterator = kills.entrySet().iterator();

    for (int i = 0; i < Math.min(5, kills.size()); i++) {
      Entry<Competitor, AtomicInteger> entry = killsIterator.next();
      topKills.add(
          topKillEntry.with(
              KillTop.ENTRY,
              new LocalizedNumber(i, KillTop.POSITION),
              entry.getKey().getColoredName(),
              new LocalizedNumber(entry.getValue().get(), KillTop.KILL_COUNT)));
    }

    for (Localizable topKill : topKills) {
      sender.sendMessage(topKill);
    }
  }
}
