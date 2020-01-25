package cz.vhromada.common.test.validator

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import cz.vhromada.common.Movable
import cz.vhromada.common.result.Event
import cz.vhromada.common.result.Severity
import cz.vhromada.common.result.Status
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.validator.MovableValidator
import cz.vhromada.common.validator.ValidationType
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

/**
 * ID
 */
private const val ID = 5

/**
 * An abstract class represents test for [MovableValidator].
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
@Suppress("FunctionName")
abstract class MovableValidatorTest<T : Movable, U : Movable> {

    /**
     * Instance of [MovableService]
     */
    @Mock
    protected lateinit var service: MovableService<U>

    /**
     * Instance of [MovableValidator]
     */
    private lateinit var validator: MovableValidator<T>

    /**
     * Initializes validator.
     */
    @BeforeEach
    open fun setUp() {
        validator = getValidator()
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.NEW] with correct data.
     */
    @Test
    fun validateNew() {
        val result = validator.validate(getValidatingData(null, null), ValidationType.NEW)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verifyZeroInteractions(service)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.NEW] with data with not null ID.
     */
    @Test
    fun validateNewNotNullId() {
        val result = validator.validate(getValidatingData(Integer.MAX_VALUE, null), ValidationType.NEW)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_ID_NOT_NULL", "ID must be null.")))
        }

        verifyZeroInteractions(service)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.NEW] with data with not null position.
     */
    @Test
    fun validateNewNotNullPosition() {
        val result = validator.validate(getValidatingData(null, Integer.MAX_VALUE), ValidationType.NEW)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_POSITION_NOT_NULL", "Position must be null.")))
        }

        verifyZeroInteractions(service)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.UPDATE] with correct data.
     */
    @Test
    fun validateUpdate() {
        val result = validator.validate(getValidatingData(ID, ID - 1), ValidationType.UPDATE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verifyZeroInteractions(service)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.UPDATE] with data with null position.
     */
    @Test
    fun validateUpdateNullPosition() {
        val result = validator.validate(getValidatingData(ID, null), ValidationType.UPDATE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_POSITION_NULL", "Position mustn't be null.")))
        }

        verifyZeroInteractions(service)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.EXISTS] with correct data.
     */
    @Test
    fun validateExists() {
        val validatingData = getValidatingData(ID)

        initExistsMock(validatingData, true)

        val result = validator.validate(validatingData, ValidationType.EXISTS)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verifyExistsMock(validatingData)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.EXISTS] with data with null ID.
     */
    @Test
    fun validateExistsNullId() {
        val result = validator.validate(getValidatingData(null), ValidationType.EXISTS)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_ID_NULL", "ID mustn't be null.")))
        }

        verifyZeroInteractions(service)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.EXISTS] with not existing data.
     */
    @Test
    fun validateExistsNotExistingData() {
        val validatingData = getValidatingData(ID)

        initExistsMock(validatingData, false)

        val result = validator.validate(validatingData, ValidationType.EXISTS)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_NOT_EXIST", "${getName()} doesn't exist.")))
        }

        verifyExistsMock(validatingData)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.UP] with correct data.
     */
    @Test
    fun validateUp() {
        val validatingData = getValidatingData(ID)

        initMovingMock(validatingData, up = true, valid = true)

        val result = validator.validate(validatingData, ValidationType.UP)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verifyMovingMock(validatingData)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.UP] with invalid data.
     */
    @Test
    fun validateUpInvalid() {
        val validatingData = getValidatingData(Integer.MAX_VALUE)

        initMovingMock(validatingData, up = true, valid = false)

        val result = validator.validate(validatingData, ValidationType.UP)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_NOT_MOVABLE", "${getName()} can't be moved up.")))
        }

        verifyMovingMock(validatingData)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.DOWN] with correct data.
     */
    @Test
    fun validateDown() {
        val validatingData = getValidatingData(ID)

        initMovingMock(validatingData, up = false, valid = true)

        val result = validator.validate(validatingData, ValidationType.DOWN)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verifyMovingMock(validatingData)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.DOWN] with invalid data.
     */
    @Test
    fun validateDownInvalid() {
        val validatingData = getValidatingData(Integer.MAX_VALUE)

        initMovingMock(validatingData, up = false, valid = false)

        val result = validator.validate(validatingData, ValidationType.DOWN)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_NOT_MOVABLE", "${getName()} can't be moved down.")))
        }

        verifyMovingMock(validatingData)
    }

    /**
     * Test method for [MovableValidator.validate] with [ValidationType.DEEP] with correct data.
     */
    @Test
    open fun validateDeep() {
        val validatingData = getValidatingData(ID)

        initDeepMock(validatingData)

        val result = validator.validate(validatingData, ValidationType.DEEP)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verifyDeepMock(validatingData)
    }

    /**
     * Initializes mock for exists.
     *
     * @param validatingData validating data
     * @param exists         true if data exists
     */
    protected open fun initExistsMock(validatingData: T, exists: Boolean) {
        val result = if (exists) getRepositoryData(validatingData) else null

        whenever(service.get(any())).thenReturn(result)
    }

    /**
     * Verifies mock for exists.
     *
     * @param validatingData validating data
     */
    protected open fun verifyExistsMock(validatingData: T) {
        verify(service).get(validatingData.id!!)
        verifyNoMoreInteractions(service)
    }

    /**
     * Initializes mock for deep.
     *
     * @param validatingData validating data
     */
    protected open fun initDeepMock(validatingData: T) {
        // implementation in overridden methods
    }

    /**
     * Verifies mock for deep.
     *
     * @param validatingData validating data
     */
    protected open fun verifyDeepMock(validatingData: T) {
        verifyZeroInteractions(service)
    }

    /**
     * Initializes mock for moving.
     *
     * @param validatingData validating data
     * @param up             true if moving up
     * @param valid          true if data should be valid
     */
    protected open fun initMovingMock(validatingData: T, up: Boolean, valid: Boolean) {
        val dataList = mutableListOf(getItem1(), getItem2())
        val repositoryData = getRepositoryData(validatingData)
        if (up && valid || !up && !valid) {
            dataList.add(repositoryData)
        } else {
            dataList.add(0, repositoryData)
        }

        whenever(service.getAll()).thenReturn(dataList)
        whenever(service.get(any())).thenReturn(repositoryData)
    }

    /**
     * Verifies mock for moving.
     *
     * @param validatingData validating data
     */
    protected open fun verifyMovingMock(validatingData: T) {
        verify(service).getAll()
        verify(service).get(validatingData.id!!)
        verifyNoMoreInteractions(service)
    }

    /**
     * Returns instance of [MovableValidator].
     *
     * @return instance of [MovableValidator]
     */
    protected abstract fun getValidator(): MovableValidator<T>

    /**
     * Returns instance of [T].
     *
     * @param id ID
     * @return instance of [T]
     */
    protected abstract fun getValidatingData(id: Int?): T

    /**
     * Returns instance of [T].
     *
     * @param id       ID
     * @param position position
     * @return instance of [T]
     */
    protected abstract fun getValidatingData(id: Int?, position: Int?): T

    /**
     * Returns instance of [U].
     *
     * @param validatingData validating data
     * @return instance of [U]
     */
    protected abstract fun getRepositoryData(validatingData: T): U

    /**
     * Returns 1st item in data list.
     *
     * @return 1st item in data list
     */
    protected abstract fun getItem1(): U

    /**
     * Returns 2nd item in data list.
     *
     * @return 2nd item in data list
     */
    protected abstract fun getItem2(): U

    /**
     * Returns name of entity.
     *
     * @return name of entity
     */
    protected abstract fun getName(): String

    /**
     * Returns prefix for validation keys.
     *
     * @return prefix for validation keys
     */
    private fun getPrefix(): String {
        return getName().toUpperCase()
    }

}
