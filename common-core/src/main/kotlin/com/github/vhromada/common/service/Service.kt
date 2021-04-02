package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Identifiable
import java.util.Optional

/**
 * An interface represents service for data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface Service<T : Identifiable> {

    /**
     * Returns data with ID.
     *
     * @param id ID
     * @return data with ID
     */
    fun get(id: Int): Optional<T>

    /**
     * Adds data. Sets new ID.
     *
     * @param data data
     * @return added data
     */
    fun add(data: T): T

    /**
     * Updates data.
     *
     * @param data new value of data
     * @return updated data
     */
    fun update(data: T): T

    /**
     * Removes data.
     *
     * @param data data
     */
    fun remove(data: T)

    /**
     * Duplicates data.
     *
     * @param data data
     * @return duplicated data
     */
    fun duplicate(data: T): T

    /**
     * Moves data in list one position up.
     *
     * @param data data
     */
    fun moveUp(data: T)

    /**
     * Moves data in list one position down.
     *
     * @param data data
     */
    fun moveDown(data: T)

}
