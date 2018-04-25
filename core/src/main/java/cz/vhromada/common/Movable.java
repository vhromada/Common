package cz.vhromada.common;

import java.io.Serializable;

/**
 * An interface represents movable object.
 *
 * @author Vladimir Hromada
 */
public interface Movable extends Serializable {

    /**
     * Returns ID.
     *
     * @return ID
     */
    Integer getId();

    /**
     * Sets a new value to ID.
     *
     * @param id new value
     */
    void setId(Integer id);

    /**
     * Returns position.
     *
     * @return position
     */
    Integer getPosition();

    /**
     * Sets a new value to position.
     *
     * @param position new value
     */
    void setPosition(Integer position);

}
