package network.walrus.utils.bukkit.points;

import java.util.Optional;
import network.walrus.utils.bukkit.block.BlockFaceUtils;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.parse.SimpleParser;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Utilities for parsing {@link AngleProvider}s from {@link Node}s.
 *
 * @author Austin Mayes
 */
public class ProviderParsingUtils {

  private static SimpleParser<Vector> vectorParser = BukkitParserRegistry.vectorParser();
  private static SimpleParser<Float> floatParser = BukkitParserRegistry.floatParser();

  /**
   * Parse a {@link Pair} of {@link AngleProvider}s from a {@link Node}.
   *
   * <p>If the node has the {@code look} attribute, targeted yaw and pitch providers will be created
   * representing the location specified in the attribute.
   *
   * <p>If the node has {@code yaw} and/or {@code pitch} attributes, {@link StaticAngleProvider}s
   * will be created using the supplied values.
   *
   * <p>Yaw values may be numbers, but also support {@link org.bukkit.block.BlockFace}s to provide
   * pre-defined yaw values.
   *
   * <p>If none of the above conditions is met, a pair of {@link Optional#empty()} values will be
   * returned, indicating that the supplied {@link Node} provided no angle provider definition data.
   *
   * @param node to parse from
   * @return pair of yaw - pitch
   */
  public static Pair<Optional<AngleProvider>, Optional<AngleProvider>> parseYawPitch(Node node) {
    Optional<AngleProvider> yaw = Optional.empty();
    Optional<AngleProvider> pitch = Optional.empty();

    if (node.hasAttribute("look")) {
      Vector look = vectorParser.parseRequired(node.attribute("look"));
      yaw = Optional.of(new TargetYawProvider(look));
      pitch = Optional.of(new TargetPitchProvider(look));
    }
    if (node.hasAttribute("yaw")) {
      float yawValue;
      try {
        BlockFace blockFace =
            BukkitParserRegistry.ofEnum(BlockFace.class).parseRequired(node.attribute("yaw"));
        yawValue = BlockFaceUtils.faceToYaw(blockFace);
      } catch (ParsingException e) {
        // Not a block face, fall back to raw yaw
        yawValue = floatParser.parseRequired(node.attribute("yaw"));
      }
      yaw = Optional.of(new StaticAngleProvider(yawValue));
    }
    if (node.hasAttribute("pitch")) {
      float pitchValue = floatParser.parseRequired(node.attribute("pitch"));
      pitch = Optional.of(new StaticAngleProvider(pitchValue));
    }

    return Pair.of(yaw, pitch);
  }
}
