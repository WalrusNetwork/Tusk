package network.walrus.games.core.facets.group.color;

import java.util.Optional;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.utils.bukkit.color.ColorProvider;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * Converts {@link GroupColor}s to colors that can be used for dying items and setting color damage
 * values.
 *
 * @author Austin Mayes
 */
public class GroupColorProvider implements ColorProvider {

  /** round that the provider is handling colors for. */
  private final FacetHolder round;

  /**
   * Constructor.
   *
   * @param round round that the provider is handling colors for
   */
  public GroupColorProvider(FacetHolder round) {
    this.round = round;
  }

  /**
   * Helper method to getFirst the team color of a player.
   *
   * @param player player to getFirst the color for
   * @return color of the player's group
   */
  private GroupColor getTeamColor(Player player) {
    return this.round.getFacetRequired(GroupsManager.class).getGroup(player).getColor();
  }

  public DyeColor getDyeColor(Optional<Player> player) {
    if (player.isPresent()) {
      return getTeamColor(player.get()).getDyeColor();
    }
    return DyeColor.BLACK;
  }

  @Override
  public Color getColor(Optional<Player> player) {
    return getDyeColor(player).getColor();
  }
}
