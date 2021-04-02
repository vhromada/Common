package com.github.vhromada.common.entity

/**
 * An interface represents movable object.
 *
 * @author Vladimir Hromada
 */
interface Movable : Identifiable {

    /**
     * Position
     */
    var position: Int?

    /**
     * Updates position.
     *
     * @param position position
     */
    open fun updatePosition(position: Int) {
        this.position = position
    }

}
