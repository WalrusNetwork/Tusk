package network.walrus.games.octc.destroyables.modes;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.DTCM.Modes;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.registry.Identifiable;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.block.Block;

/**
 * A destroyable mode is a set material (and) data of a {@link DestroyableObjective} that is applied
 * to the blocks in this objective after a set delay.
 *
 * <p>A destroyable can have an infinite amount of modes that can be rotated through. Only one mode
 * is active at a time.
 *
 * <p>Modes will also change the materials required to repair the objective.
 *
 * @author Austin Mayes
 */
public class DestroyableMode implements Identifiable<DestroyableMode> {

  /** Round that the mode operates in. */
  private final GameRound round;
  /** ID used for reference in XML. */
  private final String id;
  /** Name of the mode. */
  private final LocalizedConfigurationProperty name;
  /**
   * Message used for the countdown. FORMAT: {0} = the name of the mode. {1} = the amount of time
   * until the mode will be applied.
   */
  private final Optional<LocalizedConfigurationProperty> countdownMessage;
  /** The message that is displayed when the mode is successfully applied. */
  private final Optional<LocalizedConfigurationProperty> changeSuccessMessage;
  /** The message that is displayed when the mode fails to be applied. */
  private final Optional<LocalizedConfigurationProperty> changeFailMessage;
  /** Material that the blocks will be changed to. */
  private final LinkedHashMap<MultiMaterialMatcher, SingleMaterialMatcher> materials;
  /**
   * Delay before the mode is applied. (The countdown will be started at round start OR when the
   * preceding mode is applied.)
   */
  private final Duration delay;
  /** Filter to be ran before the mode is applied. */
  private final Optional<Filter> changeCheck;
  /** Number of times the mode should be attempted to be re-applied if the filter fails. */
  private final int retryAttempts;
  /** Delay between retry attempts. */
  private final Optional<Duration> retryDelay;
  /** mode that will be applied if the filter fails and there are no retry attempts remaining. */
  private final Optional<DestroyableMode> failMode;
  /** mode that will be applied if the filter passes. */
  private final Optional<DestroyableMode> passMode;

  private final HashMap<DestroyableObjective, AtomicInteger> failures = new HashMap<>();

  /**
   * Constructor.
   *
   * @param round round that the mode operates in
   * @param name name of the mode
   * @param countdownMessage Message used for the countdown FORMAT: {0} = the name of the mode {1} =
   *     the amount of time until the mode will be applied
   * @param changeSuccessMessage the message that is displayed when the mode is successfully applied
   * @param changeFailMessage the message that is displayed when the mode fails to be applied
   * @param materials material that the blocks will be changed to
   * @param delay delay before the mode is applied. (The countdown will be started at round start OR
   *     when the preceding mode is applied.)
   * @param changeCheck filter to be ran before the mode is applied
   * @param retryAttempts number of times the mode should be attempted to be re-applied if the
   *     filter fails
   * @param retryDelay delay between retry attempts
   * @param failMode mode that will be applied if the filter fails and there are no retry attempts
   *     remaining
   * @param passMode mode that will be applied if the filter passes
   */
  public DestroyableMode(
      GameRound round,
      String id,
      LocalizedConfigurationProperty name,
      Optional<LocalizedConfigurationProperty> countdownMessage,
      Optional<LocalizedConfigurationProperty> changeSuccessMessage,
      Optional<LocalizedConfigurationProperty> changeFailMessage,
      LinkedHashMap<MultiMaterialMatcher, SingleMaterialMatcher> materials,
      Duration delay,
      Optional<Filter> changeCheck,
      int retryAttempts,
      Optional<Duration> retryDelay,
      Optional<DestroyableMode> failMode,
      Optional<DestroyableMode> passMode) {
    this.round = round;
    this.id = id;
    this.name = name;
    this.countdownMessage = countdownMessage;
    this.changeSuccessMessage = changeSuccessMessage;
    this.changeFailMessage = changeFailMessage;
    this.materials = materials;
    this.delay = delay;
    this.changeCheck = changeCheck;
    this.retryAttempts = retryAttempts;
    this.retryDelay = retryDelay;
    this.failMode = failMode;
    this.passMode = passMode;
  }

  /**
   * Apply the mode to the objective. The apply filter should be ran before this is performed.
   *
   * <p>FIXME: Handle if a block is in mid-break.
   *
   * @param objective objective to apply the mode to
   */
  public void applyMode(DestroyableObjective objective) {
    for (Block block : objective.getRemaining()) {
      for (Entry<MultiMaterialMatcher, SingleMaterialMatcher> e : this.materials.entrySet()) {
        MultiMaterialMatcher key = e.getKey();
        SingleMaterialMatcher value = e.getValue();
        objective.updateMaterial(key, value);
      }
      if (objective.getProperties().materials.matches(block.getType(), block.getData())) {
        for (Entry<MultiMaterialMatcher, SingleMaterialMatcher> entry : this.materials.entrySet()) {
          MultiMaterialMatcher find = entry.getKey();
          SingleMaterialMatcher replace = entry.getValue();
          if (find.matches(block.getType(), block.getData())) {
            block.setType(replace.material());
            block.setData(replace.data().orElse((byte) 0));
          }
        }
      }
    }
  }

  /**
   * Determine if the mode should be applied to the objective.
   *
   * @param objective objective to run the filter on
   * @return if the mode should be applied to the objective
   */
  public boolean shouldApply(DestroyableObjective objective) {
    FilterContext context = new FilterContext();
    return !this.changeCheck.isPresent() || this.changeCheck.get().test(context).passes();
  }

  Optional<ModeApplicationCountdown> attemptApply(List<DestroyableObjective> objectives) {
    boolean pass = true;

    for (DestroyableObjective objective : objectives) {
      if (!shouldApply(objective)) {
        pass = false;
        this.failures.putIfAbsent(objective, new AtomicInteger());
        this.failures.get(objective).addAndGet(1);
      }
    }

    Optional<DestroyableMode> next = getNextMode(pass);

    if (pass) {
      Localizable success =
          this.changeSuccessMessage
              .map(LocalizedConfigurationProperty::toText)
              .orElse(OCNMessages.MODE_CHANGE_SUCCESS.with(this.name.toText()));
      this.round.getContainer().broadcast(success);
      this.round.getContainer().broadcast(Modes.CHANGE_SUCCESS);
      for (DestroyableObjective objective : objectives) {
        applyMode(objective);
      }
      if (next.isPresent()) {
        return Optional.of(
            new ModeApplicationCountdown(
                next.get().getMatch(), next.get().getDelay(), next.get(), objectives));
      }
    } else {
      Localizable fail =
          this.changeFailMessage
              .map(LocalizedConfigurationProperty::toText)
              .orElse(OCNMessages.MODE_CHANGE_FAIL.with(this.name.toText()));
      this.round.getContainer().broadcast(fail);
      this.round.getContainer().broadcast(Modes.CHANGE_FAIL);

      List<DestroyableObjective> toRetry = new ArrayList<>();
      for (Entry<DestroyableObjective, AtomicInteger> entry : this.failures.entrySet()) {
        if (entry.getValue().get() < this.retryAttempts) {
          DestroyableObjective key = entry.getKey();
          toRetry.add(key);
        }
      }

      if (!(toRetry.isEmpty())) {
        if (this.retryDelay.isPresent()) {
          return Optional.of(new ModeApplicationCountdown(round, retryDelay.get(), this, toRetry));
        }
      }

      if (next.isPresent()) {
        return Optional.of(
            new ModeApplicationCountdown(
                next.get().getMatch(), next.get().getDelay(), next.get(), objectives));
      }
    }

    return Optional.empty();
  }

  /**
   * Get the mode that should be used after this one based on if this one was applied successfully.
   *
   * @param pass if the current mode was successfully applied
   * @return the mode after this one based on application success
   */
  public Optional<DestroyableMode> getNextMode(boolean pass) {
    return pass ? this.passMode : this.failMode;
  }

  @Override
  public DestroyableMode object() {
    return this;
  }

  @Override
  public String id() {
    return this.id;
  }

  public GameRound getMatch() {
    return round;
  }

  public Duration getDelay() {
    return delay;
  }

  public Localizable getCountdownMessage() {
    return countdownMessage
        .map(LocalizedConfigurationProperty::toText)
        .orElse(OCNMessages.MODE_CHANGE_COUNTDOWN.with());
  }

  public LocalizedConfigurationProperty getName() {
    return name;
  }
}
