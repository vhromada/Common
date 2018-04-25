package cz.vhromada.common.utils;

import java.time.LocalDate;

/**
 * A class represents constants.
 *
 * @author Vladimir Hromada
 */
public final class Constants {

    /**
     * Minimal year
     */
    public static final int MIN_YEAR = 1930;

    /**
     * Current year
     */
    public static final int CURRENT_YEAR = LocalDate.now().getYear();

    /**
     * Maximum IMDB code
     */
    public static final int MAX_IMDB_CODE = 9999999;

    /**
     * Creates a new instance of Constants.
     */
    private Constants() {
    }

}
