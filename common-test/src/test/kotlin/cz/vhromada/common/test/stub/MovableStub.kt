package cz.vhromada.common.test.stub

import cz.vhromada.common.Movable

/**
 * A class represents stub for [Movable].
 *
 * @author Vladimir Hromada
 */
class MovableStub(override var id: Int?, override var position: Int? = null) : Movable
