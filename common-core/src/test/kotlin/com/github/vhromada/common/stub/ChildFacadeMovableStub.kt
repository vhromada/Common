package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.AbstractChildFacade
import com.github.vhromada.common.facade.ChildFacade
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.service.ChildService
import com.github.vhromada.common.service.ParentService
import com.github.vhromada.common.validator.Validator

/**
 * A class represents stub for [ChildFacade] for [Movable].
 *
 * @author Vladimir Hromada
 */
class ChildFacadeMovableStub(
    childService: ChildService<Movable>,
    parentService: ParentService<Movable>,
    mapper: Mapper<Movable, Movable>,
    childValidator: Validator<Movable, Movable>,
    parentValidator: Validator<Movable, Movable>,
    private val parent: (data: Movable) -> Movable
) : AbstractChildFacade<Movable, Movable, Movable, Movable>(
    childService = childService,
    parentService = parentService,
    mapper = mapper,
    childValidator = childValidator,
    parentValidator = parentValidator
) {

    override fun updateData(data: Movable): Result<Unit> {
        service.update(data = mapper.map(source = data))
        return Result()
    }

    override fun addData(parent: Movable, data: Movable): Result<Unit> {
        service.add(data = mapper.map(source = data))
        return Result()
    }

    override fun getParent(data: Movable): Movable {
        return parent.invoke(data)
    }

}
