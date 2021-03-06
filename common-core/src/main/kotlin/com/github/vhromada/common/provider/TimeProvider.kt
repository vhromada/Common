package com.github.vhromada.common.provider

import java.time.LocalDateTime

/**
 * An interface represents provider for time.
 *
 * @author Vladimir Hromada
 */
@Suppress("unused")
interface TimeProvider {

    /**
     * Returns time.
     *
     * @return time
     */
    fun getTime(): LocalDateTime

}
