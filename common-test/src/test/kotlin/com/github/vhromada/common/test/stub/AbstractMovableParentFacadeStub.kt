package com.github.vhromada.common.test.stub

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.AbstractMovableParentFacade
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.TimeProvider
import com.github.vhromada.common.service.MovableService
import com.github.vhromada.common.validator.MovableValidator

/**
 * A class represents stub for [AbstractMovableParentFacade].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableParentFacadeStub(
        service: MovableService<AuditEntity>,
        accountProvider: AccountProvider,
        timeProvider: TimeProvider,
        mapper: Mapper<Movable, AuditEntity>,
        validator: MovableValidator<Movable>) : AbstractMovableParentFacade<Movable, AuditEntity>(service, accountProvider, timeProvider, mapper, validator)
