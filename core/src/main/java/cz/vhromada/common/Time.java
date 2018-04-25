package cz.vhromada.common;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.util.Assert;

/**
 * A class represents time.
 *
 * @author Vladimir Hromada
 */
public final class Time implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Count of hours in day
     */
    private static final int DAY_HOURS = 24;

    /**
     * Count of seconds in hour
     */
    private static final int HOUR_SECONDS = 3600;

    /**
     * Count of seconds in minute
     */
    private static final int MINUTE_SECONDS = 60;

    /**
     * Minimum minutes or seconds
     */
    private static final int MIN_TIME = 0;

    /**
     * Maximum minutes or seconds
     */
    private static final int MAX_TIME = 59;

    /**
     * Time in seconds
     */
    private final int length;

    /**
     * Data
     */
    private final Map<TimeData, Integer> data;

    /**
     * Creates a new instance of Time.
     *
     * @param length time in seconds
     * @throws IllegalArgumentException if time in seconds is negative number
     */
    public Time(final int length) {
        Assert.isTrue(length >= 0L, "Length mustn't be negative number.");

        this.length = length;
        this.data = new EnumMap<>(TimeData.class);
        this.data.put(TimeData.HOUR, length / HOUR_SECONDS);
        final int temp = length % HOUR_SECONDS;
        this.data.put(TimeData.MINUTE, temp / MINUTE_SECONDS);
        this.data.put(TimeData.SECOND, temp % MINUTE_SECONDS);
    }

    /**
     * Creates a new instance of Time.
     *
     * @param hours   hours
     * @param minutes minutes
     * @param seconds seconds
     * @throws IllegalArgumentException if hours is negative number
     *                                  or minutes isn't between 0 and 59
     *                                  or seconds isn't between 0 and 59
     */
    public Time(final int hours, final int minutes, final int seconds) {
        Assert.isTrue(hours >= 0L, "Hours mustn't be negative number.");
        Assert.isTrue(minutes >= MIN_TIME && minutes <= MAX_TIME, "Minutes must be between " + MIN_TIME + " and " + MAX_TIME + '.');
        Assert.isTrue(seconds >= MIN_TIME && seconds <= MAX_TIME, "Seconds must be between " + MIN_TIME + " and " + MAX_TIME + '.');

        this.length = hours * HOUR_SECONDS + minutes * MINUTE_SECONDS + seconds;
        this.data = new EnumMap<>(TimeData.class);
        this.data.put(TimeData.HOUR, hours);
        this.data.put(TimeData.MINUTE, minutes);
        this.data.put(TimeData.SECOND, seconds);
    }

    /**
     * Returns time in seconds.
     *
     * @return time in seconds
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns data.
     *
     * @param dataType data type
     * @return data
     * @throws IllegalArgumentException if data type is null
     */
    public int getData(final TimeData dataType) {
        Assert.notNull(dataType, "Data type mustn't be null.");

        return data.get(dataType);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Time)) {
            return false;
        }

        return length == ((Time) obj).length;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(length);
    }

    @Override
    public String toString() {
        final int days = data.get(TimeData.HOUR) / DAY_HOURS;
        final int hours = data.get(TimeData.HOUR) % DAY_HOURS;
        if (days > 0) {
            return String.format("%d:%02d:%02d:%02d", days, hours, data.get(TimeData.MINUTE), data.get(TimeData.SECOND));
        }

        return String.format("%d:%02d:%02d", hours, data.get(TimeData.MINUTE), data.get(TimeData.SECOND));
    }

    /**
     * An enumeration represents time.
     *
     * @author Vladimir Hromada
     */
    public enum TimeData {

        /**
         * Hour
         */
        HOUR,

        /**
         * Minute
         */
        MINUTE,

        /**
         * Second
         */
        SECOND

    }

}
