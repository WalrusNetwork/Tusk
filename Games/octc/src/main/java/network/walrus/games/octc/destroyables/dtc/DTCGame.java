package network.walrus.games.octc.destroyables.dtc;

import network.walrus.games.core.api.round.RoundFactory;
import network.walrus.games.octc.Match;
import network.walrus.games.octc.OCNGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A classic game of destroy the core.
 *
 * @author ShinyDialga
 */
public class DTCGame extends OCNGame {

  /**
   * Constructor.
   *
   * @param matchFactory used to create matches of this game type
   */
  public DTCGame(RoundFactory<Match> matchFactory) {
    super(matchFactory);
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(Material.LAVA_BUCKET);
  }

  @Override
  public boolean canJoinMidMatch(boolean premium) {
    return true;
  }

  @Override
  public boolean canSpectateMidMatch() {
    return true;
  }

  @Override
  public String slug() {
    return "dtc";
  }

  @Override
  public String name() {
    return "Destroy the Core";
  }
}
