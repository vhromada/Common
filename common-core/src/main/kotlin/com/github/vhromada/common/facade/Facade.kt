package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.result.Result

/**
 * An interface represents facade for data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface Facade<T : Identifiable> {

    /**
     * Returns data with ID
     * <br></br>
     * Validation errors:
     *
     *  * Data doesn't exist in data storage
     *
     * @param id ID
     * @return result with data or validation errors
     */
    fun get(id: Int): Result<T>

    /**
     * Updates data.
     * <br></br>
     * Validation errors:
     *
     *  * ID is null
     *  * Position is null for movable data
     *  * Deep data validation errors
     *  * Data doesn't exist in data storage
     *
     * @param data new value of data
     * @return result with validation errors
     */
    fun update(data: T): Result<Unit>

    /**
     * Removes data.
     * <br></br>
     * Validation errors:
     *
     *  * Data doesn't exist in data storage
     *
     * @param id ID
     * @return result with validation errors
     */
    fun remove(id: Int): Result<Unit>

    /**
     * Duplicates data.
     * <br></br>
     * Validation errors:
     *
     *  * Data doesn't exist in data storage
     *
     * @param id ID
     * @return result with validation errors
     */
    fun duplicate(id: Int): Result<Unit>

    /**
     * Moves data in list one position up.
     * <br></br>
     * Validation errors:
     *
     *  * Data can't be moved up
     *  * Data doesn't exist in data storage
     *
     * @param id ID
     * @return result with validation errors
     */
    fun moveUp(id: Int): Result<Unit>

    /**
     * Moves data in list one position down.
     * <br></br>
     * Validation errors:
     *
     *  * Data can't be moved down
     *  * Data doesn't exist in data storage
     *
     * @param id ID
     * @return result with validation errors
     */
    fun moveDown(id: Int): Result<Unit>

}
