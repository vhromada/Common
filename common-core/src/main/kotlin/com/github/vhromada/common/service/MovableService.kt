package com.github.vhromada.common.service

import com.github.vhromada.common.domain.AuditEntity
import java.util.Optional

/**
 * An interface represents service for audible and movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface MovableService<T : AuditEntity> {

    /**
     * Returns list of data.
     *
     * @return list of data
     */
    fun getAll(): List<T>

    /**
     * Creates new data.
     */
    fun newData()

    /**
     * Returns data with ID.
     *
     * @param id ID
     * @return data with ID
     */
    fun get(id: Int): Optional<T>

    /**
     * Adds data. Sets new ID and position.
     *
     * @param data data
     */
    fun add(data: T)

    /**
     * Updates data.
     *
     * @param data new value of data
     */
    fun update(data: T)

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
     */
    fun duplicate(data: T)

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

    /**
     * Updates positions.
     */
    fun updatePositions()

}
