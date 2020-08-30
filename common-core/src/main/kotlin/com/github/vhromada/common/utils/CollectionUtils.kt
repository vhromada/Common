package com.github.vhromada.common.utils

import com.github.vhromada.common.entity.Movable

/**
 * Returns sorted data.
 *
 * @param <T>  type of data
 * @return sorted data
 */
fun <T : Movable> List<T>.sorted(): List<T> {
    return sortedWith(compareBy({ it.position }, { it.id }))
}
