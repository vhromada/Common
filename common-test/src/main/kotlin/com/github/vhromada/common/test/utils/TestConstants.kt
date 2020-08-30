package com.github.vhromada.common.test.utils

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.utils.Constants
import java.time.LocalDateTime

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

    /**
     * Account's ID
     */
    const val ACCOUNT_ID = 10

    /**
     * Time
     */
    val TIME: LocalDateTime = LocalDateTime.of(2000, 2, 4, 10, 45, 55, 70)

    /**
     * Account
     */
    val ACCOUNT = Account(id = ACCOUNT_ID, username = "", password = "", roles = listOf("ROLE_USER"))

}
