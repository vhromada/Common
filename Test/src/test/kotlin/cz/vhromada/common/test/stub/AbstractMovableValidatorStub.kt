package cz.vhromada.common.test.stub

import cz.vhromada.common.Movable
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.validator.AbstractMovableValidator
import cz.vhromada.validation.result.Event
import cz.vhromada.validation.result.Result
import cz.vhromada.validation.result.Severity

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
