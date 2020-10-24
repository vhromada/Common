package com.github.vhromada.common.test.validator

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.test.stub.AbstractMovableValidatorStub
import com.github.vhromada.common.test.stub.AuditEntityStub
import com.github.vhromada.common.validator.AbstractMovableValidator
import com.github.vhromada.common.validator.MovableValidator
import com.github.vhromada.common.validator.ValidationType
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
class AbstractMovableValidatorTest : MovableValidatorTest<Movable, AuditEntity>() {

    /**
     * Test method for [AbstractMovableValidator.validate] with [ValidationType.DEEP].
     */
    @Test
    override fun validateDeep() {
        val movable = getValidatingData(1)

        initDeepMock(movable)

        val result = getValidator().validate(movable, ValidationType.DEEP)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.WARN)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.WARN, KEY, VALUE)))
        }

        verifyDeepMock(movable)
    }

    override fun getValidator(): MovableValidator<Movable> {
        return AbstractMovableValidatorStub(getName(), service, KEY, VALUE)
    }

    override fun getValidatingData(id: Int?): Movable {
        return AuditEntityStub(id)
    }

    override fun getValidatingData(id: Int?, position: Int?): Movable {
        return AuditEntityStub(id, position)
    }

    override fun getRepositoryData(validatingData: Movable): AuditEntity {
        return AuditEntityStub(validatingData.id)
    }

    override fun getItem1(): AuditEntity {
        return AuditEntityStub(1)
    }

    override fun getItem2(): AuditEntity {
        return AuditEntityStub(2)
    }

    override fun getName(): String {
        return "Stub"
    }

}
