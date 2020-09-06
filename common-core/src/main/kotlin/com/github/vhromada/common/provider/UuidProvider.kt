package com.github.vhromada.common.provider

/**
 * An interface represents provider for UUID.
 *
 * @author Vladimir Hromada
 */
interface UuidProvider {

    /**
     * Returns UUID.
     *
     * @return UUID
     */
    fun getUuid(): String

}