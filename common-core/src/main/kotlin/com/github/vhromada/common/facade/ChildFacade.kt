package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.result.Result

/**
 * An interface represents facade for child data.
 *
 * @param <T> type of child data
 * @param <U> type of parent data
 * @author Vladimir Hromada
 */
interface ChildFacade<T : Identifiable, U : Identifiable> : Facade<T> {

    /**
     * Adds data. Sets new ID and position.
     * <br></br>
     * Validation errors:
     *
     *  * Parent doesn't exist in data storage
     *  * Data ID isn't null
     *  * Data position isn't null for movable data
     *  * Deep data validation errors
     *
     * @param parent parent's ID
     * @param data   data
     * @return result with validation errors
     */
    fun add(parent: Int, data: T): Result<Unit>

    /**
     * Returns list of data for specified parent.
     * <br></br>
     * Validation errors:
     *
     *  * Parent doesn't exist in data storage
     *
     * @param parent parent's ID
     * @return result with list of data or validation errors
     */
    fun find(parent: Int): Result<List<T>>

}
