package com.github.vhromada.common.result

import java.io.Serializable

/**
 * A class represents event.
 *
 * @author Vladimir Hromada
 */
data class Event(
    val severity: Severity,
    val key: String,
    val message: String
) : Serializable
