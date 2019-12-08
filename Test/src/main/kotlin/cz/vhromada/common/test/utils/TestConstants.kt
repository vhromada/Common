package cz.vhromada.common.test.utils

import cz.vhromada.common.utils.Constants

/**
 * A class represents constants for tests.
 *
 * @author Vladimir Hromada
 */
@Suppress("unused")
object TestConstants {

    /**
     * Bad minimal year
     */
    const val BAD_MIN_YEAR = Constants.MIN_YEAR - 1

    /**
     * Bad maximal year
     */
    val BAD_MAX_YEAR = Constants.CURRENT_YEAR + 1

    /**
     * Bad minimum IMDB code
     */
    const val BAD_MIN_IMDB_CODE = -2

    /**
     * Bad maximum IMDB code
     */
    const val BAD_MAX_IMDB_CODE = Constants.MAX_IMDB_CODE + 1

}
