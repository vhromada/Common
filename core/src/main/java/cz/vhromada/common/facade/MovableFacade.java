package cz.vhromada.common.facade;

import cz.vhromada.common.Movable;
import cz.vhromada.result.Result;

/**
 * An interface represents facade for movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
public interface MovableFacade<T extends Movable> {

    /**
     * Returns data with ID or null if there isn't such data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>ID is null</li>
     * </ul>
     *
     * @param id ID
     * @return result with data or validation errors
     */
    Result<T> get(Integer id);

    /**
     * Updates data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Position is null</li>
     * <li>Deep data validation errors</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data new value of data
     * @return result with validation errors
     */
    Result<Void> update(T data);

    /**
     * Removes data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    Result<Void> remove(T data);

    /**
     * Duplicates data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    Result<Void> duplicate(T data);

    /**
     * Moves data in list one position up.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Data can't be moved up</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    Result<Void> moveUp(T data);

    /**
     * Moves data in list one position down.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Data can't be moved down</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    Result<Void> moveDown(T data);

}
