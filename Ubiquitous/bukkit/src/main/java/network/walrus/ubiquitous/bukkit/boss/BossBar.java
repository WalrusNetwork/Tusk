package network.walrus.ubiquitous.bukkit.boss;

import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A boss bar.
 *
 * @author kashike
 */
public abstract class BossBar {

  /** The unique id of the boss bar. */
  private final UUID uniqueId;
  /** The boss bar name. */
  BaseComponent[] name;
  /** The percentage of the boss bar that is filled with color. */
  float percent;
  /** The color of the boss bar. */
  BossBarColor color;
  /** The overlay of the boss bar. */
  BossBarOverlay overlay;
  /** If the sky should darken. */
  boolean darkenSky;
  /** If the end boss music should be played. */
  boolean playEndBossMusic;
  /** If fog should be created. */
  boolean createFog;
  /** If the boss bar is visible. */
  boolean visible;

  /** Constructs a new boss bar with a random unique id. */
  BossBar() {
    this(UUID.randomUUID());
  }

  /**
   * Constructs a boss bar.
   *
   * @param uniqueId the unique id
   */
  private BossBar(UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  /**
   * Destroys this boss bar.
   *
   * <p>
   *
   * <p>When called, any resources used by this boss bar are cleaned up and released.
   */
  public abstract void destroy();

  /**
   * Gets the unique id of this boss bar.
   *
   * @return the unique id
   */
  public UUID getUniqueId() {
    return this.uniqueId;
  }

  /**
   * Gets the name of this boss bar.
   *
   * @return the name
   */
  public BaseComponent[] getName() {
    return this.name;
  }

  /**
   * Sets the name of this boss bar.
   *
   * @param name the name
   * @return this boss bar
   * @see #setName(BaseComponent[])
   */
  public BossBar setName(BaseComponent name) {
    return this.setName(new BaseComponent[] {name});
  }

  /**
   * Sets the name of this boss bar.
   *
   * @param name the name
   * @return this boss bar
   */
  public BossBar setName(BaseComponent[] name) {
    this.name = name;
    return this;
  }

  /**
   * Gets the percentage of this boss bar that is filled with color.
   *
   * @return the percentage of this boss bar that is filled with color
   */
  public float getPercent() {
    return this.percent;
  }

  /**
   * Sets the percentage of this boss bar that is filled with color.
   *
   * <p>
   *
   * <p>The percentage must be between {@code 0.0} and {@code 1.0}, inclusive.
   *
   * @param percent the percent
   * @return this boss bar
   */
  public BossBar setPercent(float percent) {
    this.percent = percent;
    return this;
  }

  /**
   * Gets the color of this boss bar.
   *
   * @return the color
   */
  public BossBarColor getColor() {
    return this.color;
  }

  /**
   * Sets the color of this boss bar.
   *
   * @param color the color
   * @return this boss bar
   */
  public BossBar setColor(BossBarColor color) {
    this.color = color;
    return this;
  }

  /**
   * Gets the overlay of this boss bar.
   *
   * @return the overlay
   */
  public BossBarOverlay getOverlay() {
    return this.overlay;
  }

  /**
   * Sets the overlay of this boss bar.
   *
   * @param overlay the overlay
   * @return this boss bar
   */
  public BossBar setOverlay(BossBarOverlay overlay) {
    this.overlay = overlay;
    return this;
  }

  /**
   * If the sky should darken.
   *
   * @return {@code true} if the sky should darken, {@code false} otherwise
   */
  public boolean shouldDarkenSky() {
    return this.darkenSky;
  }

  /**
   * Sets if the sky should darken.
   *
   * @param darkenSky {@code true} if the sky should darken, {@code false} otherwise
   * @return this boss bar
   */
  public BossBar setDarkenSky(boolean darkenSky) {
    this.darkenSky = darkenSky;
    return this;
  }

  /**
   * Determines if the end boss music should be played.
   *
   * @return {@code true} if the end boss music should be played, {@code false} otherwise
   */
  public boolean shouldPlayEndBossMusic() {
    return this.playEndBossMusic;
  }

  /**
   * Sets if the end boss music should be played.
   *
   * @param playEndBossMusic {@code true} if the end boss music should be played, {@code false}
   *     otherwise
   * @return this boss bar
   */
  public BossBar setPlayEndBossMusic(boolean playEndBossMusic) {
    this.playEndBossMusic = playEndBossMusic;
    return this;
  }

  /**
   * Determines if fog should be created.
   *
   * @return {@code true} if fog should be created, {@code false} otherwise
   */
  public boolean shouldCreateFog() {
    return this.createFog;
  }

  /**
   * Sets if fog should be created.
   *
   * @param createFog {@code true} if fog should be created, {@code false} otherwise
   * @return this boss bar
   */
  public BossBar setCreateFog(boolean createFog) {
    this.createFog = createFog;
    return this;
  }

  /**
   * Determines if this boss bar is visible.
   *
   * @return {@code true} if this boss bar is visible, {@code false} otherwise
   */
  public boolean isVisible() {
    return this.visible;
  }

  /**
   * Sets if this boss bar is visible.
   *
   * @param visible {@code true} if this boss bar is visible, {@code false} otherwise
   * @return this boss bar
   */
  public BossBar setVisible(boolean visible) {
    this.visible = visible;
    return this;
  }
}
