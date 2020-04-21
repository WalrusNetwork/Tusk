package network.walrus.utils.bukkit.parse.simple;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;

/**
 * Parses plain {@link BaseComponent}s from {@link StringHolder}s.
 *
 * @author Austin Mayes
 */
public class BaseComponentParser implements SimpleParser<BaseComponent> {

  @Override
  public BaseComponent parseRequired(StringHolder holder) throws ParsingException {
    String text = ChatColor.translateAlternateColorCodes('^', holder.asRequiredString());
    return new TextComponent(TextComponent.fromLegacyText(text));
  }
}
