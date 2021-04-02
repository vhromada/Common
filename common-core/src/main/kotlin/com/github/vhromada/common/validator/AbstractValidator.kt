package com.github.vhromada.common.validator

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Severity
import java.util.Optional

/**
 * An abstract class represents validator for data.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
abstract class AbstractValidator<T : Identifiable, U : Identifiable>(
    protected val name: String
) : Validator<T, U> {

    final override fun validate(data: T?, update: Boolean): Result<Unit> {
        return if (data == null) {
            Result.error(key = "${getPrefix()}_NULL", message = "$name mustn't be null.")
        } else {
            if (update) validateUpdateData(data) else validateNewData(data)
        }
    }

    final override fun validateExists(data: Optional<U>): Result<Unit> {
        return if (data.isEmpty) Result.error(key = "${getPrefix()}_NOT_EXIST", message = "$name doesn't exist.") else Result()
    }

    final override fun validateMovingData(data: U, list: List<U>, up: Boolean): Result<Unit> {
        val result = Result<Unit>()
        val index = list.indexOf(data)
        when {
            up && index <= 0 -> {
                result.addEvent(Event(severity = Severity.ERROR, key = "${getPrefix()}_NOT_MOVABLE", message = "$name can't be moved up."))
            }
            !up && (index < 0 || index >= list.size - 1) -> {
                result.addEvent(Event(severity = Severity.ERROR, key = "${getPrefix()}_NOT_MOVABLE", message = "$name can't be moved down."))
            }
        }
        return result
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
     *  * Position isn't null for movable data
     *  * Deep validation errors
     *
     * @param data validating data
     * @return result with validation errors
     */
    private fun validateNewData(data: T): Result<Unit> {
        val result = Result<Unit>()
        if (data.id != null) {
            result.addEvent(Event(severity = Severity.ERROR, key = "${getPrefix()}_ID_NOT_NULL", message = "ID must be null."))
        }
        if (data is Movable && data.position != null) {
            result.addEvent(Event(severity = Severity.ERROR, key = "${getPrefix()}_POSITION_NOT_NULL", message = "Position must be null."))
        }
        validateDataDeep(data = data, result = result)
        return result
    }

    /**
     * Validates update data.
     * <br></br>
     * Validation errors:
     *
     *  * ID is null
     *  * Position is null for movable data
     *  * Deep validation errors
     *
     * @param data validating data
     * @return result with validation errors
     */
    private fun validateUpdateData(data: T): Result<Unit> {
        val result = Result<Unit>()
        if (data.id == null) {
            result.addEvent(Event(severity = Severity.ERROR, key = "${getPrefix()}_ID_NULL", message = "ID mustn't be null."))
        }
        if (data is Movable && data.position == null) {
            result.addEvent(Event(severity = Severity.ERROR, key = "${getPrefix()}_POSITION_NULL", message = "Position mustn't be null."))
        }
        validateDataDeep(data = data, result = result)
        return result
    }

    /**
     * Returns prefix for validation keys.
     *
     * @return prefix for validation keys
     */
    private fun getPrefix(): String {
        return name.toUpperCase()
    }

}
