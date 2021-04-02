package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.validator.AbstractValidator
import com.github.vhromada.common.validator.Validator

/**
 * A class represents stub for [Validator] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
class ValidatorIdentifiableStub(
    name: String,
    private val deepValidation: (data: Identifiable, result: Result<Unit>) -> Unit
) : AbstractValidator<Identifiable, Identifiable>(name = name) {

    override fun validateDataDeep(data: Identifiable, result: Result<Unit>) {
        return deepValidation.invoke(data, result)
    }

}
