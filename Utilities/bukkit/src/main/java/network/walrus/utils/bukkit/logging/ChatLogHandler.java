package network.walrus.utils.bukkit.logging;

import com.google.common.collect.ImmutableMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.utils.core.translation.TextStyle;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A log {@link Handler} that shows log messages to all players with a given permission, and the
 * console, if it also has that permission.
 *
 * @author Avicus Network
 */
public class ChatLogHandler extends Handler {

  private static final ImmutableMap<Level, ChatColor> coloredLevels =
      ImmutableMap.<Level, ChatColor>builder()
          .put(Level.ALL, ChatColor.GREEN)
          .put(Level.FINEST, ChatColor.AQUA)
          .put(Level.FINER, ChatColor.AQUA)
          .put(Level.FINE, ChatColor.AQUA)
          .put(Level.CONFIG, ChatColor.LIGHT_PURPLE)
          .put(Level.INFO, ChatColor.GREEN)
          .put(Level.WARNING, ChatColor.GOLD)
          .put(Level.SEVERE, ChatColor.RED)
          .build();
  private static final ImmutableMap<Level, String> levelIdentifiers =
      ImmutableMap.<Level, String>builder()
          .put(Level.ALL, "A")
          .put(Level.FINEST, "FST")
          .put(Level.FINER, "FR")
          .put(Level.FINE, "F")
          .put(Level.CONFIG, "\u2699")
          .put(Level.INFO, "\u2139")
          .put(Level.WARNING, "\u26a0")
          .put(Level.SEVERE, "\u26a0\u26a0")
          .build();
  private final TextStyle prefixStyle;
  private final String prefix;
  private final String readPerm;

  /**
   * Constructor.
   *
   * @param prefixStyle style of the prefix text
   * @param prefix of the message
   * @param readPerm permission needed to see log messages
   */
  public ChatLogHandler(TextStyle prefixStyle, String prefix, String readPerm) {
    this.prefixStyle = prefixStyle;
    this.prefix = prefix;
    this.readPerm = readPerm;
  }

  private BaseComponent formatMessage(
      LogRecord record, BaseComponent prefix, CommandSender sender) {
    prefix = new TextComponent(prefix);
    if (record instanceof BaseComponentLogRecord) {
      prefix.addExtra(((BaseComponentLogRecord) record).getComponent());
    } else if (record instanceof TranslatableLogRecord) {
      prefix.addExtra(((TranslatableLogRecord) record).getComponent().render(sender));
    } else {
      prefix.addExtra(ChatColor.GRAY + record.getMessage());
    }
    return prefix;
  }

  private TextComponent formatPrefix(LogRecord record) {
    String level = levelIdentifiers.get(record.getLevel());
    ChatColor levelColor = coloredLevels.get(record.getLevel());
    TextComponent component = new TextComponent(ChatColor.DARK_BLUE + "[");
    component.addExtra(this.prefixStyle.apply(prefix));
    component.addExtra(ChatColor.DARK_BLUE + "]");
    component.addExtra(ChatColor.DARK_GRAY + ": ");
    component.addExtra(ChatColor.DARK_AQUA + "(" + levelColor + level + ChatColor.DARK_AQUA + ") ");
    component.addExtra(ChatColor.RESET + "");
    return component;
  }

  @Override
  public void publish(LogRecord record) {
    TextComponent prefix = formatPrefix(record);
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.hasPermission(this.readPerm)) {
        p.sendMessage(formatMessage(record, prefix, p));
        if (record instanceof TranslatableLogRecord
            && ((TranslatableLogRecord) record).getSound() != null) {
          ((TranslatableLogRecord) record).getSound().play(p);
        }
      }
    }
    Bukkit.getConsoleSender().sendMessage(formatMessage(record, prefix, Bukkit.getConsoleSender()));

    if (record.getThrown() != null) {
      record.getThrown().printStackTrace();
    }
  }

  @Override
  public void flush() {}

  @Override
  public void close() throws SecurityException {}
}
