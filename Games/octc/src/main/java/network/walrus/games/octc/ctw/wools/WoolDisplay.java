package network.walrus.games.octc.ctw.wools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.Renderable;
import network.walrus.utils.bukkit.visual.renderable.TargetedRenderable;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Display pane which is used to render all wools for a specific {@link WoolsFacet} for a specific
 * {@link Competitor}.
 *
 * @author Austin Mayes
 */
class WoolDisplay extends DisplayPane {

  private static final char UNTOUCHED = '⬜';
  private static final char TOUCHED = '░';
  private static final char SAFETY = '▞';
  private static final char PLACED = '⬛';
  private static final char ARROW = '«';
  public static Map<WoolObjective, String> slugs = Maps.newHashMap();
  private final FacetHolder holder;
  private final WoolsFacet facet;
  private final Optional<Competitor> competitor;

  /**
   * @param holder which this pane is being rendered in
   * @param manager this pane is working inside of
   * @param facet containing all of the wools to be displayed
   * @param competitor which is viewing the pane
   */
  WoolDisplay(
      GameRound holder, DisplayManager manager, WoolsFacet facet, Optional<Competitor> competitor) {
    super(manager);
    this.holder = holder;
    this.facet = facet;
    this.competitor = competitor;
    slugs.clear();
    addElements();
    setTitle(Games.OCN.CTW.SCOREBOARD_HEADER.apply(holder.map().game().name()));
  }

  /**
   * Get a slug which is used to represent the supplied wool in the context of IDs.
   *
   * @param objective to get the slug for
   * @return slug of the wool
   */
  static String woolSlug(WoolObjective objective) {
    String owner = objective.getTeam().map(team -> team.id()).orElse("ownerless");
    return slugs.getOrDefault(
        objective,
        owner
            + "-"
            + objective
                .getName()
                .render(Bukkit.getConsoleSender())
                .toLowerCase()
                .replace(" ", "-"));
  }

  private void addElements() {
    List<WoolObjective> ownerless = Lists.newArrayList();
    Multimap<Team, WoolObjective> byOwner = HashMultimap.create();

    // Non-owned first
    for (WoolObjective wool : facet.getWools()) {
      if (wool.getTeam().isPresent()) {
        byOwner.put(wool.getTeam().get(), wool);
      } else {
        ownerless.add(wool);
      }
    }

    int rows = ownerless.size();
    rows += ownerless.isEmpty() ? 0 : 1;

    rows += byOwner.keys().size() * 2;
    rows += byOwner.values().size();

    if (rows >= 13 && !ownerless.isEmpty()) {
      addElement(createRenderable(ownerless, "shared"));
    } else {
      for (WoolObjective wool : ownerless) {
        addElement(createRenderable(wool));
      }
    }
    if (!ownerless.isEmpty()) {
      addSpacer();
    }

    GroupsManager manager = holder.getFacetRequired(GroupsManager.class);
    for (Group group : manager.getGroups()) {
      if (!(group instanceof Team)) {
        continue;
      }

      if (byOwner.containsKey(group)) {
        Collection<WoolObjective> wools = byOwner.get((Team) group);

        TextStyle style = group.getColor().style();
        boolean same = competitor.isPresent() && group.equals(competitor.get());

        UnlocalizedFormat format = new UnlocalizedFormat("{0} " + (same ? (ARROW) : ""));

        if (same) {
          style.bold();
        }

        addElement(group.id(), format.with(group.getName().toText(style)));
        if (rows >= 13) {
          addElement(createRenderable(wools, group.id()));
        } else {
          for (WoolObjective wool : wools) {
            addElement(createRenderable(wool));
          }
        }
        addSpacer();
      }
    }
  }

  private Renderable createRenderable(Collection<WoolObjective> objectives, String suffix) {
    String slug = "group-" + suffix;
    for (WoolObjective o : objectives) {
      slugs.put(o, slug);
    }
    return new TargetedRenderable(slug) {
      @Override
      public String[] text(Player player) {
        StringBuilder text = new StringBuilder();
        for (WoolObjective objective : objectives) {
          text.append(objective.getChatColor().toString());
          text.append(stateIcon(objective, player));
          text.append(" ");
        }

        return new String[] {text.toString()};
      }
    };
  }

  private Renderable createRenderable(WoolObjective objective) {
    String slug = woolSlug(objective);
    slugs.put(objective, slug);
    return new TargetedRenderable(slug) {
      @Override
      public String[] text(Player player) {
        String text = objective.getChatColor().toString();
        text += stateIcon(objective, player);
        text += " " + objective.getName().toText().render(player).toLegacyText();

        return new String[] {text};
      }
    };
  }

  private char stateIcon(WoolObjective objective, Player player) {
    if (objective.isCompleted()) {
      return PLACED;
    } else if (objective.isTouched() && objective.canSeeTouched(player)) {
      return TOUCHED;
    }

    return UNTOUCHED;
  }
}
