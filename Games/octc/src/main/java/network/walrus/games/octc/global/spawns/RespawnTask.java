package network.walrus.games.octc.global.spawns;

import java.time.Instant;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.Match;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.compat.CompatTitleScreen;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDamageEvent;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.Respawn;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.Respawn.Subtitle;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.LocalizedText;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

/**
 * Class responsible for transitioning a player from death to their spawn again after a configurable
 * period.
 *
 * @author Avicus Network
 */
@SuppressWarnings("JavaDoc")
public class RespawnTask extends GameTask implements Listener {

  private final Match match;
  private final OCNSpawnManager manager;
  private final Player player;
  private final long respawnTime;
  private final boolean freezePlayer;
  private final boolean blindPlayer;
  private final CompatTitleScreen titleManager;
  private boolean autoRespawn;
  private BaseComponent title;
  private BaseComponent fallbackMessage;
  private int currentTick;
  private boolean sentSound = false;

  /**
   * @param match that the player is spawning in
   * @param manager used to spawn the player back in
   * @param player that this task is for
   * @param respawnTime time when the player should be re-spawned
   * @param options used to determine when the player can do during the spawning phase
   */
  public RespawnTask(
      Match match,
      OCNSpawnManager manager,
      Player player,
      Instant respawnTime,
      RespawnOptions options) {
    super("Respawn: " + player.getName());
    this.match = match;
    this.manager = manager;
    this.player = player;
    this.respawnTime = respawnTime.toEpochMilli();
    this.autoRespawn = options.autoRespawn;
    this.freezePlayer = options.freezePlayer;
    this.blindPlayer = options.blindPlayer;
    this.title =
        GamesCoreMessages.GENERIC_DEATH
            .with(Games.OCN.Respawn.TITLE)
            .render(this.player); // TODO: Custom death screen
    this.fallbackMessage =
        GamesCoreMessages.GENERIC_DEATH_FALLBACK
            .with(OCN.Respawn.FALLBACK, new LocalizedNumber(options.respawnTime.getSeconds()))
            .render(this.player);
    this.titleManager =
        UbiquitousBukkitPlugin.getInstance().getCompatManager().getCompatTitleScreen();
    this.currentTick = 0;
  }

  @EventHandler
  public void onPPlayerDamage(PlayerDamageEvent event) {
    if (event.getEntity().equals(this.player)) {
      event.setCancelled(true);
    }
  }

  /**
   * Start the task.
   *
   * @return the started task
   */
  public RespawnTask start() {
    this.repeat(0, 2);
    EventUtil.register(this);

    if (!this.freezePlayer) {
      this.player.setAllowFlight(true);
      this.player.setFlying(true);
    } else {
      UbiquitousBukkitPlugin.getInstance().getFreezeManager().freeze(player);
    }

    this.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0), true);
    player.playSound(player.getLocation(), Sound.IRONGOLEM_DEATH, 1f, 1f);
    this.player.setVelocity(new Vector());

    this.player.spigot().setCollidesWithEntities(false);
    this.player.spigot().setAffectsSpawning(false);

    if (titleManager.isLegacy(player)) {
      player.sendMessage(fallbackMessage);
    }
    return this;
  }

  @Override
  protected void onEnd() {
    EventUtil.unregister(this);

    if (this.player.isOnline()) {
      if (!titleManager.isLegacy(player)) {
        titleManager.hideTitle(player);
      }

      if (this.freezePlayer) {
        UbiquitousBukkitPlugin.getInstance().getFreezeManager().thaw(player);
      }
    }
  }

  @Override
  public void run() {
    if (!this.player.isOnline()) {
      reset();
      return;
    }

    this.currentTick = currentTick + 2;

    if (this.blindPlayer) {
      this.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1), true);
    }

    long now = System.currentTimeMillis();
    boolean isAfter = now >= this.respawnTime;

    // Match has since ended.
    if (!match.getState().playing()) {
      this.manager.stopRespawnTask(this.player);
      return;
    }

    if ((this.autoRespawn || titleManager.isLegacy(player)) && isAfter) {
      this.manager.spawn(this.player);
      this.manager.stopRespawnTask(this.player);
      return;
    }

    if (!titleManager.isLegacy(player)) {
      LocalizedText subtitle;
      if (isAfter) {
        subtitle = GamesCoreMessages.GENERIC_RESPAWN_PUNCH.with();
        if (!sentSound) {
          Respawn.ALLOWED.play(player);
          sentSound = true;
        }
      } else {
        double seconds = ((double) this.respawnTime - (double) now) / 1000.0;
        LocalizedNumber secondsPart = new LocalizedNumber(seconds, 1, 1, Subtitle.TIME);
        if (this.autoRespawn) {
          subtitle = GamesCoreMessages.GENERIC_RESPAWN_AUTO.with(secondsPart);
        } else {
          subtitle = GamesCoreMessages.GENERIC_RESPAWN_MANUAL.with(secondsPart);
        }
      }
      subtitle.style().inherit(Subtitle.TEXT);

      titleManager.sendTitle(player, new Title(this.title, subtitle.render(player), 0, 40, 20));
    }
  }

  public void setAutoRespawn(boolean autoRespawn) {
    this.autoRespawn = autoRespawn;
  }
}
