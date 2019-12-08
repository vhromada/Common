package cz.vhromada.common.test.facade

import com.nhaarman.mockitokotlin2.any
import cz.vhromada.common.Movable
import cz.vhromada.common.facade.AbstractMovableParentFacade
import cz.vhromada.common.facade.MovableParentFacade
import cz.vhromada.common.test.stub.AbstractMovableParentFacadeStub
import cz.vhromada.common.test.stub.MovableStub

/**
 * A class represents test for class [AbstractMovableParentFacade].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableParentFacadeTest : MovableParentFacadeTest<Movable, Movable>() {

    override fun getFacade(): MovableParentFacade<Movable> {
        return AbstractMovableParentFacadeStub(service, mapper, validator)
    }

    override fun newEntity(id: Int?): Movable {
        return MovableStub(id)
    }

    override fun newDomain(id: Int?): Movable {
        return MovableStub(id)
    }

    override fun anyEntity(): Movable {
        return any()
    }

    override fun anyDomain(): Movable {
        return any()
    }

}
