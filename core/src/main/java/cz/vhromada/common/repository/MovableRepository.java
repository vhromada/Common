package cz.vhromada.common.repository;

import java.util.List;

import cz.vhromada.common.Movable;

/**
 * An interface represents repository for movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
public interface MovableRepository<T extends Movable> {

    /**
     * Returns list of data.
     *
     * @return list of data
     */
    List<T> getAll();

    /**
     * Adds data. Sets new ID.
     *
     * @param data data
     * @return added data with new ID
     * @throws IllegalArgumentException if data is null
     */
    T add(T data);

    /**
     * Updates data.
     *
     * @param data new value of data
     * @return updated data
     * @throws IllegalArgumentException if data is null
     */
    T update(T data);

    /**
     * Updates data.
     *
     * @param data new value of data
     * @return updated data
     * @throws IllegalArgumentException if data is null
     *                                  or data contains null
     */
    List<T> updateAll(List<T> data);

    /**
     * Removes data.
     *
     * @param data data
     * @throws IllegalArgumentException if data is null
     */
    void remove(T data);

    /**
     * Removes all data.
     */
    void removeAll();

}
