package com.github.vhromada.common.utils

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Severity

/**
 * A class represents constants for tests.
 *
 * @author Vladimir Hromada
 */
object TestConstants {

    /**
     * Account
     */
    val ACCOUNT = Account(id = 10, uuid = "d53b2577-a3de-4df7-a8dd-2e6d9e5c1014", username = "", password = "", roles = listOf("ROLE_USER"))

    /**
     * Admin
     */
    val ADMIN = ACCOUNT.copy(roles = listOf("ROLE_ADMIN"))

    /**
     * Result for invalid data
     */
    val INVALID_DATA_RESULT = Result.error<Unit>(key = "DATA_INVALID", message = "Data must be valid.")

    /**
     * Event for invalid data
     */
    val INVALID_DATA_EVENT = Event(severity = Severity.ERROR, key = "DATA_INVALID", message = "Data must be valid.")

}
