package cz.vhromada.common.validator

import cz.vhromada.common.domain.AuditEntity
import cz.vhromada.common.entity.Movable
import cz.vhromada.common.result.Event
import cz.vhromada.common.result.Result
import cz.vhromada.common.result.Severity
import cz.vhromada.common.service.MovableService

/**
 * An abstract class represents validator for movable data.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
abstract class AbstractMovableValidator<T : Movable, U : AuditEntity>(
        private val name: String,
        protected val service: MovableService<U>) : MovableValidator<T> {

    /**
     * Prefix for validation keys
     */
    private val prefix: String = name.toUpperCase()

    override fun validate(data: T?, vararg validationTypes: ValidationType): Result<Unit> {
        if (data == null) {
            return Result.error(prefix + "_NULL", "$name mustn't be null.")
        }

        val result = Result<Unit>()
        if (validationTypes.contains(ValidationType.NEW)) {
            validateNewData(data, result)
        }
        if (validationTypes.contains(ValidationType.UPDATE)) {
            validateUpdateData(data, result)
        }
        if (validationTypes.contains(ValidationType.EXISTS)) {
            validateExistingData(data, result)
        }
        if (validationTypes.contains(ValidationType.DEEP)) {
            validateDataDeep(data, result)
        }
        if (validationTypes.contains(ValidationType.UP)) {
            validateMovingData(data, result, true)
        }
        if (validationTypes.contains(ValidationType.DOWN)) {
            validateMovingData(data, result, false)
        }

        return result
    }

    /**
     * Returns data from repository.
     *
     * @param data data
     * @return data from repository
     */
    protected open fun getData(data: T): Movable? {
        return service.get(data.id!!)
    }

    /**
     * Returns list of data from repository.
     *
     * @param data data
     * @return list of data from repository
     */
    protected open fun getList(data: T): List<Movable> {
        return service.getAll()
    }

    /**
     * Validates data deeply.
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    protected abstract fun validateDataDeep(data: T, result: Result<Unit>)

    /**
     * Validates new data.
     * <br></br>
     * Validation errors:
     *
     *  * ID isn't null
     *  * Position isn't null
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    private fun validateNewData(data: T, result: Result<Unit>) {
        if (data.id != null) {
            result.addEvent(Event(Severity.ERROR, prefix + "_ID_NOT_NULL", "ID must be null."))
        }
        if (data.position != null) {
            result.addEvent(Event(Severity.ERROR, prefix + "_POSITION_NOT_NULL", "Position must be null."))
        }
    }

    /**
     * Validates new data.
     * <br></br>
     * Validation errors:
     *
     *  * Position is null
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    private fun validateUpdateData(data: T, result: Result<Unit>) {
        if (data.position == null) {
            result.addEvent(Event(Severity.ERROR, prefix + "_POSITION_NULL", "Position mustn't be null."))
        }
    }

    /**
     * Validates existing data.
     * <br></br>
     * Validation errors:
     *
     *  * ID is null
     *  * Data doesn't exist in data storage
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    private fun validateExistingData(data: T, result: Result<Unit>) {
        when {
            data.id == null -> {
                result.addEvent(Event(Severity.ERROR, prefix + "_ID_NULL", "ID mustn't be null."))
            }
            getData(data) == null -> {
                result.addEvent(Event(Severity.ERROR, prefix + "_NOT_EXIST", "$name doesn't exist."))
            }
        }
    }

    /**
     * Validates moving data.
     * <br></br>
     * Validation errors:
     *
     *  * Not movable data
     *
     * @param data   validating data
     * @param result result with validation errors
     * @param up     true if data is moving up
     */
    private fun validateMovingData(data: T, result: Result<Unit>, up: Boolean) {
        if (data.id != null) {
            val domainData = getData(data)
            if (domainData != null) {
                val list = getList(data)
                val index = list.indexOf(domainData)
                when {
                    up && index <= 0 -> {
                        result.addEvent(Event(Severity.ERROR, prefix + "_NOT_MOVABLE", "$name can't be moved up."))
                    }
                    !up && (index < 0 || index >= list.size - 1) -> {
                        result.addEvent(Event(Severity.ERROR, prefix + "_NOT_MOVABLE", "$name can't be moved down."))
                    }
                }
            }
        }
    }

}
