package network.walrus.games.octc.destroyables.objectives.events;

import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Useful information passed around for events relating to {@link DestroyableObjective}s.
 *
 * @author Austin Mayes
 */
public class DestroyableEventInfo {

  private final Player actor;
  private final ItemStack tool;
  private final Material broken;
  private final boolean byHand;

  /**
   * @param actor who caused the event to occur
   * @param tool that was used to change the objective
   * @param broken material that was broken
   * @param byHand if the action was performed using direct contact
   */
  public DestroyableEventInfo(Player actor, ItemStack tool, Material broken, boolean byHand) {
    this.actor = actor;
    this.tool = tool;
    this.broken = broken;
    this.byHand = byHand;
  }

  public Player getActor() {
    return actor;
  }

  public ItemStack getTool() {
    return tool;
  }

  public Material getBroken() {
    return broken;
  }

  public boolean isByHand() {
    return byHand;
  }
}
