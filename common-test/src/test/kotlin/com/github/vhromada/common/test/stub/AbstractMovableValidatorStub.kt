package com.github.vhromada.common.test.stub

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.service.MovableService
import com.github.vhromada.common.validator.AbstractMovableValidator

/**
 * A class represents stub for [AbstractMovableValidator].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableValidatorStub(
        name: String,
        service: MovableService<AuditEntity>,
        private val key: String,
        private val value: String) : AbstractMovableValidator<Movable, AuditEntity>(name, service) {

    override fun validateDataDeep(data: Movable, result: Result<Unit>) {
        result.addEvent(Event(Severity.WARN, key, value))
    }

}
