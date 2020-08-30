package com.github.vhromada.common.test.stub

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.AbstractMovableChildFacade
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.TimeProvider
import com.github.vhromada.common.service.MovableService
import com.github.vhromada.common.validator.MovableValidator

/**
 * A class represents stub for [AbstractMovableChildFacade].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableChildFacadeStub(
        service: MovableService<AuditEntity>,
        accountProvider: AccountProvider,
        timeProvider: TimeProvider,
        mapper: Mapper<Movable, AuditEntity>,
        parentValidator: MovableValidator<Movable>,
        childValidator: MovableValidator<Movable>) : AbstractMovableChildFacade<Movable, AuditEntity, Movable, AuditEntity>(service, accountProvider, timeProvider, mapper, parentValidator, childValidator) {

    override fun getDomainData(id: Int): AuditEntity? {
        for (movable in service.getAll()) {
            if (id == movable.id) {
                return movable
            }
        }
        return null
    }

    override fun getDomainList(parent: Movable): List<AuditEntity> {
        return listOf(service.get(parent.id!!)!!)
    }

    override fun getForAdd(parent: Movable, data: AuditEntity): AuditEntity {
        return service.get(parent.id!!)!!
    }

    override fun getForUpdate(data: Movable): AuditEntity {
        val updateData = getDomainData(getDataForUpdate(data).id!!)!!
        updateData.modify(getAudit())
        return updateData
    }

    override fun getForRemove(data: Movable): AuditEntity {
        return getDomainData(data.id!!)!!
    }

    override fun getForDuplicate(data: Movable): AuditEntity {
        return getDomainData(data.id!!)!!
    }

    override fun getForMove(data: Movable, up: Boolean): AuditEntity {
        val moveData = getDomainData(data.id!!)!!
        moveData.modify(getAudit())
        return moveData
    }

}
