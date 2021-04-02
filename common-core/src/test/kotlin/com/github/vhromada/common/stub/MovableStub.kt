package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Movable

/**
 * A class represents stub for [Movable].
 *
 * @author Vladimir Hromada
 */
class MovableStub(
    override var id: Int?,
    override var position: Int?
) : Movable
