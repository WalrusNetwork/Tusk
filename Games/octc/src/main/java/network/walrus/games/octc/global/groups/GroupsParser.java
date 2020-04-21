package network.walrus.games.octc.global.groups;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.group.CompetitorRule;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.games.core.facets.group.ffa.FFATeam;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.octc.global.groups.ffa.FFAManager;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.games.octc.global.groups.teams.TeamsManager;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.simple.ListParser;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser which parses teams and FFA options.
 *
 * @author Austin Mayes
 */
public class GroupsParser implements FacetParser<OCNGroupsManager> {

  private static final ListParser listParser = BukkitParserRegistry.listParser();
  public static final SimpleParser<GroupColor> COLOR_PARSER =
      holder -> {
        List<String> parts = new ArrayList<>();
        for (StringHolder stringHolder : listParser.parseRequiredList(holder, ":", true)) {
          String asRequiredString = stringHolder.asRequiredString();
          parts.add(asRequiredString);
        }
        GroupColor color = GroupColor.BY_NAME.get(parts.get(0));
        if (color == null) {
          throw new ParsingException(
              holder.parent(), "Unknown group color \"" + parts.get(0) + "\"");
        }
        if (parts.size() > 1) {
          if (parts.contains("bold")) {
            color.bold(true);
          }
          if (parts.contains("underline")) {
            color.underline(true);
          }
          if (parts.contains("italic")) {
            color.italic(true);
          }
        }
        return color;
      };

  @Override
  public Optional<OCNGroupsManager> parse(FacetHolder holder, Node<?> node)
      throws ParsingException {
    Optional<? extends Node<?>> teamsNode = node.child("teams");
    Optional<? extends Node<?>> playersNode = node.child("players");
    Spectators spectators = new Spectators();
    holder.getRegistry().add(spectators);

    if (teamsNode.isPresent()) {
      Node<?> teamsFinal = teamsNode.get();
      List<Team> teams = Lists.newArrayList();
      CompetitorRule rule =
          BukkitParserRegistry.ofEnum(CompetitorRule.class)
              .parse(teamsFinal.attribute("rule"))
              .orElse(CompetitorRule.TEAM);

      for (Node<?> c : teamsFinal.children()) {
        Team team = parseTeam(c);
        holder.getRegistry().add(team);
        teams.add(team);
      }

      return Optional.of(new TeamsManager(holder, teams, rule, spectators));
    }

    if (playersNode.isPresent()) {
      Node playersFinal = playersNode.get();
      FFATeam team = parseFFA(holder, playersFinal);
      return Optional.of(new FFAManager(holder, team, spectators));
    }

    return Optional.empty();
  }

  private Team parseTeam(Node node) throws ParsingException {
    String id =
        node.attribute("id")
            .value()
            .orElse(node.text().asRequiredString().toLowerCase().replace(" ", "-"));
    LocalizedConfigurationProperty name =
        BukkitParserRegistry.localizedPropertyParser().parseRequired(node.text());
    GroupColor color = COLOR_PARSER.parseRequired(node.attribute("color"));
    int min = BukkitParserRegistry.integerParser().parse(node.attribute("min")).orElse(1);
    int max = BukkitParserRegistry.integerParser().parseRequired(node.attribute("max"));
    int maxOverfill =
        BukkitParserRegistry.integerParser()
            .parse(node.attribute("max-overfill"))
            .orElse(max + (int) (max * .5));

    return new Team(id, name, color, min, max, maxOverfill);
  }

  private FFATeam parseFFA(FacetHolder holder, Node node) throws ParsingException {
    LocalizedConfigurationProperty name =
        BukkitParserRegistry.localizedPropertyParser().parseRequired(node.text());

    int min = BukkitParserRegistry.integerParser().parse(node.attribute("min")).orElse(1);
    int max = BukkitParserRegistry.integerParser().parseRequired(node.attribute("max"));
    int maxOverfill =
        BukkitParserRegistry.integerParser()
            .parse(node.attribute("max-overfill"))
            .orElse(max + (int) (max * .5));

    boolean colorize =
        BukkitParserRegistry.booleanParser().parse(node.attribute("colors")).orElse(true);
    boolean friendly =
        BukkitParserRegistry.booleanParser().parse(node.attribute("friendly-fire")).orElse(true);

    return new FFATeam(holder, name, min, max, maxOverfill, colorize, friendly);
  }

  @Override
  public boolean required() {
    return true;
  }
}
