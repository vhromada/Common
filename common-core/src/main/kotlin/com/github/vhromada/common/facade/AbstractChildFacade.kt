package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.service.ChildService
import com.github.vhromada.common.service.Service
import com.github.vhromada.common.validator.Validator

/**
 * An abstract class facade for child data.
 *
 * @param <T> type of child entity data
 * @param <U> type of child domain data
 * @param <V> type of parent entity data
 * @param <W> type of parent domain data
 * @author Vladimir Hromada
 */
abstract class AbstractChildFacade<T : Identifiable, U : Identifiable, V : Identifiable, W : Identifiable>(
    private val childService: ChildService<U>,
    protected val parentService: Service<W>,
    mapper: Mapper<T, U>,
    childValidator: Validator<T, U>,
    protected val parentValidator: Validator<V, W>
) : AbstractFacade<T, U>(service = childService, mapper = mapper, validator = childValidator), ChildFacade<T, V> {

    override fun add(parent: Int, data: T): Result<Unit> {
        val storedParent = parentService.get(parent)
        val parentValidationResult = parentValidator.validateExists(storedParent)
        val childValidationResult = validator.validate(data, update = false)
        return if (parentValidationResult.isOk() && childValidationResult.isOk()) {
            addData(parent = storedParent.get(), data = data)
        } else {
            Result.of(parentValidationResult, childValidationResult)
        }
    }

    override fun find(parent: Int): Result<List<T>> {
        val data = parentService.get(parent)
        val validationResult = parentValidator.validateExists(data)
        return if (validationResult.isOk()) {
            Result.of(mapper.mapBack(childService.find(parent)))
        } else {
            return Result.of(validationResult)
        }
    }

    final override fun getDataList(data: U): List<U> {
        return childService.find(getParent(data).id!!)
    }

    /**
     * Adds data.
     *
     * @param parent parent
     * @param data   data
     * @return result with validation errors
     */
    protected abstract fun addData(parent: W, data: T): Result<Unit>

    /**
     * Returns parent.
     *
     * @param data data
     * @return parent
     */
    protected abstract fun getParent(data: U): W

}
