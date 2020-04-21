package network.walrus.games.octc.destroyables;

import network.walrus.games.core.api.round.RoundFactory;
import network.walrus.games.octc.Match;
import network.walrus.games.octc.OCNGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A mixed game that has cores and monuments.
 *
 * @author Austin Mayes
 */
public class MixedDestroyableGame extends OCNGame {

  /**
   * Constructor.
   *
   * @param matchFactory used to create matches of this game type
   */
  public MixedDestroyableGame(RoundFactory<Match> matchFactory) {
    super(matchFactory);
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(Material.DIAMOND_PICKAXE);
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
    return "dtcm";
  }

  @Override
  public String name() {
    return "Mixed Destroy";
  }
}
