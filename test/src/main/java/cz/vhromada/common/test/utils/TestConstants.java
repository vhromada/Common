package cz.vhromada.common.test.utils;

import cz.vhromada.common.utils.Constants;

/**
 * A class represents constants for tests.
 *
 * @author Vladimir Hromada
 */
public final class TestConstants {

    /**
     * Bad minimal year
     */
    public static final int BAD_MIN_YEAR = Constants.MIN_YEAR - 1;

    /**
     * Bad maximal year
     */
    public static final int BAD_MAX_YEAR = Constants.CURRENT_YEAR + 1;

    /**
     * Bad minimum IMDB code
     */
    public static final int BAD_MIN_IMDB_CODE = -2;

    /**
     * Bad maximum IMDB code
     */
    public static final int BAD_MAX_IMDB_CODE = Constants.MAX_IMDB_CODE + 1;

    /**
     * Creates a new instance of TestConstants.
     */
    private TestConstants() {
    }

}
