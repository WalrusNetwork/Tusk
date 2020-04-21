package network.walrus.uhcworldgen;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * Data about how a would should be generated.
 *
 * @author Austin Mayes
 */
class ConfiguredWorldData {

  final String prefix;
  final int count;
  final int size;
  final boolean nether;
  final boolean end;
  final int priority;
  private final LocalTime start;
  private final LocalTime stop;
  private final DayOfWeek[] activeDays;

  int toDo;

  /**
   * @param prefix of all worlds
   * @param count of world groups to make
   * @param size of each world
   * @param nether if nether worlds should be included in generation groups
   * @param end if end worlds should be included in generation groups
   * @param start time where this generator should be active
   * @param stop time where this generator should no longer be active
   * @param priority of this generator over the others
   * @param activeDays days which this generator should be active on
   */
  ConfiguredWorldData(
      String prefix,
      int count,
      int size,
      boolean nether,
      boolean end,
      LocalTime start,
      LocalTime stop,
      int priority,
      DayOfWeek[] activeDays) {
    this.prefix = prefix;
    this.count = count;
    this.size = size / 2;
    this.nether = nether;
    this.end = end;
    this.start = start;
    this.stop = stop;
    this.priority = priority;
    this.activeDays = activeDays;
    this.toDo = count;
  }

  /**
   * Create a copy of this data using a supplied size reduction factor
   *
   * @param reductionFactor to reduce size by
   * @return exact same data with reduced size
   */
  public ConfiguredWorldData cloneWithSizeReduction(double reductionFactor) {
    return cloneWithSize((int) Math.floor(this.size * 2 * reductionFactor));
  }

  /**
   * Clone the data with a different world size.
   *
   * @param size of the new data
   * @return copy of the data with the new size
   */
  public ConfiguredWorldData cloneWithSize(int size) {
    return new ConfiguredWorldData(
        this.prefix,
        this.count,
        size,
        this.nether,
        this.end,
        this.start,
        this.stop,
        this.priority,
        this.activeDays);
  }

  boolean insideBorder(double xLoc, double zLoc) {
    return !(xLoc < -size || xLoc > size || zLoc < -size || zLoc > size);
  }

  boolean shouldBeActive() {
    DayOfWeek day = DayOfWeek.of(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    boolean onDay = false;
    for (DayOfWeek d : activeDays) {
      if (d == day) {
        onDay = true;
        break;
      }
    }
    if (!onDay) return false;
    LocalTime now = LocalTime.now();
    if (start.equals(stop)) {
      return false;
    } else if (stop.isAfter(start)) {
      return (!now.isBefore(start)) && now.isBefore(stop);
    } else if (stop.isBefore(start)) {
      return (now.equals(start) || now.isAfter(start)) && now.isBefore(stop);
    } else {
      return false;
    }
  }
}
