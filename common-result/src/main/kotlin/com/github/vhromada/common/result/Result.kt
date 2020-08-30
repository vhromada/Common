package com.github.vhromada.common.result

import java.io.Serializable

/**
 * A class represents result.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
class Result<T> : Serializable {

    /**
     * Data
     */
    var data: T?
        private set

    /**
     * Status
     */
    var status: Status
        private set

    /**
     * Events
     */
    private var events: MutableList<Event>

    init {
        data = null
        status = Status.OK
        events = mutableListOf()
    }

    /**
     * Returns events.
     *
     * @return events
     */
    fun events(): List<Event> {
        return events.toList()
    }

    /**
     * Adds event.
     *
     * @param event event
     */
    fun addEvent(event: Event) {
        events.add(event)
        status = getNewStatus(event)
    }

    /**
     * Adds events.
     *
     * @param eventList list of events
     */
    fun addEvents(eventList: List<Event>) {
        eventList.forEach { addEvent(it) }
    }


    /**
     * Returns new status.
     *
     * @param event event
     * @return new status
     */
    private fun getNewStatus(event: Event): Status {
        val newStatus = getStatus(event.severity)
        return if (status.ordinal >= newStatus.ordinal) status else newStatus
    }

    /**
     * Returns status for severity.
     *
     * @param severity severity
     * @return status for severity
     */
    private fun getStatus(severity: Severity): Status {
        return Status.values()
                .first { it.ordinal == severity.ordinal }
    }

    override fun toString(): String {
        return "Result(data=$data, status=$status, events=$events)"
    }

    companion object {

        /**
         * Returns result with specified data.
         *
         * @param data data
         * @param <T>  type of data
         * @return result with specified data
         */
        fun <T> of(data: T): Result<T> {
            val result = Result<T>()
            result.data = data
            return result
        }

        /**
         * Returns result with specified information message.
         *
         * @param key     key
         * @param message message
         * @param <T>     type of data
         * @return result with specified information message
         */
        fun <T> info(key: String, message: String): Result<T> {
            val result = Result<T>()
            result.addEvent(Event(Severity.INFO, key, message))
            return result
        }

        /**
         * Returns result with specified warning message.
         *
         * @param key     key
         * @param message message
         * @param <T>     type of data
         * @return result with specified warning message
         */
        fun <T> warn(key: String, message: String): Result<T> {
            val result = Result<T>()
            result.addEvent(Event(Severity.WARN, key, message))
            return result
        }

        /**
         * Returns result with specified error message.
         *
         * @param key     key
         * @param message message
         * @param <T>     type of data
         * @return result with specified error message
         */
        fun <T> error(key: String, message: String): Result<T> {
            val result = Result<T>()
            result.addEvent(Event(Severity.ERROR, key, message))
            return result
        }

    }

}
