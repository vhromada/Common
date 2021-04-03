package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.facade.AbstractParentFacade
import com.github.vhromada.common.facade.ParentFacade
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.service.ParentService
import com.github.vhromada.common.validator.Validator

/**
 * A class represents stub for [ParentFacade] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
class ParentFacadeIdentifiableStub(
    service: ParentService<Identifiable>,
    mapper: Mapper<Identifiable, Identifiable>,
    validator: Validator<Identifiable, Identifiable>
) : AbstractParentFacade<Identifiable, Identifiable>(parentService = service, mapper = mapper, validator = validator) {

    override fun updateData(data: Identifiable): Result<Unit> {
        service.update(data = mapper.map(source = data))
        return Result()
    }

    override fun addData(data: Identifiable): Result<Unit> {
        service.add(data = mapper.map(source = data))
        return Result()
    }

}
