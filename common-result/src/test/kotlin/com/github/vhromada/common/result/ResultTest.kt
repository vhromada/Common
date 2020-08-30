package com.github.vhromada.common.result

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test

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
    private val infoEvent: Event = Event(Severity.INFO, KEY, MESSAGE)

    /**
     * Instance of [Event] with severity warning
     */
    private val warnEvent: Event = Event(Severity.WARN, KEY, MESSAGE)

    /**
     * Instance of [Event] with severity error
     */
    private val errorEvent: Event = Event(Severity.ERROR, KEY, MESSAGE)

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
        }

        result.addEvent(warnEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.WARN)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent))
        }

        result.addEvent(infoEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.WARN)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, infoEvent))
        }

        result.addEvent(errorEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, infoEvent, errorEvent))
        }

        result.addEvent(infoEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, infoEvent, errorEvent, infoEvent))
        }

        result.addEvent(warnEvent)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent, warnEvent, infoEvent, errorEvent, infoEvent, warnEvent))
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
        }
    }

    /**
     * Test method for [Result.of].
     */
    @Test
    fun of() {
        result = Result.of(DATA)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(DATA)
            it.assertThat(result.events()).isEmpty()
        }
    }

    /**
     * Test method for [Result.info].
     */
    @Test
    fun info() {
        result = Result.info(KEY, MESSAGE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(infoEvent))
        }
    }

    /**
     * Test method for [Result.warn].
     */
    @Test
    fun warn() {
        result = Result.warn(KEY, MESSAGE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.WARN)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(warnEvent))
        }
    }

    /**
     * Test method for [Result.error].
     */
    @Test
    fun error() {
        result = Result.error(KEY, MESSAGE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(errorEvent))
        }
    }

}
