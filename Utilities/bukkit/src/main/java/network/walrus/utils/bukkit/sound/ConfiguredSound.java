package network.walrus.utils.bukkit.sound;

import com.google.common.collect.Range;
import java.util.Collection;
import java.util.Random;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A customized wrapper for {@link Sound} containing specialized configuration attributes.
 *
 * @author Austin Mayes
 */
public class ConfiguredSound {

  private static final Random RANDOM = new Random();
  private @Nullable Sound sound = null;
  private Range<Float> pitch = Range.singleton(1f);
  private float volume = 1;

  private ConfiguredSound() {}

  /** Create a new instance of this class with no sound. */
  public static ConfiguredSound create() {
    return new ConfiguredSound();
  }

  /**
   * Create a new instance of this class which will wrap the supplied sound.
   *
   * @param sound to wrap
   * @return wrapped sound
   */
  public static ConfiguredSound ofSound(Sound sound) {
    ConfiguredSound configured = create();
    configured.sound = sound;
    return configured;
  }

  /**
   * Set the pitch of this sound to a singular value which will always be used.
   *
   * @param pitch of the sound
   * @return object with modified value
   */
  public ConfiguredSound pitch(float pitch) {
    this.pitch = Range.singleton(pitch);
    return this;
  }

  /**
   * Set the pitch of this sound to a range of values which will randomly be selected on each
   * execution of {@link #play(Player, Location)}.
   *
   * @param lower bound of the pitch of this sound
   * @param upper bound of the pitch of this sound
   * @return object with modified value
   */
  public ConfiguredSound pitch(float lower, float upper) {
    this.pitch = Range.open(lower, upper);
    return this;
  }

  /**
   * Set the volume of this sound.
   *
   * @param volume of the sound
   * @return object with modified value
   */
  public ConfiguredSound volume(float volume) {
    this.volume = volume;
    return this;
  }

  /** If the supplied sender is a {@link Player}, execute {@link #play(Player)} */
  public void play(CommandSender sender) {
    if (sender instanceof Player) {
      play((Player) sender, null);
    }
  }

  /** If the supplied sender is a {@link Player}, execute {@link #play(Player, Location)} */
  public void play(CommandSender sender, Location location) {
    if (sender instanceof Player) {
      play((Player) sender, location);
    }
  }

  /** {@link #play(Player)} with no defined location */
  public void play(Player player) {
    play(player, null);
  }

  /**
   * Play the sound to the specified player with an optional origin location.
   *
   * @param player to play the sound to
   * @param location to use as the sound origin. If the location is {@code null}, the player's
   *     location will be used instead.
   */
  public void play(Player player, Location location) {
    if (sound == null || player == null) {
      return;
    }

    location = location == null ? player.getLocation() : location;
    float chosenPitch =
        pitch.lowerEndpoint()
            + (pitch.upperEndpoint() - pitch.lowerEndpoint()) * RANDOM.nextFloat();
    player.playSound(location, this.sound, volume, chosenPitch);
  }

  /** Execute {@link #play(Player, Location)} for each player in the collection. */
  public void play(Collection<? extends Player> players, Location location) {
    for (Player p : players) {
      play(p, location);
    }
  }

  /** Execute {@link #play(Player)} for each player in the collection. */
  public void play(Collection<? extends Player> players) {
    for (Player player : players) {
      play(player);
    }
  }

  /** @return an exact copy of this sound */
  public ConfiguredSound duplicate() {
    ConfiguredSound sound = ConfiguredSound.ofSound(this.sound);
    sound.volume = this.volume;
    sound.pitch = this.pitch;
    return sound;
  }
}
