package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.result.Result

/**
 * An interface represents facade for movable data for parent data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface MovableParentFacade<T : Movable> : MovableFacade<T> {

    /**
     * Returns list of data.
     *
     * @return result with list of data
     */
    fun getAll(): Result<List<T>>

    /**
     * Creates new data.
     *
     * @return result
     */
    fun newData(): Result<Unit>

    /**
     * Adds data. Sets new ID and position.
     * <br></br>
     * Validation errors:
     *
     *  * ID isn't null
     *  * Position isn't null
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
