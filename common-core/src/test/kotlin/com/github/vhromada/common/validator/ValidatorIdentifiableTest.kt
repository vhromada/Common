package com.github.vhromada.common.validator

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.stub.IdentifiableStub
import com.github.vhromada.common.stub.ValidatorIdentifiableStub
import com.github.vhromada.common.utils.TestConstants
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

/**
 * An abstract class represents test for [Validator] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
class ValidatorIdentifiableTest {

    /**
     * Instance of [Validator]
     */
    private lateinit var validator: Validator<Identifiable, Identifiable>

    /**
     * Name
     */
    private val name = "Stub"

    /**
     * Prefix
     */
    private val prefix = name.toUpperCase()

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = ValidatorIdentifiableStub(name = name, deepValidation = { _, _ -> })
    }

    /**
     * Test method for [Validator.validate] with correct new data.
     */
    @Test
    fun validateNew() {
        val result = validator.validate(data = IdentifiableStub(null), update = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }
    }

    /**
     * Test method for [Validator.validate] with null new data.
     */
    @Test
    fun validateNewNull() {
        val result = validator.validate(data = null, update = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_NULL", message = "$name mustn't be null.")))
        }
    }

    /**
     * Test method for [Validator.validate] with new data with not null ID.
     */
    @Test
    fun validateNewNotNullId() {
        val result = validator.validate(data = IdentifiableStub(Int.MAX_VALUE), update = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_ID_NOT_NULL", message = "ID must be null.")))
        }
    }

    /**
     * Test method for [Validator.validate] with new data with invalid deep data.
     */
    @Test
    fun validateNewInvalidDeepData() {
        validator = ValidatorIdentifiableStub(name = name, deepValidation = { _, result -> run { result.addEvent(TestConstants.INVALID_DATA_EVENT) } })

        val result = validator.validate(data = IdentifiableStub(null), update = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(TestConstants.INVALID_DATA_EVENT))
        }
    }

    /**
     * Test method for [Validator.validate] with with update correct data.
     */
    @Test
    fun validateUpdate() {
        val result = validator.validate(data = IdentifiableStub(1), update = true)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }
    }

    /**
     * Test method for [Validator.validate] with null update data.
     */
    @Test
    fun validateUpdateNull() {
        val result = validator.validate(data = null, update = true)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_NULL", message = "$name mustn't be null.")))
        }
    }

    /**
     * Test method for [Validator.validate] with update data with null ID.
     */
    @Test
    fun validateUpdateNullId() {
        val result = validator.validate(data = IdentifiableStub(null), update = true)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_ID_NULL", message = "ID mustn't be null.")))
        }
    }

    /**
     * Test method for [Validator.validate] with update data with invalid deep data.
     */
    @Test
    fun validateUpdateInvalidDeepData() {
        validator = ValidatorIdentifiableStub(name = name, deepValidation = { _, result -> run { result.addEvent(TestConstants.INVALID_DATA_EVENT) } })

        val result = validator.validate(data = IdentifiableStub(1), update = true)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(TestConstants.INVALID_DATA_EVENT))
        }
    }

    /**
     * Test method for [Validator.validateExists] with correct data.
     */
    @Test
    fun validateExists() {
        val result = validator.validateExists(Optional.of(IdentifiableStub(1)))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }
    }

    /**
     * Test method for [Validator.validateExists] with invalid data.
     */
    @Test
    fun validateExistsInvalid() {
        val result = validator.validateExists(Optional.empty())

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_NOT_EXIST", message = "$name doesn't exist.")))
        }
    }

    /**
     * Test method for [Validator.validateMovingData] with correct up data.
     */
    @Test
    fun validateMovingDataUp() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        val result = validator.validateMovingData(data = dataList[1], list = dataList, up = true)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }
    }

    /**
     * Test method for [Validator.validateMovingData] with with invalid up data.
     */
    @Test
    fun validateMovingDataUpInvalid() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        val result = validator.validateMovingData(data = dataList[0], list = dataList, up = true)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_NOT_MOVABLE", message = "$name can't be moved up.")))
        }
    }

    /**
     * Test method for [Validator.validateMovingData] with correct down data.
     */
    @Test
    fun validateMovingDataDown() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        val result = validator.validateMovingData(data = dataList[0], list = dataList, up = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }
    }

    /**
     * Test method for [Validator.validateMovingData] with with invalid down data.
     */
    @Test
    fun validateMovingDataDownInvalid() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        val result = validator.validateMovingData(data = dataList[1], list = dataList, up = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_NOT_MOVABLE", message = "$name can't be moved down.")))
        }
    }

}
