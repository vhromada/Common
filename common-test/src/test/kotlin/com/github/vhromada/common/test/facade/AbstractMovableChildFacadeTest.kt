package com.github.vhromada.common.test.facade

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.AbstractMovableChildFacade
import com.github.vhromada.common.facade.MovableChildFacade
import com.github.vhromada.common.test.stub.AbstractMovableChildFacadeStub
import com.github.vhromada.common.test.stub.AuditEntityStub
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.junit.jupiter.api.BeforeEach

/**
 * A class represents test for class [AbstractMovableChildFacade].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableChildFacadeTest : MovableChildFacadeTest<Movable, AuditEntity, Movable, AuditEntity>() {

    /**
     * Instance of [AuditEntity]
     */
    private var auditEntity: AuditEntity? = null

    /**
     * {@inheritDoc}
     * Cleanup movable.
     */
    @BeforeEach
    override fun setUp() {
        super.setUp()

        auditEntity = null
    }

    override fun getFacade(): MovableChildFacade<Movable, Movable> {
        return AbstractMovableChildFacadeStub(service, accountProvider, timeProvider, mapper, parentMovableValidator, childMovableValidator)
    }

    override fun newParentEntity(id: Int): Movable {
        return getMovable(id)
    }

    override fun newParentDomain(id: Int): AuditEntity {
        return getMovable(id)
    }

    override fun newParentDomainWithChildren(id: Int, children: List<AuditEntity>): AuditEntity {
        return getMovable(id)
    }

    override fun newChildEntity(id: Int?): Movable {
        return getMovable(id)
    }

    override fun newChildDomain(id: Int?): AuditEntity {
        return getMovable(id)
    }

    override fun getParentRemovedData(parent: AuditEntity, child: AuditEntity): AuditEntity {
        return parent
    }

    override fun anyParentEntity(): Movable {
        return any()
    }

    override fun anyChildEntity(): Movable {
        return any()
    }

    override fun anyChildDomain(): AuditEntity {
        return any()
    }

    override fun argumentCaptorParentDomain(): KArgumentCaptor<AuditEntity> {
        return argumentCaptor()
    }

    override fun assertParentDeepEquals(expected: AuditEntity, actual: AuditEntity) {
        // nothing
    }

    /**
     * Returns movable object.
     *
     * @param id ID
     * @return movable object
     */
    private fun getMovable(id: Int?): AuditEntity {
        if (auditEntity == null) {
            auditEntity = AuditEntityStub(id)
        } else {
            auditEntity!!.id = id
        }

        return auditEntity!!
    }

}
