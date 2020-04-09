package cz.vhromada.common.facade

import cz.vhromada.common.entity.Movable
import cz.vhromada.common.result.Result

/**
 * An interface represents facade for movable data for child data.
 *
 * @param <T> type of child data
 * @param <U> type of parent data
 * @author Vladimir Hromada
 */
interface MovableChildFacade<T : Movable, U : Movable> : MovableFacade<T> {

    /**
     * Adds data. Sets new ID and position.
     * <br></br>
     * Validation errors:
     *
     *  * Parent ID is null
     *  * Parent doesn't exist in data storage
     *  * Data ID isn't null
     *  * Data position isn't null
     *  * Deep data validation errors
     *
     * @param parent parent
     * @param data   data
     * @return result with validation errors
     */
    fun add(parent: U, data: T): Result<Unit>

    /**
     * Returns list of data for specified parent.
     * <br></br>
     * Validation errors:
     *
     *  * Parent ID is null
     *  * Parent doesn't exist in data storage
     *
     * @param parent parent
     * @return result with list of data or validation errors
     */
    fun find(parent: U): Result<List<T>>

}
