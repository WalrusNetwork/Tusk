package network.walrus.nerve.bungee.sound;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * API to have the Bukkit server send a sound to a specified player.
 *
 * @author Rafi Baum
 */
public class SoundSender {

  public static final String SUBCHANNEL = "Nerve-SOUND";

  /**
   * Send a sound to a player.
   *
   * @param player to send a sound to
   * @param soundName as specified in Bukkit sound enum
   * @param volume volume of sound
   * @param pitch pitch of sound
   */
  public static void sendSound(ProxiedPlayer player, String soundName, float volume, float pitch) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Forward");
    out.writeUTF(player.getServer().getInfo().getName());
    out.writeUTF(SUBCHANNEL);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream bytesOut = new DataOutputStream(bytes);
    try {
      bytesOut.writeUTF(soundName);
      bytesOut.writeFloat(volume);
      bytesOut.writeFloat(pitch);
    } catch (IOException e) {
      e.printStackTrace();
    }

    out.writeShort(bytes.toByteArray().length);
    out.write(bytes.toByteArray());
    player.getServer().getInfo().sendData(SUBCHANNEL, out.toByteArray());
  }
}
