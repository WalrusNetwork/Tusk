package network.walrus.utils.core.player;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Class which is used to render prefixes based on {@link CommandSenderRelationInfo relational
 * information}.
 *
 * @author Austin Mayes
 */
public interface PrefixGenerator {

  /**
   * Render a long prefix for a viewer.
   *
   * @param info describing the relation the viewer has to the target
   * @return long prefix
   */
  BaseComponent renderLong(CommandSenderRelationInfo info);

  /**
   * Render a condensed prefix for a viewer.
   *
   * <p>If no specific condensed version of a renderer is defined, the {@link
   * #renderLong(CommandSenderRelationInfo)} prefix will be used as a fallback.
   *
   * @param info describing the relation the viewer has to the target
   * @return condensed prefix
   */
  default BaseComponent renderCondensed(CommandSenderRelationInfo info) {
    return renderLong(info);
  }
}
