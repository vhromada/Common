package cz.vhromada.common.facade

import cz.vhromada.common.Movable
import cz.vhromada.common.result.Result

/**
 * An interface represents facade for movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface MovableFacade<T : Movable> {

    /**
     * Returns data with ID or null if there isn't such data.
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
     *  * Position is null
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
     *  * ID is null
     *  * Data doesn't exist in data storage
     *
     * @param data data
     * @return result with validation errors
     */
    fun remove(data: T): Result<Unit>

    /**
     * Duplicates data.
     * <br></br>
     * Validation errors:
     *
     *  * ID is null
     *  * Data doesn't exist in data storage
     *
     * @param data data
     * @return result with validation errors
     */
    fun duplicate(data: T): Result<Unit>

    /**
     * Moves data in list one position up.
     * <br></br>
     * Validation errors:
     *
     *  * ID is null
     *  * Data can't be moved up
     *  * Data doesn't exist in data storage
     *
     * @param data data
     * @return result with validation errors
     */
    fun moveUp(data: T): Result<Unit>

    /**
     * Moves data in list one position down.
     * <br></br>
     * Validation errors:
     *
     *  * ID is null
     *  * Data can't be moved down
     *  * Data doesn't exist in data storage
     *
     * @param data data
     * @return result with validation errors
     */
    fun moveDown(data: T): Result<Unit>

}
