package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.service.ParentService
import com.github.vhromada.common.validator.Validator

/**
 * An abstract class facade for parent data.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
abstract class AbstractParentFacade<T : Identifiable, U : Identifiable>(
    private val parentService: ParentService<U>,
    mapper: Mapper<T, U>,
    validator: Validator<T, U>
) : AbstractFacade<T, U>(service = parentService, mapper = mapper, validator = validator), ParentFacade<T> {

    override fun newData(): Result<Unit> {
        parentService.newData()
        return Result()
    }

    override fun getAll(): Result<List<T>> {
        return Result.of(mapper.mapBack(parentService.getAll()))
    }

    override fun add(data: T): Result<Unit> {
        val validationResult = validator.validate(data = data, update = false)
        return if (validationResult.isOk()) addData(data) else validationResult
    }

    override fun updatePositions(): Result<Unit> {
        parentService.updatePositions()
        return Result()
    }

    final override fun getDataList(data: U): List<U> {
        return parentService.getAll()
    }

    /**
     * Adds data.
     *
     * @param data data
     * @return result result with validation errors
     */
    protected abstract fun addData(data: T): Result<Unit>

}
