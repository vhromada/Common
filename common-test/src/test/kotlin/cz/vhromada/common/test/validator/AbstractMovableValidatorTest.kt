package cz.vhromada.common.test.validator

import cz.vhromada.common.Movable
import cz.vhromada.common.result.Event
import cz.vhromada.common.result.Severity
import cz.vhromada.common.result.Status
import cz.vhromada.common.test.stub.AbstractMovableValidatorStub
import cz.vhromada.common.test.stub.MovableStub
import cz.vhromada.common.validator.AbstractMovableValidator
import cz.vhromada.common.validator.MovableValidator
import cz.vhromada.common.validator.ValidationType
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test

/**
 * Event key
 */
private const val KEY = "key"

/**
 * Event value
 */
private const val VALUE = "value"

/**
 * A class represents test for class [AbstractMovableValidator].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableValidatorTest : MovableValidatorTest<Movable, Movable>() {

    /**
     * Test method for [AbstractMovableValidator.validate] with [ValidationType.DEEP].
     */
    @Test
    override fun validateDeep() {
        val movable = getValidatingData(1)

        initDeepMock(movable)

        val result = getValidator().validate(movable, ValidationType.DEEP)

        assertSoftly { softly ->
            softly.assertThat(result.status).isEqualTo(Status.WARN)
            softly.assertThat(result.events()).isEqualTo(listOf(Event(Severity.WARN, KEY, VALUE)))
        }

        verifyDeepMock(movable)
    }

    override fun getValidator(): MovableValidator<Movable> {
        return AbstractMovableValidatorStub(getName(), service, KEY, VALUE)
    }

    override fun getValidatingData(id: Int?): Movable {
        return MovableStub(id)
    }

    override fun getValidatingData(id: Int?, position: Int?): Movable {
        return MovableStub(id, position)
    }

    override fun getRepositoryData(validatingData: Movable): Movable {
        return MovableStub(validatingData.id)
    }

    override fun getItem1(): Movable {
        return MovableStub(1)
    }

    override fun getItem2(): Movable {
        return MovableStub(2)
    }

    override fun getName(): String {
        return "Stub"
    }

}
