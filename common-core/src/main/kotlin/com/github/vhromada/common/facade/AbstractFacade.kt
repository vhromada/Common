package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.service.Service
import com.github.vhromada.common.validator.Validator

/**
 * An abstract class facade for data.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
abstract class AbstractFacade<T : Identifiable, U : Identifiable>(
    protected val service: Service<U>,
    protected val mapper: Mapper<T, U>,
    protected val validator: Validator<T, U>
) : Facade<T> {

    override fun get(id: Int): Result<T> {
        val data = service.get(id)
        val validationResult = validator.validateExists(data)
        return if (validationResult.isOk()) {
            Result.of(mapper.mapBack(data.get()))
        } else {
            Result.of(validationResult)
        }
    }

    override fun update(data: T): Result<Unit> {
        val result = validator.validate(data = data, update = true)
        return if (result.isOk()) updateData(data) else result
    }

    override fun remove(id: Int): Result<Unit> {
        val data = service.get(id)
        val validationResult = validator.validateExists(data)
        if (validationResult.isOk()) {
            service.remove(data.get())
        }
        return validationResult
    }

    override fun duplicate(id: Int): Result<Unit> {
        val data = service.get(id)
        val validationResult = validator.validateExists(data)
        if (validationResult.isOk()) {
            service.duplicate(data.get())
        }
        return validationResult
    }

    override fun moveUp(id: Int): Result<Unit> {
        return moveData(id = id, up = true)
    }

    override fun moveDown(id: Int): Result<Unit> {
        return moveData(id = id, up = false)
    }

    /**
     * Updates data.
     *
     * @param data data
     * @return result result with validation errors
     */
    protected abstract fun updateData(data: T): Result<Unit>

    /**
     * Returns list of data.
     *
     * @param data data
     * @return list of data
     */
    protected abstract fun getDataList(data: U): List<U>

    /**
     * Moves data in list.
     * <br></br>
     * Validation errors:
     *
     *  * Data can't be moved
     *  * Data doesn't exist in data storage
     *
     * @param id ID
     * @param up true if data is moving up
     * @return result with validation errors
     */
    private fun moveData(id: Int, up: Boolean): Result<Unit> {
        val data = service.get(id)
        var validationResult = validator.validateExists(data)
        if (validationResult.isError()) {
            return validationResult
        }
        val dataList = getDataList(data.get())
        validationResult = validator.validateMovingData(data = data.get(), list = dataList, up = up)
        if (validationResult.isOk()) {
            if (up) {
                service.moveUp(data.get())
            } else {
                service.moveDown(data.get())
            }
        }
        return validationResult
    }

}
