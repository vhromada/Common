package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.AbstractParentFacade
import com.github.vhromada.common.facade.ParentFacade
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.service.ParentService
import com.github.vhromada.common.validator.Validator

/**
 * A class represents stub for [ParentFacade] for [Movable].
 *
 * @author Vladimir Hromada
 */
class ParentFacadeMovableStub(
    service: ParentService<Movable>,
    mapper: Mapper<Movable, Movable>,
    validator: Validator<Movable, Movable>
) : AbstractParentFacade<Movable, Movable>(parentService = service, mapper = mapper, validator = validator) {

    override fun updateData(data: Movable): Result<Unit> {
        service.update(mapper.map(data))
        return Result()
    }

    override fun addData(data: Movable): Result<Unit> {
        service.add(mapper.map(data))
        return Result()
    }

}
