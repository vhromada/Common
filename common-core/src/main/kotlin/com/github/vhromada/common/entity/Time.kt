package com.github.vhromada.common.entity

import java.io.Serializable
import java.util.Objects

/**
 * A class represents time.
 *
 * @author Vladimir Hromada
 */
class Time : Serializable {

    /**
     * Time in seconds
     */
    val length: Int

    /**
     * Data
     */
    private val data: Map<TimeData, Int>

    /**
     * Creates a new instance of Time.
     *
     * @param length time in seconds
     * @throws IllegalArgumentException if time in seconds is negative number
     */
    constructor(length: Int) {
        require(length >= 0L) { "Length mustn't be negative number." }

        this.length = length
        val temp = length % HOUR_SECONDS
        this.data = mapOf(TimeData.HOUR to length / HOUR_SECONDS, TimeData.MINUTE to temp / MINUTE_SECONDS, TimeData.SECOND to temp % MINUTE_SECONDS)
    }

    /**
     * Creates a new instance of Time.
     *
     * @param hours   hours
     * @param minutes minutes
     * @param seconds seconds
     * @throws IllegalArgumentException if hours is negative number
     * or minutes isn't between 0 and 59
     * or seconds isn't between 0 and 59
     */
    constructor(hours: Int, minutes: Int, seconds: Int) {
        require(hours >= 0L) { "Hours mustn't be negative number." }
        require(minutes in MIN_TIME..MAX_TIME) { "Minutes must be between $MIN_TIME and $MAX_TIME." }
        require(seconds in MIN_TIME..MAX_TIME) { "Seconds must be between $MIN_TIME and $MAX_TIME." }

        this.length = hours * HOUR_SECONDS + minutes * MINUTE_SECONDS + seconds
        this.data = mapOf(TimeData.HOUR to hours, TimeData.MINUTE to minutes, TimeData.SECOND to seconds)
    }

    /**
     * Returns data.
     *
     * @param dataType data type
     * @return data
     */
    fun getData(dataType: TimeData): Int {
        return data[dataType]!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is Time) {
            false
        } else {
            length == other.length
        }
    }

    override fun hashCode(): Int {
        return Objects.hashCode(length)
    }

    override fun toString(): String {
        val days = data[TimeData.HOUR]!! / DAY_HOURS
        val hours = data[TimeData.HOUR]!! % DAY_HOURS
        return if (days > 0) {
            "%d:%02d:%02d:%02d".format(days, hours, data[TimeData.MINUTE], data[TimeData.SECOND])
        } else {
            "%d:%02d:%02d".format(hours, data[TimeData.MINUTE], data[TimeData.SECOND])
        }
    }

    companion object {

        /**
         * Count of hours in day
         */
        private const val DAY_HOURS = 24

        /**
         * Count of seconds in hour
         */
        private const val HOUR_SECONDS = 3600

        /**
         * Count of seconds in minute
         */
        private const val MINUTE_SECONDS = 60

        /**
         * Minimum minutes or seconds
         */
        private const val MIN_TIME = 0

        /**
         * Maximum minutes or seconds
         */
        private const val MAX_TIME = 59

    }

    /**
     * An enumeration represents time.
     *
     * @author Vladimir Hromada
     */
    enum class TimeData {

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
