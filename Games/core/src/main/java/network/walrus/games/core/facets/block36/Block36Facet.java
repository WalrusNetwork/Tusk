package network.walrus.games.core.facets.block36;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Removes all instances of block 36 in the game world and allows other facets to handle any
 * side-effects this may have.
 *
 * @author Rafi Baum
 */
public class Block36Facet extends Facet implements Listener {

  private static Block36Facet instance;
  private final FacetHolder holder;
  private final Set<Vector> block36Locs;

  /** @param holder which this facet is monitoring */
  public Block36Facet(FacetHolder holder) {
    this.holder = holder;
    instance = this;
    block36Locs = Sets.newHashSet();
  }

  /** @return the current instance of {@link Block36Facet}. */
  public static Block36Facet getInstance() {
    return instance;
  }

  /** Remove blocks and keep track of locations. */
  @EventHandler
  public void onChunkLoad(ChunkLoadEvent event) {
    if (!holder.getContainer().isInside(event.getChunk())) return;

    Set<Vector> blocks = new HashSet<>();
    for (Block b : event.getChunk().getBlocks(Material.PISTON_EXTENSION)) {
      Vector vector = new Vector(b.getX(), b.getY(), b.getZ());
      blocks.add(vector);
    }

    block36Locs.addAll(blocks);

    event.getWorld().fastBlockChange(blocks, new MaterialData(Material.AIR));
  }

  /**
   * Returns true if block 36 was in the specified location when the map was loaded.
   *
   * @param x x block coordinate
   * @param y y block coordinate
   * @param z z block coordinate
   * @return if block 36 was in the specified location when the map was loaded
   */
  public boolean is36Here(int x, int y, int z) {
    return block36Locs.contains(new Vector(x, y, z));
  }
}
