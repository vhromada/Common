package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Identifiable

/**
 * An interface represents service for parent data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface ParentService<T : Identifiable> : Service<T> {

    /**
     * Creates new data.
     */
    fun newData()

    /**
     * Returns list of data.
     *
     * @return list of data
     */
    fun getAll(): List<T>

    /**
     * Updates positions.
     */
    fun updatePositions()

}
