package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Identifiable

/**
 * An interface represents service for child data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface ChildService<T : Identifiable> : Service<T> {

    /**
     * Returns list of data for specified parent.
     *
     * @param parent parent's ID
     * @return list of data for specified parent
     */
    fun find(parent: Int): List<T>

}
