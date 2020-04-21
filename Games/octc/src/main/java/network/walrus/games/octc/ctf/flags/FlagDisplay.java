package network.walrus.games.octc.ctf.flags;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.Renderable;
import network.walrus.utils.bukkit.visual.renderable.animated.StaticAnimatedRenderable;
import network.walrus.utils.bukkit.visual.renderable.animated.TargetedAnimatedRenderable;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.CTF;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Display pane which is used to render all flags for a specific {@link FlagsFacet} for a specific
 * {@link Competitor}.
 *
 * @author Austin Mayes
 */
class FlagDisplay extends DisplayPane {

  private static final String AT_POST = "⚑";
  private static final String ON_GROUND = "⚑";
  private static final String CARRIED = "➔";
  private static final String ARROW = "«";
  private static final UnlocalizedFormat flagFormatNoPoints = new UnlocalizedFormat("{0} {1}");
  private static final UnlocalizedFormat flagFormatWithPoints =
      new UnlocalizedFormat("{0} {1}: {2}/{3}");
  public static Map<FlagObjective, String> slugs = Maps.newHashMap();
  private final FacetHolder holder;
  private final FlagsFacet facet;
  private final Optional<Competitor> competitor;
  private SidebarFacet sidebarFacet;

  /**
   * @param holder which this pane is being rendered in
   * @param manager this pane is working inside of
   * @param facet containing all of the flags to be displayed
   * @param competitor which is viewing the pane
   */
  FlagDisplay(
      GameRound holder, DisplayManager manager, FlagsFacet facet, Optional<Competitor> competitor) {
    super(manager);
    this.holder = holder;
    this.facet = facet;
    this.competitor = competitor;
    slugs.clear();
    addElements();
    setTitle(Games.OCN.CTW.SCOREBOARD_HEADER.apply(holder.map().game().name()));
  }

  /**
   * Get a slug which is used to represent the supplied flag in the context of IDs.
   *
   * @param objective to get the slug for
   * @param competitor competitor who is viewing the flag
   * @return slug of the flag
   */
  static String flagSlug(FlagObjective objective, Optional<Competitor> competitor) {
    String compSlug = competitor.map(c -> c.getName().translateDefault()).orElse("spec") + "-";
    return compSlug
        + slugs.getOrDefault(
            objective,
            objective.getOwner().map(t -> t.id() + "-").orElse("")
                + objective
                    .getName()
                    .render(Bukkit.getConsoleSender())
                    .toLowerCase()
                    .replace(" ", "-"));
  }

  private void addElements() {
    GroupsManager manager = holder.getFacetRequired(GroupsManager.class);
    int rows = manager.getGroups().size() * (2 + facet.getFlags().size());

    for (Group group : manager.getGroups()) {
      if (!(group instanceof Team)) {
        continue;
      }

      TextStyle style = group.getColor().style();
      boolean same = competitor.isPresent() && group.equals(competitor.get());

      UnlocalizedFormat format = new UnlocalizedFormat("{0} " + (same ? (ARROW) : ""));

      if (same) {
        style.bold();
      }

      addElement(group.id(), format.with(group.getName().toText(style)));

      Set<FlagObjective> flags = Sets.newHashSet();
      for (FlagObjective flag : facet.getFlags()) {
        if (flag.getOwner().isPresent() && flag.getOwner().get().equals(group)) {
          continue;
        }

        flags.add(flag);
      }

      if (rows >= 13) {
        addElement(createCompactRenderable(flags, group.id()));
      } else {
        for (FlagObjective flag : flags) {
          addElement(createRenderable(flag));
        }
      }
      addSpacer();
    }
  }

  private Renderable createCompactRenderable(Collection<FlagObjective> objectives, String suffix) {
    String slug = "group-" + suffix;
    for (FlagObjective o : objectives) {
      slugs.put(o, slug);
    }

    return new TargetedAnimatedRenderable(slug) {
      @Override
      public int maxFrames() {
        return 2;
      }

      @Override
      public short delay() {
        return 10;
      }

      @Override
      public String[] text(Player player, int frame) {
        StringBuilder text = new StringBuilder();
        for (FlagObjective objective : objectives) {
          text.append(objective.getChatColor().toString());
          text.append(stateIcon(objective, frame).render(player).toLegacyText());
          text.append(" ");
        }

        return new String[] {text.toString()};
      }
    };
  }

  private Renderable createRenderable(FlagObjective objective) {
    String slug = flagSlug(objective, competitor);
    slugs.put(objective, slug);
    return new StaticAnimatedRenderable(slug) {
      @Override
      public int maxFrames() {
        return 2;
      }

      @Override
      public short delay() {
        return 10;
      }

      @Override
      public Localizable[] text(int frame) {
        Localizable icon = stateIcon(objective, frame);
        if (objective.getPointsNeeded() > 1) {
          return new Localizable[] {
            flagFormatWithPoints.with(
                icon,
                objective.getName().toText(),
                new LocalizedNumber(objective.getPoints(), CTF.SCORE),
                new LocalizedNumber(objective.getPointsNeeded(), CTF.SCORE_LIMIT))
          };
        } else {
          return new Localizable[] {flagFormatNoPoints.with(icon, objective.getName().toText())};
        }
      }
    };
  }

  private Localizable stateIcon(FlagObjective objective, int frame) {
    String symbol;
    if (objective.isCarried()) {
      symbol = CARRIED;

      if (frame % 2 == 1) {
        return new UnlocalizedText(CARRIED, ChatColor.DARK_GRAY);
      }
    } else if (objective.isAtPost()) {
      symbol = AT_POST;
    } else if (objective.getCountdown().isPresent()) {
      symbol = Long.toString(objective.getCountdown().get().getRemainingTime().getSeconds());
      return new UnlocalizedText(symbol, CTF.SIDEBAR_COUNTDOWN);
    } else if (objective.isDropped()) {
      symbol = ON_GROUND;
    } else {
      throw new IllegalStateException("Could not generate state icon for flag state");
    }

    return new UnlocalizedText(symbol, objective.getChatColor());
  }
}
