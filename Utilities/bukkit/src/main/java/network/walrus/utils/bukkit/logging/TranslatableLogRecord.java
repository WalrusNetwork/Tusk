package network.walrus.utils.bukkit.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.annotation.Nullable;
import network.walrus.common.text.PersonalizedComponent;
import network.walrus.utils.bukkit.sound.ConfiguredSound;
import org.bukkit.Bukkit;

/**
 * A log record which, when used with a logger using the {@link ChatLogHandler}, passes along the
 * raw component allowing it to be translated personally for the players it is being sent to.
 *
 * @author Austin Mayes
 */
public class TranslatableLogRecord extends LogRecord {

  private final PersonalizedComponent component;
  private final @Nullable ConfiguredSound sound;

  /**
   * @param level of the record
   * @param component describing the record
   */
  public TranslatableLogRecord(Level level, PersonalizedComponent component) {
    this(level, component, null);
  }

  /**
   * @param level of the record
   * @param component describing the record
   */
  public TranslatableLogRecord(
      Level level, PersonalizedComponent component, ConfiguredSound sound) {
    super(level, component.render(Bukkit.getConsoleSender()).toLegacyText());
    this.component = component;
    this.sound = sound;
  }

  public PersonalizedComponent getComponent() {
    return component;
  }

  @Nullable
  public ConfiguredSound getSound() {
    return sound;
  }
}
