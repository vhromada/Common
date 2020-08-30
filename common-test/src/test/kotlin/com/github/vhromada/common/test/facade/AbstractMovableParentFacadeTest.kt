package com.github.vhromada.common.test.facade

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.AbstractMovableParentFacade
import com.github.vhromada.common.facade.MovableParentFacade
import com.github.vhromada.common.test.stub.AbstractMovableParentFacadeStub
import com.github.vhromada.common.test.stub.AuditEntityStub
import com.nhaarman.mockitokotlin2.any

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
