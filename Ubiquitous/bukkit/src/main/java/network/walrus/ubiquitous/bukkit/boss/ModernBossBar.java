package network.walrus.ubiquitous.bukkit.boss;

import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossFlag;
import us.myles.ViaVersion.api.boss.BossStyle;

/**
 * A modern boss bar.
 *
 * <p>
 *
 * <p>A modern boss bar is used for 1.9, 1.10, and 1.11 clients.
 *
 * @author kashike
 */
public class ModernBossBar extends BossBar {

  /** An array of colors that a modern boss bar supports. */
  private static final BossColor[] COLORS = BossColor.values();
  /** An array of overlays that a modern boss bar supports. */
  private static final BossStyle[] OVERLAYS = BossStyle.values();
  /** The boss bar container. */
  private us.myles.ViaVersion.api.boss.BossBar<Player> bar;

  /**
   * Constructs a new modern boss bar.
   *
   * @param player the player who is viewing this boss bar
   */
  ModernBossBar(Player player) {
    // A normal (as seen in versions before 1.9) boss bar has a PROGRESS (SOLID) overlay and is PINK
    // in color
    this.bar = Via.getAPI().createBossBar("Free VBucks", BossColor.PINK, BossStyle.SOLID);
    this.bar.addPlayer(player.getUniqueId());
  }

  @Override
  public void destroy() {
    final UUID uniqueId = this.bar.getPlayers().iterator().next();
    this.bar.removePlayer(uniqueId);
    this.bar = null;
  }

  @Override
  public ModernBossBar setName(BaseComponent[] name) {
    if (name != this.name) {
      super.setName(name);
      this.bar.setTitle(BaseComponent.toLegacyText(name));
    }

    return this;
  }

  @Override
  public ModernBossBar setPercent(float percent) {
    if (percent != this.percent) {
      super.setPercent(percent);
      this.bar.setHealth(percent);
    }

    return this;
  }

  @Override
  public ModernBossBar setColor(BossBarColor color) {
    if (color != this.color) {
      super.setColor(color);
      this.bar.setColor(COLORS[color.ordinal()]);
    }

    return this;
  }

  @Override
  public ModernBossBar setOverlay(BossBarOverlay overlay) {
    if (overlay != this.overlay) {
      super.setOverlay(overlay);
      this.bar.setStyle(OVERLAYS[overlay.ordinal()]);
    }

    return this;
  }

  @Override
  public ModernBossBar setDarkenSky(boolean darkenSky) {
    if (darkenSky != this.darkenSky) {
      super.setDarkenSky(darkenSky);

      if (darkenSky) {
        this.bar.addFlag(BossFlag.DARKEN_SKY);
      } else {
        this.bar.removeFlag(BossFlag.DARKEN_SKY);
      }
    }

    return this;
  }

  @Override
  public ModernBossBar setPlayEndBossMusic(boolean playEndBossMusic) {
    if (playEndBossMusic != this.playEndBossMusic) {
      super.setPlayEndBossMusic(playEndBossMusic);

      if (playEndBossMusic) {
        this.bar.addFlag(BossFlag.PLAY_BOSS_MUSIC);
      } else {
        this.bar.removeFlag(BossFlag.PLAY_BOSS_MUSIC);
      }
    }

    return this;
  }

  /*
   * This method intentionally uses PLAY_BOSS_MUSIC for addFlag and removeFlag - vanilla
   * treats them both (PLAY_BOSS_MUSIC and CREATE_FOG) the same in the network, so ViaVersion
   * decided not to add a BossFlag for it.
   */
  @Override
  public ModernBossBar setCreateFog(boolean createFog) {
    if (createFog != this.createFog) {
      super.setCreateFog(createFog);

      if (createFog) {
        this.bar.addFlag(BossFlag.PLAY_BOSS_MUSIC);
      } else {
        this.bar.removeFlag(BossFlag.PLAY_BOSS_MUSIC);
      }
    }

    return this;
  }

  @Override
  public BossBar setVisible(boolean visible) {
    if (visible != this.visible) {
      if (visible) {
        this.bar.show();
      } else {
        this.bar.hide();
      }
    }

    return super.setVisible(visible);
  }
}
