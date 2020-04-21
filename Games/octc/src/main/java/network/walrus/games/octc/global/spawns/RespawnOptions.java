package network.walrus.games.octc.global.spawns;

import java.time.Duration;
import network.walrus.games.octc.Match;

/**
 * Data class to represent global respawn options for a {@link Match}
 *
 * @author Austin Mayes
 */
public class RespawnOptions {

  final Duration respawnTime;
  final boolean freezePlayer;
  final boolean blindPlayer;
  final boolean autoRespawn;

  /**
   * Constructor.
   *
   * @param respawnTime time before the player is allowed to (re)spawn
   * @param freezePlayer if the player should be frozen while they are waiting to spawn
   * @param blindPlayer if the player should be blinded while they are waiting to spawn
   * @param autoRespawn if, when the {@link #respawnTime} runs out, the player should be
   *     automatically spawned in
   */
  public RespawnOptions(
      Duration respawnTime, boolean freezePlayer, boolean blindPlayer, boolean autoRespawn) {
    this.respawnTime = respawnTime;
    this.freezePlayer = freezePlayer;
    this.blindPlayer = blindPlayer;
    this.autoRespawn = autoRespawn;
  }
}
