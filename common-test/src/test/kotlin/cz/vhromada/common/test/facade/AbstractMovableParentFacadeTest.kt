package cz.vhromada.common.test.facade

import com.nhaarman.mockitokotlin2.any
import cz.vhromada.common.domain.AuditEntity
import cz.vhromada.common.entity.Movable
import cz.vhromada.common.facade.AbstractMovableParentFacade
import cz.vhromada.common.facade.MovableParentFacade
import cz.vhromada.common.test.stub.AbstractMovableParentFacadeStub
import cz.vhromada.common.test.stub.AuditEntityStub

/**
 * A class represents test for class [AbstractMovableParentFacade].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableParentFacadeTest : MovableParentFacadeTest<Movable, AuditEntity>() {

    override fun getFacade(): MovableParentFacade<Movable> {
        return AbstractMovableParentFacadeStub(service, accountProvider, timeProvider, mapper, validator)
    }

    override fun newEntity(id: Int?): Movable {
        return AuditEntityStub(id)
    }

    override fun newDomain(id: Int?): AuditEntity {
        return AuditEntityStub(id)
    }

    override fun anyEntity(): Movable {
        return any()
    }

    override fun anyDomain(): AuditEntity {
        return any()
    }

}
