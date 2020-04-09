package cz.vhromada.common.test.stub

import cz.vhromada.common.domain.AuditEntity
import cz.vhromada.common.entity.Movable
import cz.vhromada.common.facade.AbstractMovableParentFacade
import cz.vhromada.common.mapper.Mapper
import cz.vhromada.common.provider.AccountProvider
import cz.vhromada.common.provider.TimeProvider
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.validator.MovableValidator

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
