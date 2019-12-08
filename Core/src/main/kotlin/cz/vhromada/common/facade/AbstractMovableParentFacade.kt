package cz.vhromada.common.facade

import cz.vhromada.common.Movable
import cz.vhromada.common.mapper.Mapper
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.validator.MovableValidator
import cz.vhromada.common.validator.ValidationType
import cz.vhromada.validation.result.Result
import cz.vhromada.validation.result.Status

/**
 * An abstract class facade for movable data for parent data.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
abstract class AbstractMovableParentFacade<T : Movable, U : Movable>(
        protected val service: MovableService<U>,
        private val mapper: Mapper<T, U>,
        private val validator: MovableValidator<T>) : MovableParentFacade<T> {

    override fun getAll(): Result<List<T>> {
        return Result.of(mapper.mapBack(service.getAll()))
    }

    override fun newData(): Result<Unit> {
        service.newData()
        return Result()
    }

    override fun get(id: Int): Result<T> {
        val item = service.get(id) ?: return Result()
        return Result.of(mapper.mapBack(item))
    }

    override fun add(data: T): Result<Unit> {
        val result = validator.validate(data, ValidationType.NEW, ValidationType.DEEP)
        if (Status.OK == result.status) {
            service.add(getDataForAdd(data))
        }
        return result
    }

    override fun update(data: T): Result<Unit> {
        val result = validator.validate(data, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP)
        if (Status.OK == result.status) {
            service.update(getDataForUpdate(data))
        }
        return result
    }

    override fun remove(data: T): Result<Unit> {
        val result = validator.validate(data, ValidationType.EXISTS)
        if (Status.OK == result.status) {
            service.remove(service.get(data.id!!)!!)
        }
        return result
    }

    override fun duplicate(data: T): Result<Unit> {
        val result = validator.validate(data, ValidationType.EXISTS)
        if (Status.OK == result.status) {
            service.duplicate(service.get(data.id!!)!!)
        }
        return result
    }

    override fun moveUp(data: T): Result<Unit> {
        val result = validator.validate(data, ValidationType.EXISTS, ValidationType.UP)
        if (Status.OK == result.status) {
            service.moveUp(service.get(data.id!!)!!)
        }
        return result
    }

    override fun moveDown(data: T): Result<Unit> {
        val result = validator.validate(data, ValidationType.EXISTS, ValidationType.DOWN)
        if (Status.OK == result.status) {
            service.moveDown(service.get(data.id!!)!!)
        }
        return result
    }

    override fun updatePositions(): Result<Unit> {
        service.updatePositions()
        return Result()
    }

    /**
     * Returns data for add.
     *
     * @param data data
     * @return data for add
     */
    protected open fun getDataForAdd(data: T): U {
        return mapper.map(data)
    }

    /**
     * Returns data for update.
     *
     * @param data data
     * @return data for update
     */
    protected open fun getDataForUpdate(data: T): U {
        return mapper.map(data)
    }

}
