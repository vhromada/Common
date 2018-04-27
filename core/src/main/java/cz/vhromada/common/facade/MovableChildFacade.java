package cz.vhromada.common.facade;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.result.Result;

/**
 * An interface represents facade for movable data for child data.
 *
 * @param <T> type of child data
 * @param <U> type of parent data
 * @author Vladimir Hromada
 */
public interface MovableChildFacade<T extends Movable, U extends Movable> extends MovableFacade<T> {

    /**
     * Adds data. Sets new ID and position.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Parent is null</li>
     * <li>Parent ID is null</li>
     * <li>Parent doesn't exist in data storage</li>
     * <li>Data is null</li>
     * <li>Data ID isn't null</li>
     * <li>Data position isn't null</li>
     * <li>Deep data validation errors</li>
     * </ul>
     *
     * @param parent parent
     * @param data   data
     * @return result with validation errors
     */
    Result<Void> add(U parent, T data);

    /**
     * Returns list of data for specified parent.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Parent is null</li>
     * <li>Parent ID is null</li>
     * <li>Parent doesn't exist in data storage</li>
     * </ul>
     *
     * @param parent parent
     * @return result with list of data or validation errors
     */
    Result<List<T>> find(U parent);

}
