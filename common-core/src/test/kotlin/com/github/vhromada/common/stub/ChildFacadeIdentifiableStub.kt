package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.facade.AbstractChildFacade
import com.github.vhromada.common.facade.ChildFacade
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.service.ChildService
import com.github.vhromada.common.service.ParentService
import com.github.vhromada.common.validator.Validator

/**
 * A class represents stub for [ChildFacade] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
class ChildFacadeIdentifiableStub(
    childService: ChildService<Identifiable>,
    parentService: ParentService<Identifiable>,
    mapper: Mapper<Identifiable, Identifiable>,
    parentValidator: Validator<Identifiable, Identifiable>,
    childValidator: Validator<Identifiable, Identifiable>,
    private val parent: (data: Identifiable) -> Identifiable
) : AbstractChildFacade<Identifiable, Identifiable, Identifiable, Identifiable>(
    childService = childService,
    parentService = parentService,
    mapper = mapper,
    childValidator = childValidator,
    parentValidator = parentValidator
) {

    override fun updateData(data: Identifiable): Result<Unit> {
        service.update(data = mapper.map(source = data))
        return Result()
    }

    override fun addData(parent: Identifiable, data: Identifiable): Result<Unit> {
        service.add(data = mapper.map(source = data))
        return Result()
    }

    override fun getParent(data: Identifiable): Identifiable {
        return parent.invoke(data)
    }

}
