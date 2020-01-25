package cz.vhromada.common

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Length
 */
private const val LENGTH = 9326

/**
 * Array of [Time] in length
 */
private val TIME_LENGTHS = listOf(106261, 88261, 104401, 106260, 45061, 19861, 18000, 211, 12, 0)

/**
 * Array of [Time] in strings
 */
private val TIME_STRINGS = listOf("1:05:31:01", "1:00:31:01", "1:05:00:01", "1:05:31:00", "12:31:01", "5:31:01", "5:00:00", "0:03:31", "0:00:12", "0:00:00")

/**
 * Length - hours
 */
private const val HOURS = 2

/**
 * Length - minutes
 */
private const val MINUTES = 35

/**
 * Length - seconds
 */
private const val SECONDS = 26

/**
 * Bad maximum minutes or seconds
 */
private const val BAD_MAX_TIME = 60

/**
 * A class represents test for class [Time].
 *
 * @author Vladimir Hromada
 */
class TimeTest {

    /**
     * Instance of [Time]
     */
    private lateinit var timeLength: Time

    /**
     * Instance of [Time]
     */
    private lateinit var timeHMS: Time

    /**
     * Initializes time.
     */
    @BeforeEach
    fun setUp() {
        timeLength = Time(LENGTH)
        timeHMS = Time(HOURS, MINUTES, SECONDS)
    }

    /**
     * Test method for constructor with bad length.
     */
    @Test
    fun constructorBadLength() {
        assertThatThrownBy { Time(-1) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    /**
     * Test method for constructor with bad hours.
     */
    @Test
    fun constructorBadHours() {
        assertThatThrownBy { Time(-1, MINUTES, SECONDS) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    /**
     * Test method for constructor with negative minutes.
     */
    @Test
    fun constructorNegativeMinutes() {
        assertThatThrownBy { Time(HOURS, -1, SECONDS) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    /**
     * Test method for constructor with bad minutes.
     */
    @Test
    fun constructorBadMinutes() {
        assertThatThrownBy { Time(HOURS, BAD_MAX_TIME, SECONDS) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    /**
     * Test method for constructor with negative seconds.
     */
    @Test
    fun constructorNegativeSeconds() {
        assertThatThrownBy { Time(HOURS, MINUTES, -1) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    /**
     * Test method for constructor with bad seconds.
     */
    @Test
    fun constructorBadSeconds() {
        assertThatThrownBy { Time(HOURS, MINUTES, BAD_MAX_TIME) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    /**
     * Test method for [Time.length].
     */
    @Test
    fun length() {
        assertSoftly {
            it.assertThat(timeLength.length).isEqualTo(LENGTH)
            it.assertThat(timeHMS.length).isEqualTo(LENGTH)
        }
    }

    /**
     * Test method for [Time.getData].
     */
    @Test
    fun getData() {
        assertSoftly {
            it.assertThat(timeLength.getData(Time.TimeData.HOUR)).isEqualTo(HOURS)
            it.assertThat(timeLength.getData(Time.TimeData.MINUTE)).isEqualTo(MINUTES)
            it.assertThat(timeLength.getData(Time.TimeData.SECOND)).isEqualTo(SECONDS)
            it.assertThat(timeHMS.getData(Time.TimeData.HOUR)).isEqualTo(HOURS)
            it.assertThat(timeHMS.getData(Time.TimeData.MINUTE)).isEqualTo(MINUTES)
            it.assertThat(timeHMS.getData(Time.TimeData.SECOND)).isEqualTo(SECONDS)
        }
    }

    /**
     * Test method for [Time.toString].
     */
    @Test
    fun testToString() {
        assertSoftly {
            it.assertThat(timeLength.toString()).isEqualTo("2:35:26")
            it.assertThat(timeHMS.toString()).isEqualTo("2:35:26")
            it.assertThat(TIME_LENGTHS.map { length -> Time(length).toString() }).isEqualTo(TIME_STRINGS)
        }
    }

}
