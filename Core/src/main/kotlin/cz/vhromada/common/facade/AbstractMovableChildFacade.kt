package cz.vhromada.common.facade

import cz.vhromada.common.Movable
import cz.vhromada.common.mapper.Mapper
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.utils.sorted
import cz.vhromada.common.validator.MovableValidator
import cz.vhromada.common.validator.ValidationType
import cz.vhromada.validation.result.Result
import cz.vhromada.validation.result.Status

/**
 * An abstract class facade for movable data for child data.
 *
 * @param <S> type of child entity data
 * @param <T> type of child domain data
 * @param <U> type of parent entity data
 * @param <V> type of domain repository data
 * @author Vladimir Hromada
 */
abstract class AbstractMovableChildFacade<S : Movable, T : Movable, U : Movable, V : Movable>(
        protected val service: MovableService<V>,
        private val mapper: Mapper<S, T>,
        private val parentValidator: MovableValidator<U>,
        private val childValidator: MovableValidator<S>) : MovableChildFacade<S, U> {

    override fun get(id: Int): Result<S> {
        val item = getDomainData(id) ?: return Result()
        return Result.of(mapper.mapBack(item))
    }

    override fun add(parent: U, data: S): Result<Unit> {
        val result = parentValidator.validate(parent, ValidationType.EXISTS)
        result.addEvents(childValidator.validate(data, ValidationType.NEW, ValidationType.DEEP).events())
        if (Status.OK == result.status) {
            service.update(getForAdd(parent, getDataForAdd(data)))
        }
        return result
    }

    override fun update(data: S): Result<Unit> {
        val result = childValidator.validate(data, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP)
        if (Status.OK == result.status) {
            service.update(getForUpdate(data))
        }
        return result
    }

    override fun remove(data: S): Result<Unit> {
        val result = childValidator.validate(data, ValidationType.EXISTS)
        if (Status.OK == result.status) {
            service.update(getForRemove(data))
        }
        return result
    }

    override fun duplicate(data: S): Result<Unit> {
        val result = childValidator.validate(data, ValidationType.EXISTS)
        if (Status.OK == result.status) {
            service.update(getForDuplicate(data))
        }
        return result
    }

    override fun moveUp(data: S): Result<Unit> {
        val result = childValidator.validate(data, ValidationType.EXISTS, ValidationType.UP)
        if (Status.OK == result.status) {
            service.update(getForMove(data, true))
        }
        return result
    }

    override fun moveDown(data: S): Result<Unit> {
        val result = childValidator.validate(data, ValidationType.EXISTS, ValidationType.DOWN)
        if (Status.OK == result.status) {
            service.update(getForMove(data, false))
        }
        return result
    }

    override fun find(parent: U): Result<List<S>> {
        val validationResult = parentValidator.validate(parent, ValidationType.EXISTS)
        if (Status.OK == validationResult.status) {
            return Result.of(mapper.mapBack(getDomainList(parent)).sorted())
        }
        val result = Result<List<S>>()
        result.addEvents(validationResult.events())
        return result
    }

    /**
     * Returns data for add.
     *
     * @param data data
     * @return data for add
     */
    protected open fun getDataForUpdate(data: S): T {
        return mapper.map(data)
    }

    /**
     * Returns domain data with specified ID.
     *
     * @param id ID
     * @return domain data with specified ID
     */
    protected abstract fun getDomainData(id: Int): T?

    /**
     * Returns data for specified parent.
     *
     * @param parent parent
     * @return data for specified parent
     */
    protected abstract fun getDomainList(parent: U): List<T>

    /**
     * Returns data for add.
     *
     * @param parent parent
     * @param data   data
     * @return data for add
     */
    protected abstract fun getForAdd(parent: U, data: T): V

    /**
     * Returns data for update.
     *
     * @param data data
     * @return data for update
     */
    protected abstract fun getForUpdate(data: S): V

    /**
     * Returns data for remove.
     *
     * @param data data
     * @return data for remove
     */
    protected abstract fun getForRemove(data: S): V

    /**
     * Returns data for duplicate.
     *
     * @param data data
     * @return data for duplicate
     */
    protected abstract fun getForDuplicate(data: S): V

    /**
     * Returns data for duplicate.
     *
     * @param data data
     * @param up   true if moving data up
     * @return data for duplicate
     */
    protected abstract fun getForMove(data: S, up: Boolean): V

    /**
     * Returns data for add.
     *
     * @param data data
     * @return data for add
     */
    private fun getDataForAdd(data: S): T {
        val updatedData = mapper.map(data)
        updatedData.position = Integer.MAX_VALUE
        return updatedData
    }

}