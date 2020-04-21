package network.walrus.games.octc.destroyables;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import network.walrus.games.octc.destroyables.objectives.cores.CoreObjective;
import network.walrus.games.octc.destroyables.objectives.monuments.MonumentObjective;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.Renderable;
import network.walrus.utils.bukkit.visual.renderable.StaticRenderable;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;

/**
 * Display pane which is used to render all destroyable objectives for a specific {@link
 * Competitor}.
 *
 * @author ShinyDialga & Austin Mayes
 */
public class DestroyablesDisplay extends DisplayPane {

  private static final char UNTOUCHED = '✕';
  private static final char TOUCHED = '✴';
  private static final char COMPLETED = '✔';
  private static final char ARROW = '«';
  private static final UnlocalizedFormat spacedFormat = new UnlocalizedFormat("{0} {1}");
  private final FacetHolder holder;
  private final List<DestroyableObjective> objectives;
  private final Optional<Competitor> competitor;

  /**
   * @param holder which this pane is being rendered in
   * @param manager this pane is working inside of
   * @param objectives that are to be displayed
   * @param competitor which is viewing the pane
   */
  public DestroyablesDisplay(
      GameRound holder,
      DisplayManager manager,
      List<DestroyableObjective> objectives,
      Optional<Competitor> competitor) {
    super(manager);
    this.holder = holder;
    this.objectives = objectives;
    this.competitor = competitor;
    addElements();
    boolean onlyMons = true;
    for (DestroyableObjective objective : objectives) {
      if (!(objective instanceof MonumentObjective)) {
        onlyMons = false;
        break;
      }
    }
    if (onlyMons) {
      setTitle(Games.OCN.DTM.SCOREBOARD_HEADER.apply("Monuments"));
    } else {
      boolean onlyCores = true;
      for (DestroyableObjective o : objectives) {
        if (!(o instanceof CoreObjective)) {
          onlyCores = false;
          break;
        }
      }
      if (onlyCores) {
        setTitle(Games.OCN.DTC.SCOREBOARD_HEADER.apply("Cores"));
      } else {
        setTitle(Games.OCN.DTCM.SCOREBOARD_HEADER.apply("Objectives"));
      }
    }
  }

  /**
   * Get a slug which is used to represent the supplied objective in the context of IDs.
   *
   * @param objective to get the slug for
   * @return slug of the objective
   */
  public static String objectiveSlug(DestroyableObjective objective) {
    return (objective.getProperties().owner.isPresent()
            ? objective.getProperties().owner.get().id()
            : "")
        + objective.getName().render(Bukkit.getConsoleSender()).toLowerCase().replace(" ", "-");
  }

  private void addElements() {
    Multimap<Team, DestroyableObjective> teamObjectives = HashMultimap.create();
    Map<DestroyableObjective, Renderable> destroyRenderable = Maps.newHashMap();

    GroupsManager manager = holder.getFacetRequired(GroupsManager.class);

    // Build list of objectives each team needs to complete
    for (Group group : manager.getGroups()) {
      if (!(group instanceof Team)) {
        continue;
      }

      Team team = (Team) group;
      for (DestroyableObjective objective : objectives) {
        if (objective
            .getProperties()
            .owner
            .map(owner -> !owner.id().equals(team.id()))
            .orElse(true)) {
          teamObjectives.put(team, objective);
        }
      }
    }
    for (Team team : teamObjectives.keySet()) {
      // Create header
      Localizable teamName = team.getColoredName();
      Localizable header;

      if (competitor.isPresent() && competitor.get().id().equals(team.id())) {
        // Team is viewing its own objectives
        teamName.style().bold(true);
        header = spacedFormat.with(teamName, new UnlocalizedText(Character.toString(ARROW)));
      } else {
        // Team is viewing other objectives
        header = teamName;
      }
      addElement(team.id() + "-header", header);

      // Create objective renderables
      for (DestroyableObjective objective : teamObjectives.get(team)) {
        addElement(destroyRenderable.computeIfAbsent(objective, this::createRenderable));
      }
      addSpacer();
    }
  }

  private Renderable createRenderable(DestroyableObjective objective) {
    return new StaticRenderable(objectiveSlug(objective)) {
      @Override
      public Localizable[] text() {
        return new Localizable[] {
          spacedFormat.with(
              stateStyle(objective), stateIcon(objective), objective.getName().toText())
        };
      }
    };
  }

  private TextStyle stateStyle(DestroyableObjective objective) {
    if (objective.isCompleted()) {
      return Games.OCN.Objectives.COMPLETED;
    } else if (objective.isTouched() && objective.canSeeTouched(competitor)) {
      return Games.OCN.Objectives.TOUCHED;
    }

    return Games.OCN.Objectives.UNTOUCHED;
  }

  private Localizable stateIcon(DestroyableObjective objective) {
    char c;

    if (objective.isCompleted()) {
      c = COMPLETED;
    } else if (objective.isTouched() && objective.canSeeTouched(competitor)) {
      c = TOUCHED;
    } else {
      c = UNTOUCHED;
    }

    return new UnlocalizedText(Character.toString(c));
  }
}
