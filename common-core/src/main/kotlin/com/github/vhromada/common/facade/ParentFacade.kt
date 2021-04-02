package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.result.Result

/**
 * An interface represents facade for parent data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface ParentFacade<T : Identifiable> : Facade<T> {

    /**
     * Creates new data.
     *
     * @return result
     */
    fun newData(): Result<Unit>

    /**
     * Returns list of data.
     *
     * @return result with list of data
     */
    fun getAll(): Result<List<T>>

    /**
     * Adds data. Sets new ID and position.
     * <br></br>
     * Validation errors:
     *
     *  * ID isn't null
     *  * Position isn't null for movable data
     *  * Deep data validation errors
     *
     * @param data data
     * @return result with validation errors
     */
    fun add(data: T): Result<Unit>

    /**
     * Updates positions.
     *
     * @return result
     */
    fun updatePositions(): Result<Unit>

}
