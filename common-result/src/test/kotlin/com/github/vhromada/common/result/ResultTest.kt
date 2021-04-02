package com.github.vhromada.common.result

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test

/**
 * A class represents test for class [Result].
 *
 * @author Vladimir Hromada
 */
class ResultTest {

    /**
     * Instance of [Result]
     */
    private lateinit var result: Result<String>

    /**
     * Instance of [Event] with severity information
     */
    private val infoEvent: Event = Event(severity = Severity.INFO, key = KEY, message = MESSAGE)

    /**
     * Instance of [Event] with severity warning
     */
    private val warnEvent: Event = Event(severity = Severity.WARN, key = KEY, message = MESSAGE)

    /**
     * Instance of [Event] with severity error
     */
    private val errorEvent: Event = Event(severity = Severity.ERROR, key = KEY, message = MESSAGE)

    /**
     * Test method for [Result.addEvent].
     */
    @Test
    fun addEvent() {
        result = Result()
        result.addEvent(infoEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent))
            it.assertThat(result.isOk()).isTrue
            it.assertThat(result.isError()).isFalse
        }

        result.addEvent(warnEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.WARN)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isFalse
        }

        result.addEvent(infoEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.WARN)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, infoEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isFalse
        }

        result.addEvent(errorEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, infoEvent, errorEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isTrue
        }

        result.addEvent(infoEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, infoEvent, errorEvent, infoEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isTrue
        }

        result.addEvent(warnEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, infoEvent, errorEvent, infoEvent, warnEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isTrue
        }
    }

    /**
     * Test method for [Result.addEvents].
     */
    @Test
    fun addEvents() {
        result = Result()
        result.addEvents(listOf(infoEvent, warnEvent, errorEvent))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, errorEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isTrue
        }
    }

    /**
     * Test method for [Result.of] from data.
     */
    @Test
    fun ofData() {
        result = Result.of(DATA)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(DATA)
            it.assertThat(result.events()).isEmpty()
            it.assertThat(result.isOk()).isTrue
            it.assertThat(result.isError()).isFalse
        }
    }

    /**
     * Test method for [Result.of] from result.
     */
    @Test
    fun ofResult() {
        val result1 = Result.of(DATA)
        val result2 = Result.of(1)
        result2.addEvent(infoEvent)
        val result3 = Result.of('c')
        result3.addEvent(warnEvent)
        val result4 = Result<Unit>()
        result4.addEvent(errorEvent)

        val result = Result.of<Unit>(result1, result2, result3, result4)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, errorEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isTrue
        }
    }

    /**
     * Test method for [Result.info].
     */
    @Test
    fun info() {
        result = Result.info(key = KEY, message = MESSAGE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent))
            it.assertThat(result.isOk()).isTrue
            it.assertThat(result.isError()).isFalse
        }
    }

    /**
     * Test method for [Result.warn].
     */
    @Test
    fun warn() {
        result = Result.warn(key = KEY, message = MESSAGE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.WARN)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(warnEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isFalse
        }
    }

    /**
     * Test method for [Result.error].
     */
    @Test
    fun error() {
        result = Result.error(key = KEY, message = MESSAGE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(errorEvent))
            it.assertThat(result.isOk()).isFalse
            it.assertThat(result.isError()).isTrue
        }
    }

    companion object {

        /**
         * Data
         */
        private const val DATA = "data"

        /**
         * Key
         */
        private const val KEY = "key"

        /**
         * Message
         */
        private const val MESSAGE = "message"

    }

}
