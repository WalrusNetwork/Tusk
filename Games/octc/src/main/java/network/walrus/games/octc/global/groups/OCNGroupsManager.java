package network.walrus.games.octc.global.groups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.GamesCorePermissions;
import network.walrus.games.core.api.spawns.SpawnsManager;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.OCNPermissions;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.Groups.Errors;
import network.walrus.utils.core.text.LocalizedText;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;

/**
 * Default group manager implementation for all OCN style game types.
 *
 * @author Austin Mayes
 */
public abstract class OCNGroupsManager extends GroupsManager {

  /**
   * Constructor.
   *
   * @param holder which this manager is a part of
   */
  public OCNGroupsManager(FacetHolder holder) {
    super(holder);
  }

  @Override
  public boolean isDead(Player player) {
    return getHolder().getFacetRequired(SpawnsManager.class).isDead(player);
  }

  @Override
  public boolean isSpawning(Player player) {
    return getHolder().getFacetRequired(SpawnsManager.class).isSpawning(player);
  }

  @Override
  public double getMaxGroupImbalance() {
    return 0.4;
  }

  /** Finds a team for a player to join */
  void join(Player player, Group from) {
    List<Group> minTeams = new ArrayList<>();
    Group to;

    for (Group test : getGroups()) {
      if (test.isSpectator()) {
        continue;
      }
      if (test.equals(from)) {
        continue;
      }

      if (minTeams.size() == 0 || test.filledPortion() < minTeams.get(0).filledPortion()) {
        minTeams.clear();
        minTeams.add(test);
      } else if (test.filledPortion() == minTeams.get(0).filledPortion()) {
        minTeams.add(test);
      }
    }

    if (minTeams.size() > 0) {
      Collections.shuffle(minTeams);
      to = minTeams.get(0);
    } else {
      to = null;
    }

    join(player, from, to);
  }

  /** Have a user request to join a team */
  void join(Player player, Group from, @Nullable Group to) {
    GameRound round = (GameRound) getHolder();

    if (to == null) {
      player.sendMessage(OCNMessages.ERROR_TEAM_NOT_FOUND.with(Errors.NOT_FOUND));
      return;
    }

    if (from.equals(to)) {
      LocalizedText message;
      if (from.isSpectator()) {
        message = OCNMessages.ERROR_ALREADY_SPECTATOR.with();
      } else {
        message = OCNMessages.ERROR_ALREADY_TEAM.with(to.getName().toText());
      }
      message.style().inherit(Errors.SAME_TEAM_TYPE);
      player.sendMessage(message);
      return;
    }

    boolean canSwitchBeforeStart =
        to.isObserving() && player.hasPermission(OCNPermissions.JOIN_PICK);

    if (!to.isSpectator() && !from.isSpectator() && !canSwitchBeforeStart) {
      player.sendMessage(OCNMessages.ERROR_ALREADY_PLAYING.with(Errors.ALREADY_PLAYING));
      return;
    }

    if (round.getState().finished() && !to.isSpectator()) {
      player.sendMessage(
          GamesCoreMessages.ERROR_CANNOT_JOIN_FINISHED.with(Errors.CANNOT_JOIN_FINISHED));
      return;
    }

    // Check full teams
    if (to.isFull(false)) {
      if (!player.hasPermission(GamesCorePermissions.JOIN_FULL)) {
        player.sendMessage(GamesCoreMessages.ERROR_CANNOT_JOIN_FULL.with(Errors.CANNOT_JOIN_FULL));
        return;
      }

      if (to.isFull(true)) {
        if (!player.hasPermission(OCNPermissions.JOIN_OVERFILL)) {
          player.sendMessage(
              GamesCoreMessages.ERROR_CANNOT_JOIN_OVERFILL.with(Errors.CANNOT_JOIN_OVERFILL));
          return;
        }
      }
    }

    // Check if balanced with one additional player
    if (!isGroupBalanced(to, 1)) {
      if (!player.hasPermission(OCNPermissions.JOIN_OVERFILL)) {
        player.sendMessage(
            OCNMessages.ERROR_CANNOT_JOIN_IMBALANCE.with(Errors.CANNOT_JOIN_IMBALANCE));
        return;
      }
    }

    joinGroup(player, to, Optional.of(from));
  }

  Optional<Group> joinGroup(Player player, Group group, Optional<Group> from) {
    boolean toPlaying = !group.isObserving();
    boolean triggerSpawn = toPlaying || group.isSpectator();

    Optional<Group> joined = changeGroup(player, from, group, triggerSpawn, toPlaying);
    joined.ifPresent(
        x ->
            player.sendMessage(
                GamesCoreMessages.GENERIC_JOINED.with(x.getName().toText(x.getColor().style()))));

    return joined;
  }
}
