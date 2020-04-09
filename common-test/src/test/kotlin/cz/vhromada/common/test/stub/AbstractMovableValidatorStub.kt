package cz.vhromada.common.test.stub

import cz.vhromada.common.domain.AuditEntity
import cz.vhromada.common.entity.Movable
import cz.vhromada.common.result.Event
import cz.vhromada.common.result.Result
import cz.vhromada.common.result.Severity
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.validator.AbstractMovableValidator

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
