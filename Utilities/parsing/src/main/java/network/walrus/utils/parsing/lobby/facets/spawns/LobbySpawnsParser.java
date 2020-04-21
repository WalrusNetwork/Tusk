package network.walrus.utils.parsing.lobby.facets.spawns;

import java.util.Optional;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parses a single {@link BoundedRegion} and hands it off to the {@link LobbySpawnManager}.
 *
 * @author Austin Mayes
 */
public class LobbySpawnsParser implements FacetParser<LobbySpawnManager> {

  @Override
  public boolean required() {
    return true;
  }

  @Override
  public Optional<LobbySpawnManager> parse(FacetHolder holder, Node<?> node)
      throws ParsingException {
    Node spawnNode = node.childRequired("spawn");
    BoundedRegion region =
        holder
            .getRegistry()
            .get(BoundedRegion.class, spawnNode.text().asRequiredString(), true)
            .get();
    return Optional.of(new LobbySpawnManager(region));
  }
}
