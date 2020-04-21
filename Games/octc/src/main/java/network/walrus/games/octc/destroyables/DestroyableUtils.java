package network.walrus.games.octc.destroyables;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.touchable.TouchableDistanceMetrics;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.destroyables.modes.DestroyableMode;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import network.walrus.games.octc.destroyables.objectives.DestroyableProperties;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableDamageEvent;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableEventInfo;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableRepairEvent;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableTouchEvent;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.bukkit.listener.EventUtil;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.DTCM;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.DTCM.Repair;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.DTCM.Errors;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.player.PersonalizedPlayer;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;

/**
 * Utilities for parsing and handling events revolving around {@link DestroyableObjective}s.
 *
 * @author Austin Mayes
 */
public class DestroyableUtils {

  /**
   * Parse {@link DestroyableProperties} from a configuration source.
   *
   * @param node to pull data from
   * @param holder the properties will be used inside of
   * @return properties parsed from the node
   * @throws ParsingException if the node contains invalid or missing data
   */
  public static DestroyableProperties parseProperties(Node node, FacetHolder holder)
      throws ParsingException {
    // name
    LocalizedConfigurationProperty name =
        BukkitParserRegistry.localizedPropertyParser().parseRequired(node.attribute("name"));

    // owner
    Optional<Team> owner = Optional.empty();
    if (node.hasAttribute("owner")) {
      owner =
          holder.getRegistry().get(Team.class, node.attribute("owner").asRequiredString(), false);
    }

    // Region
    BoundedRegion region =
        holder
            .getRegistry()
            .get(BoundedRegion.class, node.attribute("region").asRequiredString(), true)
            .get();

    // Materials
    MultiMaterialMatcher materials =
        BukkitParserRegistry.multiMaterialMatcherParser()
            .parseRequired(node.attribute("materials"));

    double completion =
        BukkitParserRegistry.percentParser().parse(node.attribute("completion")).orElse(1.0);

    boolean destroyable =
        BukkitParserRegistry.booleanParser().parse(node.attribute("destroyable")).orElse(true);

    // repairable
    boolean repairable =
        BukkitParserRegistry.booleanParser().parse(node.attribute("repairable")).orElse(false);

    boolean enforceAntiRepair =
        BukkitParserRegistry.booleanParser()
            .parse(node.attribute("enforce-anti-repair"))
            .orElse(true);
    ;

    // fireworks
    boolean fireworks =
        BukkitParserRegistry.booleanParser().parse(node.attribute("fireworks")).orElse(true);

    // break-filter
    Optional<Filter> breakCheck =
        node.attribute("break-filter")
            .value()
            .flatMap((n) -> holder.getRegistry().get(Filter.class, n, true));

    // repair-filter
    Optional<Filter> repairCheck =
        node.attribute("repair-filter")
            .value()
            .flatMap((n) -> holder.getRegistry().get(Filter.class, n, true));

    // any-repair
    boolean anyRepair =
        BukkitParserRegistry.booleanParser().parse(node.attribute("any-repair")).orElse(false);

    SingleMaterialMatcher completedState =
        BukkitParserRegistry.singleMaterialMatcherParser()
            .parse(node.attribute("completed-state"))
            .orElse(new SingleMaterialMatcher(Material.AIR));

    DistanceCalculationMetric def =
        new DistanceCalculationMetric(DistanceCalculationMetric.Type.PLAYER, true);
    TouchableDistanceMetrics metrics =
        new TouchableDistanceMetrics.Builder()
            .preComplete(node, "pre-touch", def)
            .postTouch(node, "post-touch", def)
            .postComplete(node, "post-complete", null)
            .build();

    // mode
    Optional<DestroyableMode> mode = Optional.empty();
    if (node.hasAttribute("first-mode")) {
      mode =
          holder
              .getRegistry()
              .get(DestroyableMode.class, node.attribute("first-mode").asRequiredString(), true);
    }

    return new DestroyableProperties(
        owner,
        name,
        region,
        materials,
        destroyable,
        repairable,
        enforceAntiRepair,
        fireworks,
        breakCheck,
        repairCheck,
        completedState,
        completion,
        anyRepair,
        mode,
        metrics);
  }

  /**
   * Parse a {@link DestroyableMode} from a configuration source.
   *
   * @param node to pull data from
   * @param round the properties will be used inside of
   * @return mode from the configuration document
   */
  public static DestroyableMode parseMode(GameRound round, Node<?> node) {
    node.inheritAttributes("modes");
    String id = node.attribute("id").asRequiredString();

    LocalizedConfigurationProperty name =
        BukkitParserRegistry.localizedPropertyParser().parseRequired(node.attribute("name"));

    Optional<LocalizedConfigurationProperty> countdown =
        BukkitParserRegistry.localizedPropertyParser().parse(node.attribute("countdown-message"));
    Optional<LocalizedConfigurationProperty> success =
        BukkitParserRegistry.localizedPropertyParser().parse(node.attribute("success-message"));
    Optional<LocalizedConfigurationProperty> fail =
        BukkitParserRegistry.localizedPropertyParser().parse(node.attribute("fail-message"));

    LinkedHashMap<MultiMaterialMatcher, SingleMaterialMatcher> materials = new LinkedHashMap<>();

    for (Node<?> child : node.childRequired("materials").children()) {
      MultiMaterialMatcher find =
          BukkitParserRegistry.multiMaterialMatcherParser().parseRequired(child.attribute("find"));
      SingleMaterialMatcher replace =
          BukkitParserRegistry.singleMaterialMatcherParser()
              .parseRequired(child.attribute("replace"));
      materials.put(find, replace);
    }

    Duration delay = BukkitParserRegistry.durationParser().parseRequired(node.attribute("delay"));

    Optional<Filter> filter =
        round.getRegistry().get(Filter.class, node.attribute("filter").value().orElse(null), false);
    int retry =
        BukkitParserRegistry.integerParser().parse(node.attribute("retry-attempts")).orElse(0);
    Optional<Duration> retryDelay =
        BukkitParserRegistry.durationParser().parse(node.attribute("retry-delay"));

    Optional<DestroyableMode> passMode = Optional.empty();
    if (node.hasAttribute("pass-mode")) {
      passMode =
          round
              .getRegistry()
              .get(DestroyableMode.class, node.attribute("pass-mode").asRequiredString(), true);
    }

    Optional<DestroyableMode> failMode = Optional.empty();
    if (node.hasAttribute("fail-mode")) {
      failMode =
          round
              .getRegistry()
              .get(DestroyableMode.class, node.attribute("fail-mode").asRequiredString(), true);
    }

    return new DestroyableMode(
        round,
        id,
        name,
        countdown,
        success,
        fail,
        materials,
        delay,
        filter,
        retry,
        retryDelay,
        failMode,
        passMode);
  }

  /**
   * Handle a block break event effecting a single {@link DestroyableObjective}.
   *
   * @param objective which this event will be effecting
   * @param event that is being fired
   * @param manager to pull group data from
   * @return if the objective was actually changed
   */
  public static boolean handleBlockBreak(
      DestroyableObjective objective, BlockChangeByPlayerEvent event, GroupsManager manager) {
    if (!objective.isRemaining(event.getBlock())) {
      return false;
    }

    event.setCancelled(true);

    Player player = event.getPlayer();
    Competitor competitor = manager.getCompetitorOf(player).orElse(null);

    if (competitor == null) {
      return false;
    }

    if (!objective.canPlayerBreak(player, event.getBlock())) {
      player.sendMessage(OCNMessages.ERROR_OBJECTIVE_DAMAGE.with(Errors.DAMAGE_OTHER));
      DTCM.Errors.BREAK_OTHER.play(player);
      return false;
    }

    if (!objective.canComplete(competitor)) {
      player.sendMessage(OCNMessages.ERROR_OBJECTIVE_DAMAGE_OWN.with(Errors.DAMAGE_OWN));
      DTCM.Errors.BREAK_OWN.play(player);
      return false;
    }

    DestroyableEventInfo info =
        new DestroyableEventInfo(
            event.getPlayer(),
            event.getPlayer().getItemInHand(),
            event.getBlock().getType(),
            event.getCause() instanceof BlockBreakEvent);

    DestroyableTouchEvent callTouch = new DestroyableTouchEvent(objective, info);
    EventUtil.call(callTouch);

    Block block = event.getBlock();

    SingleMaterialMatcher matcher = objective.getProperties().completedState;
    MaterialData material = matcher.toMaterialData();

    block.setType(material.getItemType());

    // only change the data if it's specified
    if (matcher.isDataPresent()) {
      block.setData(material.getData());
    }

    boolean wasComplete = objective.isCompleted();
    objective.recordBreak(player);
    objective.recalculateCompletion();

    if (!wasComplete) {
      if (objective.isCompleted()) {
        objective.setTouchedRecently(player, true);
        objective.onComplete(info);
      } else {
        if (!objective.hasTouchedRecently(player)) {
          objective.setTouchedRecently(player, true);

          Localizable monName;

          if (objective.getProperties().owner.isPresent()) {
            monName =
                objective
                    .getName()
                    .toText(objective.getProperties().owner.get().getColor().style());
          } else {
            monName = objective.getName().toText(competitor.getColor().style());
          }

          PersonalizedPlayer playerName = new PersonalizedBukkitPlayer(player);

          Localizable broadcast =
              objective.getTouchMessage().with(OCN.DTCM.DESTROYABLE_TOUCHED, monName, playerName);

          manager.playScopedSound(
              player, DTCM.Touch.SELF, DTCM.Touch.TEAM, null, DTCM.Touch.SPECTATOR);

          List<Player> toMessage = new ArrayList<>();
          toMessage.addAll(manager.getGroup(player).getPlayers());
          toMessage.addAll(manager.getSpectators().getPlayers());

          for (Player messaging : toMessage) {
            messaging.sendMessage(broadcast);
          }

          objective.spawnFirework(block, competitor);

          // New break message after 15 seconds.
          GameTask.of(
                  "Touch reset for " + objective.getName().translateDefault(),
                  () -> {
                    try {
                      if (objective.isTouchRelevant(player)) {
                        objective.setTouchedRecently(player, false);
                      }
                    } catch (Exception e) {
                      // The round has likely cycled, just ignore it.
                    }
                  })
              .later(15 * 20);
        }

        DestroyableDamageEvent damageEvent = new DestroyableDamageEvent(objective, info);
        EventUtil.call(damageEvent);
      }

      return true;
    }

    return false;
  }

  /**
   * Handle a block place event effecting a single {@link DestroyableObjective}.
   *
   * @param objective which this event will be effecting
   * @param event that is being fired
   * @param group of the player who is performing the event
   * @return if the objective was actually changed
   */
  public static boolean handleBlockPlace(
      DestroyableObjective objective,
      BlockChangeByPlayerEvent event,
      Group group,
      GroupsManager manager) {
    Player player = event.getPlayer();

    if (!objective.shouldEnforceRepairRules()) {
      return false;
    }

    BlockState placed = event.getNewState();

    if (!objective.getProperties().materials.matches(placed)
        && !objective.getProperties().anyRepair) {
      return false;
    }

    if (!objective.isOwner(group)) {
      player.sendMessage(OCNMessages.ERROR_OBJECTIVE_REPAIR_ENEMY.with(Errors.REPAIR_ENEMY));
      DTCM.Errors.ENEMY_REPAIR.play(player);
      event.setCancelled(true);
      return false;
    }

    if (!objective.getProperties().repairable
        || !objective.canPlayerRepair(player, event.getBlock())) {
      player.sendMessage(OCNMessages.ERROR_OBJECTIVE_CANNOT_REPAIR.with(Errors.REPAIR_DISABLED));
      DTCM.Errors.NOT_REPAIRABLE.play(player);
      event.setCancelled(true);
      return false;
    }

    objective.repair(event.getBlock());
    event.setCancelled(false);

    new GameTask("Repair " + objective.getName().translateDefault()) {
      @Override
      public void run() {
        objective.recalculateCompletion();
        manager.playScopedSound(player, Repair.SELF, Repair.TEAM, Repair.ENEMY, Repair.SPECTATOR);
        DestroyableRepairEvent callRepair = new DestroyableRepairEvent(objective, player);
        EventUtil.call(callRepair);
      }
    }.now();

    return true;
  }
}
