package com.github.vhromada.common.entity

import java.io.Serializable

/**
 * An interface represents identifiable object.
 *
 * @author Vladimir Hromada
 */
interface Identifiable : Serializable {

    /**
     * ID
     */
    var id: Int?

}