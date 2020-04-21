package network.walrus.utils.bukkit.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A log record which, when used with a logger using the {@link ChatLogHandler}, passes along the
 * raw component to the players it is being sent to.
 *
 * @author Austin Mayes
 */
public class BaseComponentLogRecord extends LogRecord {

  private final BaseComponent component;

  /**
   * @param level of the record
   * @param component describing the record
   */
  public BaseComponentLogRecord(Level level, BaseComponent component) {
    super(level, component.toLegacyText());
    this.component = component;
  }

  public BaseComponent getComponent() {
    return component;
  }
}
