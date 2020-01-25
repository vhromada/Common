package cz.vhromada.common.test.stub

import cz.vhromada.common.Movable
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
        service: MovableService<Movable>,
        private val key: String,
        private val value: String) : AbstractMovableValidator<Movable, Movable>(name, service) {

    override fun validateDataDeep(data: Movable, result: Result<Unit>) {
        result.addEvent(Event(Severity.WARN, key, value))
    }

}
