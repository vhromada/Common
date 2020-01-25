package cz.vhromada.common.test.stub

import cz.vhromada.common.Movable
import cz.vhromada.common.facade.AbstractMovableParentFacade
import cz.vhromada.common.mapper.Mapper
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.validator.MovableValidator

/**
 * A class represents stub for [AbstractMovableParentFacade].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableParentFacadeStub(
        service: MovableService<Movable>,
        mapper: Mapper<Movable, Movable>,
        validator: MovableValidator<Movable>) : AbstractMovableParentFacade<Movable, Movable>(service, mapper, validator)
