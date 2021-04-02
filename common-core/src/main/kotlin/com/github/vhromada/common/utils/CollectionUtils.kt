package com.github.vhromada.common.utils

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.entity.Movable

/**
 * Returns sorted data.
 *
 * @param <T> type of data
 * @return sorted data
 */
fun <T : Identifiable> List<T>.sorted(): List<T> {
    return sortedWith(compareBy({ it.getPosition() }, { it.id }))
}

/**
 * Returns position.
 *
 * @return position
 */
private fun Identifiable.getPosition(): Int? {
    return if (this is Movable) this.position else Int.MAX_VALUE
}
