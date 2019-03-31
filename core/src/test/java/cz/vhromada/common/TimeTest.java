package cz.vhromada.common;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A class represents test for class {@link Time}.
 *
 * @author Vladimir Hromada
 */
class TimeTest {

    /**
     * Length
     */
    private static final int LENGTH = 9326;

    /**
     * Array of {@link Time} in length
     */
    private static final int[] TIME_LENGTHS = { 106261, 88261, 104401, 106260, 45061, 19861, 18000, 211, 12, 0 };

    /**
     * Array of {@link Time} in strings
     */
    private static final String[] TIME_STRINGS = { "1:05:31:01", "1:00:31:01", "1:05:00:01", "1:05:31:00", "12:31:01", "5:31:01", "5:00:00", "0:03:31",
        "0:00:12", "0:00:00" };

    /**
     * Length - hours
     */
    private static final int HOURS = 2;

    /**
     * Length - minutes
     */
    private static final int MINUTES = 35;

    /**
     * Length - seconds
     */
    private static final int SECONDS = 26;

    /**
     * Bad maximum minutes or seconds
     */
    private static final int BAD_MAX_TIME = 60;

    /**
     * Instance of {@link Time}
     */
    private Time timeLength;

    /**
     * Instance of {@link Time}
     */
    private Time timeHMS;

    /**
     * Initializes time.
     */
    @BeforeEach
    void setUp() {
        timeLength = new Time(LENGTH);
        timeHMS = new Time(HOURS, MINUTES, SECONDS);
    }

    /**
     * Test method for {@link Time#Time(int)} with bad length.
     */
    @Test
    void constructor_BadLength() {
        assertThatThrownBy(() -> new Time(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link Time#Time(int)} with bad hours.
     */
    @Test
    void constructor_BadHours() {
        assertThatThrownBy(() -> new Time(-1, MINUTES, SECONDS)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link Time#Time(int)} with negative minutes.
     */
    @Test
    void constructor_NegativeMinutes() {
        assertThatThrownBy(() -> new Time(HOURS, -1, SECONDS)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link Time#Time(int)} with bad minutes.
     */
    @Test
    void constructor_BadMinutes() {
        assertThatThrownBy(() -> new Time(HOURS, BAD_MAX_TIME, SECONDS)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link Time#Time(int)} with negative seconds.
     */
    @Test
    void constructor_NegativeSeconds() {
        assertThatThrownBy(() -> new Time(HOURS, MINUTES, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link Time#Time(int)} with bad seconds.
     */
    @Test
    void constructor_BadSeconds() {
        assertThatThrownBy(() -> new Time(HOURS, MINUTES, BAD_MAX_TIME)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link Time#getLength()}.
     */
    @Test
    void getLength() {
        assertSoftly(softly -> {
            softly.assertThat(timeLength.getLength()).isEqualTo(LENGTH);
            softly.assertThat(timeHMS.getLength()).isEqualTo(LENGTH);
        });
    }

    /**
     * Test method for {@link Time#getData(Time.TimeData)}.
     */
    @Test
    void getData() {
        assertSoftly(softly -> {
            softly.assertThat(timeLength.getData(Time.TimeData.HOUR)).isEqualTo(HOURS);
            softly.assertThat(timeLength.getData(Time.TimeData.MINUTE)).isEqualTo(MINUTES);
            softly.assertThat(timeLength.getData(Time.TimeData.SECOND)).isEqualTo(SECONDS);
            softly.assertThat(timeHMS.getData(Time.TimeData.HOUR)).isEqualTo(HOURS);
            softly.assertThat(timeHMS.getData(Time.TimeData.MINUTE)).isEqualTo(MINUTES);
            softly.assertThat(timeHMS.getData(Time.TimeData.SECOND)).isEqualTo(SECONDS);
        });
    }

    /**
     * Test method for {@link Time#getData(Time.TimeData)} with null data type.
     */
    @Test
    void getData_NegativeDataType() {
        assertThatThrownBy(() -> timeLength.getData(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link Time#toString()}.
     */
    @Test
    void testToString() {
        assertSoftly(softly -> {
            softly.assertThat(timeLength.toString()).isEqualTo("2:35:26");
            softly.assertThat(timeHMS.toString()).isEqualTo("2:35:26");
            softly.assertThat(Arrays.stream(TIME_LENGTHS).mapToObj(length -> new Time(length).toString()).toArray(String[]::new)).isEqualTo(TIME_STRINGS);
        });
    }

}
