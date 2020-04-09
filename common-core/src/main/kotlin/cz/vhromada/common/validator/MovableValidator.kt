package cz.vhromada.common.validator

import cz.vhromada.common.entity.Movable
import cz.vhromada.common.result.Result

/**
 * An interface represents validator for movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
interface MovableValidator<T : Movable> {

    /**
     * Validates data.
     *
     * @param data            validating data
     * @param validationTypes types of validation
     * @return result with validation errors
     */
    fun validate(data: T?, vararg validationTypes: ValidationType): Result<Unit>

}
