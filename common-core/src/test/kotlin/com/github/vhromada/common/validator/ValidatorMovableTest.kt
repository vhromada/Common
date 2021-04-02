package com.github.vhromada.common.validator

import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.stub.MovableStub
import com.github.vhromada.common.stub.ValidatorMovableStub
import com.github.vhromada.common.utils.TestConstants
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

/**
 * An abstract class represents test for [Validator] for [Movable].
 *
 * @author Vladimir Hromada
 */
class ValidatorMovableTest {

    /**
     * Instance of [Validator]
     */
    private lateinit var validator: Validator<Movable, Movable>

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
        validator = ValidatorMovableStub(name = name, deepValidation = { _, _ -> })
    }

    /**
     * Test method for [Validator.validate] with correct new data.
     */
    @Test
    fun validateNew() {
        val result = validator.validate(data = MovableStub(id = null, position = null), update = false)

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
        val result = validator.validate(data = MovableStub(id = Int.MAX_VALUE, position = null), update = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_ID_NOT_NULL", message = "ID must be null.")))
        }
    }

    /**
     * Test method for [Validator.validate] with new data with not null position.
     */
    @Test
    fun validateNewNotNullPosition() {
        val result = validator.validate(data = MovableStub(id = null, position = Int.MAX_VALUE), update = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_POSITION_NOT_NULL", message = "Position must be null.")))
        }
    }

    /**
     * Test method for [Validator.validate] with new data with invalid deep data.
     */
    @Test
    fun validateNewInvalidDeepData() {
        validator = ValidatorMovableStub(name = name, deepValidation = { _, result -> run { result.addEvent(TestConstants.INVALID_DATA_EVENT) } })

        val result = validator.validate(data = MovableStub(id = null, position = null), update = false)

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
        val result = validator.validate(data = MovableStub(id = 1, position = 1), update = true)

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
        val result = validator.validate(data = MovableStub(id = null, position = 1), update = true)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_ID_NULL", message = "ID mustn't be null.")))
        }
    }

    /**
     * Test method for [Validator.validate] with update data with null position.
     */
    @Test
    fun validateUpdateNullPosition() {
        val result = validator.validate(data = MovableStub(id = 1, position = null), update = true)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_POSITION_NULL", message = "Position mustn't be null.")))
        }
    }

    /**
     * Test method for [Validator.validate] with update data with invalid deep data.
     */
    @Test
    fun validateUpdateInvalidDeepData() {
        validator = ValidatorMovableStub(name = name, deepValidation = { _, result -> run { result.addEvent(TestConstants.INVALID_DATA_EVENT) } })

        val result = validator.validate(data = MovableStub(id = 1, position = 1), update = true)

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
        val result = validator.validateExists(Optional.of(MovableStub(id = 1, position = 1)))

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
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

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
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

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
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

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
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

        val result = validator.validateMovingData(data = dataList[1], list = dataList, up = false)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "${prefix}_NOT_MOVABLE", message = "$name can't be moved down.")))
        }
    }

}
