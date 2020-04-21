package network.walrus.games.core.facets.modifyprojectile;

import java.util.Optional;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.math.PreparedNumberAction;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;

/**
 * Parser for {@link ModifyProjectileFacet}.
 *
 * @author Rafi Baum
 */
public class ModifyProjectileParser implements FacetParser<ModifyProjectileFacet> {

  @Override
  public Optional<ModifyProjectileFacet> parse(FacetHolder holder, Node<?> node)
      throws ParsingException {
    if (!node.hasChild("modify-bow-projectile")) {
      return Optional.empty();
    }

    Node<?> modifyNode = node.childRequired("modify-bow-projectile");

    Optional<Class<? extends Projectile>> type = Optional.empty();
    Optional<PreparedNumberAction> velocityMod = Optional.empty();
    Optional<String> kit = Optional.empty();

    if (modifyNode.hasChild("projectile")) {
      Class<? extends Entity> typeClass =
          BukkitParserRegistry.ofEnum(EntityType.class)
              .parseRequired(modifyNode.childRequired("projectile").text())
              .getEntityClass();

      if (!Projectile.class.isAssignableFrom(typeClass)) {
        throw new ParsingException("Projectile is not valid");
      }

      type = Optional.of(typeClass.asSubclass(Projectile.class));
    }

    if (modifyNode.hasChild("velocity")) {
      velocityMod =
          Optional.of(
              (BukkitParserRegistry.preparedNumberActionParser()
                  .parse(modifyNode.childRequired("velocity"))));
    }

    if (modifyNode.hasAttribute("kit")) {
      kit = Optional.of(node.attribute("kit").asRequiredString());
    }

    return Optional.of(new ModifyProjectileFacet(holder, type, velocityMod, kit));
  }
}
