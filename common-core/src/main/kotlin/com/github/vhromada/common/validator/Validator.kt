package com.github.vhromada.common.validator

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.result.Result
import java.util.Optional

/**
 * An interface represents validator for data.
 *
 * @param <T> type of entity data
 * @param <T> type of domain data
 * @author Vladimir Hromada
 */
interface Validator<T : Identifiable, U : Identifiable> {

    /**
     * Validates data.
     *
     * @param data   validating data
     * @param update true if data is for update
     * @return result with validation errors
     */
    fun validate(data: T?, update: Boolean): Result<Unit>

    /**
     * Validates existing data.
     *
     * @param data validating data
     * @return result with validation errors
     */
    fun validateExists(data: Optional<U>): Result<Unit>

    /**
     * Validates moving data.
     *
     * @param data validating data
     * @param list list of data
     * @param up   true if data is moving up
     * @param <U>  type of validating data
     * @return result with validation errors
     */
    fun validateMovingData(data: U, list: List<U>, up: Boolean): Result<Unit>

}
