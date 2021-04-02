package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.validator.AbstractValidator
import com.github.vhromada.common.validator.Validator

/**
 * A class represents stub for [Validator] for [Movable].
 *
 * @author Vladimir Hromada
 */
class ValidatorMovableStub(
    name: String,
    private val deepValidation: (data: Movable, result: Result<Unit>) -> Unit
) : AbstractValidator<Movable, Movable>(name = name) {

    override fun validateDataDeep(data: Movable, result: Result<Unit>) {
        return deepValidation.invoke(data, result)
    }

}
