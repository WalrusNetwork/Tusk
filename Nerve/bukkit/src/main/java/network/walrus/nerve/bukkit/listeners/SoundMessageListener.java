package network.walrus.nerve.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.GenericStringHolder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Listens for sound messages from Bungee and executes requests.
 *
 * @author Rafi Baum
 */
public class SoundMessageListener implements PluginMessageListener {

  public static final String SUBCHANNEL = "Nerve-SOUND";

  @Override
  public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
    if (!channel.equals(SUBCHANNEL)) {
      return;
    }

    ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

    Sound sound =
        BukkitParserRegistry.ofEnum(Sound.class)
            .parseRequired(new GenericStringHolder(in.readUTF(), null));
    float volume = in.readFloat();
    float pitch = in.readFloat();
    player.playSound(player.getLocation(), sound, volume, pitch);
  }
}
