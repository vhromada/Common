package com.github.vhromada.common.facade

import com.github.vhromada.common.domain.Audit
import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.TimeProvider
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.service.MovableService
import com.github.vhromada.common.validator.MovableValidator
import com.github.vhromada.common.validator.ValidationType

/**
 * An abstract class facade for movable data for parent data.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
abstract class AbstractMovableParentFacade<T : Movable, U : AuditEntity>(
        protected val service: MovableService<U>,
        private val accountProvider: AccountProvider,
        private val timeProvider: TimeProvider,
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
            val updateData = getDataForUpdate(data)
            updateData.audit = service.get(data.id!!)!!.audit
            updateData.modify(getAudit())
            service.update(updateData)
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

    /**
     * Returns audit.
     *
     * @return audit
     */
    protected open fun getAudit(): Audit {
        return Audit(accountProvider.getAccount().id, timeProvider.getTime())
    }

}
