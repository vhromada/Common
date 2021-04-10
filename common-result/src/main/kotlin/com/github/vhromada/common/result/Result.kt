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
        eventList.forEach { addEvent(event = it) }
    }

    /**
     * Returns true if status is OK.
     *
     * @return true if status is OK
     */
    fun isOk(): Boolean {
        return Status.OK == status
    }

    /**
     * Returns true if status is error.
     *
     * @return true if status is error
     */
    fun isError(): Boolean {
        return Status.ERROR == status
    }

    /**
     * Returns new status.
     *
     * @param event event
     * @return new status
     */
    private fun getNewStatus(event: Event): Status {
        val newStatus = getStatus(severity = event.severity)
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
         * Returns result merged from other result.
         *
         * @param results results
         * @param <T>     type of data
         * @return result merged from other result
         */
        fun <T> of(vararg results: Result<*>): Result<T> {
            val result = Result<T>()
            results.forEach { result.addEvents(eventList = it.events) }
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
            result.addEvent(event = Event(severity = Severity.INFO, key = key, message = message))
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
            result.addEvent(event = Event(severity = Severity.WARN, key = key, message = message))
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
            result.addEvent(event = Event(severity = Severity.ERROR, key = key, message = message))
            return result
        }

    }

}
