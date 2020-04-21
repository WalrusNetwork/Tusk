package network.walrus.utils.core.util;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Locale;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.impl.ResourcesTimeFormat;
import org.ocpsoft.prettytime.units.JustNow;

/**
 * Various utilities involving java.time.
 *
 * @author Overcast Network
 */
public class TimeUtils {

  public static final Duration INF_POSITIVE = ChronoUnit.FOREVER.getDuration();
  public static final Duration INF_NEGATIVE = INF_POSITIVE.negated();
  public static final Instant INF_FUTURE = Instant.MAX;
  public static final Instant INF_PAST = Instant.MIN;

  /**
   * Determine if a duration is finite.
   *
   * @param duration to check
   * @return if the duration is finite
   */
  public static boolean isFinite(Duration duration) {
    return !isInfPositive(duration) && !isInfNegative(duration);
  }

  /**
   * Determine if a duration will last until the foreseeable end of the world.
   *
   * @param duration to check
   * @return if the duration is positively infinite
   */
  public static boolean isInfPositive(Duration duration) {
    return INF_POSITIVE.equals(duration);
  }

  /**
   * Determine if a duration is negatively infinite.
   *
   * @param duration to check
   * @return if the duration is illicitly negative
   */
  public static boolean isInfNegative(Duration duration) {
    return INF_NEGATIVE.equals(duration);
  }

  /**
   * Determine if an instant has not happened yet and will never happen.
   *
   * @param instant to check
   * @return if this is a Back to the Future II scenario
   */
  public static boolean isInfFuture(Instant instant) {
    return INF_FUTURE.equals(instant);
  }

  /**
   * Determine if an instant literally occurred when the universe was created.
   *
   * @param instant to check
   * @return if this is a Back to the Future III scenario
   */
  public static boolean isInfPast(Instant instant) {
    return INF_PAST.equals(instant);
  }

  /**
   * Checks if the duration of the unit is not an estimate.
   *
   * @param unit to check for precision
   * @return if the unit's duration is exact
   */
  public static boolean isPrecise(TemporalUnit unit) {
    return !unit.isDurationEstimated() || ChronoUnit.DAYS.equals(unit);
  }

  /**
   * Convert a duration to microseconds.
   *
   * @param duration to convert
   * @return microsecond length of the duration
   */
  public static long toMicros(Duration duration) {
    return Math.addExact(
        Math.multiplyExact(duration.getSeconds(), 1_000_000), duration.getNano() / 1_000);
  }

  /**
   * Get the number of temporal units inside of a duration.
   *
   * @param unit to calculate inside of duration
   * @param duration to retrieve units from
   * @return number of units in the duration
   */
  public static long toUnit(TemporalUnit unit, Duration duration) {
    switch ((ChronoUnit) unit) {
      case NANOS:
        return duration.toNanos();
      case MICROS:
        return toMicros(duration);
      case MILLIS:
        return duration.toMillis();
      case SECONDS:
        return duration.getSeconds();
    }

    if (unit.getDuration().getNano() == 0) {
      return duration.getSeconds() / unit.getDuration().getSeconds();
    }

    throw new IllegalArgumentException("Unsupported sub-second unit " + unit);
  }

  /**
   * Get the number of units a temporal amount represents, or return a default value if the amount
   * is less than the supplied unit definition
   *
   * @param period to determine number of units inside of
   * @param unit which make up the amount
   * @param def to be returned if the amount is less than the unit
   * @return units the amount represents, or default is amount is less than unit length
   */
  public static long getUnitOrDefault(TemporalAmount period, TemporalUnit unit, long def) {
    return period.getUnits().contains(unit) ? period.get(unit) : def;
  }

  /** See {@link #getUnitOrDefault(TemporalAmount, TemporalUnit, long)} with a default of 0. */
  public static long getUnitOrZero(TemporalAmount period, TemporalUnit unit) {
    return getUnitOrDefault(period, unit, 0L);
  }

  /**
   * Get the length between two instants
   *
   * @param start to be used as 0
   * @param end to be used as the max of the duration
   * @return duration between the instants, or an infinite possitive duration if either of the
   *     instants are in infinity territory
   */
  public static Duration duration(Instant start, Instant end) {
    if (isInfPast(start) || isInfFuture(end)) {
      return INF_POSITIVE;
    } else if (start.isBefore(end)) {
      return Duration.between(start, end);
    } else {
      return Duration.ZERO;
    }
  }

  /** See {@link #duration(Instant, Instant)} with the start being now. */
  public static Duration durationUntil(Instant end) {
    return duration(Instant.now(), end);
  }

  /** See {@link #duration(Instant, Instant)} with the end being now. */
  public static Duration durationSince(Instant start) {
    return duration(start, Instant.now());
  }

  /**
   * Determine if the date will occur in the infinite future. This is written to match the ruby
   * implementation of the same name.
   *
   * @param date to check
   * @return if the date is infinitely in the future (according to ruby)
   */
  public static boolean isInfFuture(Date date) {
    return date.getYear() > 8000; // Hacky, but needs to match Ruby's Time::INF_FUTURE
  }

  /**
   * Determine if this date occurred before the creation of universe.
   *
   * @param date to check
   * @return if this date existed in the empty vacuum of space.
   */
  public static boolean isInfPast(Date date) {
    return date.getYear() < -10000;
  }

  /**
   * Convert a Date into an Instant, while also handling infinite dates.
   *
   * @param date to convert to an instant
   * @return the instant representation of the supplied date
   */
  public static Instant toInstant(Date date) {
    if (isInfFuture(date)) {
      return INF_FUTURE;
    } else if (isInfPast(date)) {
      return INF_PAST;
    } else {
      return date.toInstant();
    }
  }

  /**
   * Get the number of days a duration represents, while always rounding up if a duration doesn't
   * last a full day.
   *
   * @param duration to get days of
   * @return rounded days of the duration
   */
  public static long daysRoundingUp(Duration duration) {
    final long days = duration.toDays();
    return duration.equals(Duration.ofDays(days)) ? days : days + 1;
  }

  /**
   * Convert a duration to Minecraft game ticks,
   *
   * @param duration to convert to ticks
   * @return the ticks representation of the duration
   */
  public static long toTicks(Duration duration) {
    return duration.toMillis() / 50;
  }

  /**
   * Get a duration calculated from Minecraft game ticks,
   *
   * @param ticks to convert to a duration
   * @return the duration representation of the ticks
   */
  public static Duration fromTicks(long ticks) {
    return Duration.ofMillis(50 * ticks);
  }

  /**
   * Get the smallest (closest to negative infinity) duration of the two supplied durations.
   *
   * @param a to compare
   * @param b to be compared to
   * @return the smallest duration of the two
   */
  public static Duration min(Duration a, Duration b) {
    return a.compareTo(b) <= 0 ? a : b;
  }

  /**
   * Get the largest (closest to positive infinity) duration of the two supplied durations.
   *
   * @param a to compare
   * @param b to be compared to
   * @return the largest duration of the two
   */
  public static Duration max(Duration a, Duration b) {
    return a.compareTo(b) >= 0 ? a : b;
  }

  /**
   * Get the smallest (closest to negative infinity) instant of the two supplied instants.
   *
   * @param a to compare
   * @param b to be compared to
   * @return the smallest instant of the two
   */
  public static Instant min(Instant a, Instant b) {
    return a.compareTo(b) <= 0 ? a : b;
  }

  /**
   * Get the largest (closest to positive infinity) instant of the two supplied instants.
   *
   * @param a to compare
   * @param b to be compared to
   * @return the largest instant of the two
   */
  public static Instant max(Instant a, Instant b) {
    return a.compareTo(b) >= 0 ? a : b;
  }

  /**
   * Check if an instant is equal to, or will occur before, another instant.
   *
   * @param now to be used as current time
   * @param instant to check for position
   * @return if the instant has already occurred
   */
  public static boolean isEqualOrBeforeNow(Instant now, Instant instant) {
    return !instant.isAfter(now);
  }

  /**
   * Check if the instant is right now, or has already happened.
   *
   * @param instant to check
   * @return if the instant has already occurred
   */
  public static boolean isEqualOrBeforeNow(Instant instant) {
    return isEqualOrBeforeNow(Instant.now(), instant);
  }

  /**
   * Check if an instant is equal to, or will occur after, another instant.
   *
   * @param now to be used as current time
   * @param instant to check for position
   * @return if the instant hasn't already occurred
   */
  public static boolean isEqualOrAfterNow(Instant now, Instant instant) {
    return !instant.isBefore(now);
  }

  /**
   * Check if the instant is right now, or will happen in the future.
   *
   * @param instant to check
   * @return if the instant hasn't already occurred
   */
  public static boolean isEqualOrAfterNow(Instant instant) {
    return isEqualOrAfterNow(Instant.now(), instant);
  }

  /**
   * Parse a duration, and treat simple numbers with no declaration as pure seconds.
   *
   * @param text to parse
   * @return the parsed duration
   * @throws DateTimeParseException if parsing fails
   */
  public static Duration parseDurationOrSeconds(String text) throws DateTimeParseException {
    if ("oo".equals(text)) {
      return INF_POSITIVE;
    }

    // If text looks like a plain number, try to parse it as seconds,
    // but be fairly strict so we don't accidentally parse a time as
    // a number.
    if (text.matches("^\\s*-?[0-9]+(\\.[0-9]+)?\\s*$")) {
      try {
        return Duration.ofMillis((long) (1000 * Double.parseDouble(text)));
      } catch (NumberFormatException ignored) {
      }
    }

    return parseDuration(text);
  }

  /**
   * See {@link #parseDurationOrSeconds(String)}. If the string is empty, the default argument will
   * be returned.
   *
   * @throws DateTimeParseException if parsing of the text fails
   */
  public static Duration parseDurationOrSeconds(String text, Duration def)
      throws DateTimeParseException {
    if (text == null || text.length() == 0) {
      return def;
    } else {
      return parseDurationOrSeconds(text);
    }
  }

  /**
   * Add to an instant by a specific duration, while also handling positive and negative infinity
   * appropriately.
   *
   * @param instant to be added to
   * @param add to add to the instant
   * @return the added instant, or positive/negative infinity based on arguments
   */
  public static Instant plus(Instant instant, Duration add) {
    if (isInfFuture(instant)) {
      return INF_FUTURE;
    } else if (isInfPast(instant)) {
      return INF_PAST;
    } else if (isInfPositive(add)) {
      return INF_FUTURE;
    } else if (isInfNegative(add)) {
      return INF_PAST;
    } else {
      return instant.plus(add);
    }
  }

  /**
   * Subtract an instant by a specific duration, while also handling positive and negative infinity
   * appropriately.
   *
   * @param instant to be subtracted from
   * @param sub to subtract from the instant
   * @return the subtracted instant, or positive/negative infinity based on arguments
   */
  public static Instant minus(Instant instant, Duration sub) {
    if (isInfFuture(instant)) {
      return INF_FUTURE;
    } else if (isInfPast(instant)) {
      return INF_PAST;
    } else if (isInfPositive(sub)) {
      return INF_PAST;
    } else if (isInfNegative(sub)) {
      return INF_FUTURE;
    } else {
      return instant.minus(sub);
    }
  }

  /**
   * Multiply a duration by a single factor.
   *
   * @param duration to be multiplied
   * @param factor to multiply the duration by
   * @return the multiplied duration
   */
  public static Duration multiply(Duration duration, double factor) {
    final long nanosPerSecond = ChronoUnit.SECONDS.getDuration().toNanos();
    final long nanos = (long) (duration.getNano() * factor);
    return Duration.ofSeconds(
        Math.addExact(
            (long) (duration.getSeconds() * factor), Math.floorDiv(nanos, nanosPerSecond)),
        Math.floorMod(nanos, nanosPerSecond));
  }

  /**
   * Trim nanoseconds off of a {@link Duration} using ceiling rounding.
   *
   * @param duration to trim
   * @return the rounded duration
   */
  public static Duration ceilSeconds(Duration duration) {
    return duration.getNano() == 0 ? duration : Duration.ofSeconds(duration.getSeconds());
  }

  /**
   * Helper method to compare the durations of two {@link TemporalUnit}s.
   *
   * @param a base object
   * @param b object to compare the base with
   * @return comparison between two durations
   */
  public static int compareUnits(TemporalUnit a, TemporalUnit b) {
    return a.getDuration().compareTo(b.getDuration());
  }

  /**
   * Create a {@link PrettyTime} instance for a specific locale.
   *
   * @param locale to create the instance for
   * @return a pretty time instance for the supplied locale
   */
  public static PrettyTime prettyTime(Locale locale) {
    final PrettyTime pretty = new PrettyTime(locale);
    // "moments ago" only shows when less than 5 seconds
    for (TimeUnit unit : pretty.getUnits()) {
      if (unit instanceof JustNow) {
        ((JustNow) unit).setMaxQuantity(10000L);
      }
    }
    return pretty;
  }

  /**
   * Remove all future suffixes from a {@link PrettyTime} instance.
   *
   * @param pt to remove suffixes from
   * @return the time instance with no future suffixes
   */
  public static PrettyTime removeFutureSuffix(PrettyTime pt) {
    for (TimeUnit unit : pt.getUnits()) {
      ((ResourcesTimeFormat) pt.getFormat(unit)).setFutureSuffix("");
    }
    return pt;
  }

  /**
   * Parse a {@link Duration} from a formatted string, adding on the needed prefix as needed.
   *
   * @param text to parse
   * @return the duration represented by the string
   * @throws DateTimeParseException if parsing fails
   */
  public static Duration parseDuration(CharSequence text) throws DateTimeParseException {
    if (text.length() > 0) {
      switch (text.charAt(0)) {
        case '-':
        case 'P':
        case 'p':
          return Duration.parse(text);
      }
    }

    return Duration.parse("PT" + text);
  }
}
