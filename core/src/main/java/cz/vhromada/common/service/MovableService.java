package cz.vhromada.common.service;

import java.util.List;

import cz.vhromada.common.Movable;

/**
 * An interface represents service for movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
public interface MovableService<T extends Movable> {

    /**
     * Creates new data.
     */
    void newData();

    /**
     * Returns list of data.
     *
     * @return list of data
     */
    List<T> getAll();

    /**
     * Returns data with ID or null if there aren't such data.
     *
     * @param id ID
     * @return data with ID or null if there aren't such data
     * @throws IllegalArgumentException if ID is null
     */
    T get(Integer id);

    /**
     * Adds data. Sets new ID and position.
     *
     * @param data data
     * @throws IllegalArgumentException if data is null
     */
    void add(T data);

    /**
     * Updates data.
     *
     * @param data new value of data
     * @throws IllegalArgumentException if data is null
     */
    void update(T data);

    /**
     * Removes data.
     *
     * @param data data
     * @throws IllegalArgumentException if data is null
     */
    void remove(T data);

    /**
     * Duplicates data.
     *
     * @param data data
     * @throws IllegalArgumentException if data is null
     */
    void duplicate(T data);

    /**
     * Moves data in list one position up.
     *
     * @param data data
     * @throws IllegalArgumentException if data is null
     */
    void moveUp(T data);

    /**
     * Moves data in list one position down.
     *
     * @param data data
     * @throws IllegalArgumentException if data is null
     */
    void moveDown(T data);

    /**
     * Updates positions.
     */
    void updatePositions();

}
