package network.walrus.utils.bukkit.sound;

import network.walrus.utils.core.config.GenericStringHolder;
import network.walrus.utils.core.parse.CoreParserRegistry;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.util.FileBackedKVSet;
import org.bukkit.Sound;

// TODO: Move to Nerve
/**
 * A collection of {@link ConfiguredSound}s mapped by key.
 *
 * @author Austin Mayes
 */
public class SoundBundle extends FileBackedKVSet<ConfiguredSound> {

  @Override
  protected ConfiguredSound parse(String raw) {
    if (raw.trim().isEmpty()) return null;
    if (raw.trim().equalsIgnoreCase("none")) {
      return SoundInjector.$NULL$.duplicate();
    }
    String[] parts = raw.split(":");
    SimpleParser<Float> parser = CoreParserRegistry.floatParser();
    ConfiguredSound sound =
        ConfiguredSound.ofSound(
            CoreParserRegistry.ofEnum(Sound.class)
                .parseRequired(new GenericStringHolder(parts[0], null)));
    if (parts.length > 1) {
      String[] pitch = parts[1].split("-");
      if (pitch.length == 1)
        sound.pitch(parser.parseRequired(new GenericStringHolder(pitch[0], null)));
      else
        sound.pitch(
            parser.parseRequired(new GenericStringHolder(pitch[0], null)),
            parser.parseRequired(new GenericStringHolder(pitch[1], null)));
    }
    if (parts.length > 2) {
      sound.volume(parser.parseRequired(new GenericStringHolder(parts[2], null)));
    }
    return sound;
  }
}
