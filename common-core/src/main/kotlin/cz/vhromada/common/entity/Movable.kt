package cz.vhromada.common.entity

import java.io.Serializable

/**
 * An interface represents movable object.
 *
 * @author Vladimir Hromada
 */
interface Movable : Serializable {

    /**
     * ID
     */
    var id: Int?

    /**
     * Position
     */
    var position: Int?

}
