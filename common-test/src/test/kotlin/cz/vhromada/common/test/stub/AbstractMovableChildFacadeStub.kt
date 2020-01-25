package cz.vhromada.common.test.stub

import cz.vhromada.common.Movable
import cz.vhromada.common.facade.AbstractMovableChildFacade
import cz.vhromada.common.mapper.Mapper
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.validator.MovableValidator

/**
 * A class represents stub for [AbstractMovableChildFacade].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableChildFacadeStub(
        service: MovableService<Movable>,
        mapper: Mapper<Movable, Movable>,
        parentValidator: MovableValidator<Movable>,
        childValidator: MovableValidator<Movable>) : AbstractMovableChildFacade<Movable, Movable, Movable, Movable>(service, mapper, parentValidator, childValidator) {

    override fun getDomainData(id: Int): Movable? {
        for (movable in service.getAll()) {
            if (id == movable.id) {
                return movable
            }
        }
        return null
    }

    override fun getDomainList(parent: Movable): List<Movable> {
        return listOf(service.get(parent.id!!)!!)
    }

    override fun getForAdd(parent: Movable, data: Movable): Movable {
        return service.get(parent.id!!)!!
    }

    override fun getForUpdate(data: Movable): Movable {
        return getDomainData(getDataForUpdate(data).id!!)!!
    }

    override fun getForRemove(data: Movable): Movable {
        return getDomainData(data.id!!)!!
    }

    override fun getForDuplicate(data: Movable): Movable {
        return getDomainData(data.id!!)!!
    }

    override fun getForMove(data: Movable, up: Boolean): Movable {
        return getDomainData(data.id!!)!!
    }

}
