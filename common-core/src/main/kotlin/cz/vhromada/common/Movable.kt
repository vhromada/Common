package cz.vhromada.common

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
