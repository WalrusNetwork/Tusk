package network.walrus.games.core.facets.group;

import java.util.UUID;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.distance.PlayerStore;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.entity.Player;

/**
 * A person (or group of people) that is competing to be the winner of a {@link GameRound}.
 *
 * <p>Groups can potentially be competitors, but not all groups should be assumed to be competing,
 * and not all competitors should be assumed to be a direct holder to a group.
 *
 * <p>This is purely an internal class, and should never be made to be directly accessible from a
 * configuration file, or even shown in the UI.
 *
 * @author Avicus Network
 */
public interface Competitor extends PlayerStore {

  /** Internal ID of the competitor, used for equality testing. */
  String id();

  /** Name of this competitor, used in the UI. */
  LocalizedConfigurationProperty getName();

  /** Helper method to get the colored name of this competitor. */
  default Localizable getColoredName() {
    return getName().toText(getColor().style());
  }

  /** The parent group which this competitor is made up of, or is inside of. */
  Group getGroup();

  /**
   * Check if a player is in this competitor.
   *
   * @param uuid to check
   */
  boolean hasPlayer(UUID uuid);

  /**
   * Check if a player is in this competitor.
   *
   * @param player to check
   */
  default boolean hasPlayer(Player player) {
    return hasPlayer(player.getUniqueId());
  }

  /**
   * The color of the competitor.
   *
   * <p>This should be used anywhere the group is referenced in the UI, including overhead names and
   * in the tab list. If players spawn with leather armor, this is the color that the armor should
   * be, and thus allows for uniqueness in armor selection.
   */
  GroupColor getColor();
}
