package com.github.vhromada.common.test.facade

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.MovableParentFacade
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.TimeProvider
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.service.MovableService
import com.github.vhromada.common.test.utils.TestConstants
import com.github.vhromada.common.validator.MovableValidator
import com.github.vhromada.common.validator.ValidationType
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

/**
 * Result for invalid data
 */
private val INVALID_DATA_RESULT = Result.error<Unit>("DATA_INVALID", "Data must be valid.")

/**
 * An abstract class represents test for [MovableParentFacade].
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
@Suppress("FunctionName")
abstract class MovableParentFacadeTest<T : Movable, U : AuditEntity> {

    /**
     * Instance of [MovableService]
     */
    @Mock
    protected lateinit var service: MovableService<U>

    /**
     * Instance of [AccountProvider]
     */
    @Mock
    protected lateinit var accountProvider: AccountProvider

    /**
     * Instance of [TimeProvider]
     */
    @Mock
    protected lateinit var timeProvider: TimeProvider

    /**
     * Instance of [Mapper]
     */
    @Mock
    protected lateinit var mapper: Mapper<T, U>

    /**
     * Instance of [MovableValidator]
     */
    @Mock
    protected lateinit var validator: MovableValidator<T>

    /**
     * Instance of [MovableParentFacade]
     */
    private lateinit var facade: MovableParentFacade<T>

    /**
     * Initializes facade for movable data.
     */
    @BeforeEach
    open fun setUp() {
        facade = getFacade()
    }

    /**
     * Test method for [MovableParentFacade.newData].
     */
    @Test
    fun newData() {
        val result = facade.newData()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).newData()
        verifyNoMoreInteractions(service)
        verifyZeroInteractions(accountProvider, timeProvider, mapper, validator)
    }

    /**
     * Test method for [MovableParentFacade.getAll].
     */
    @Test
    fun getAll() {
        val domainList = listOf(newDomain(1), newDomain(2))
        val entityList = listOf(newEntity(1), newEntity(2))

        whenever(service.getAll()).thenReturn(domainList)
        whenever(mapper.mapBack(any<List<U>>())).thenReturn(entityList)

        val result = facade.getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entityList)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(mapper).mapBack(domainList)
        verifyNoMoreInteractions(service, mapper)
        verifyZeroInteractions(accountProvider, timeProvider, validator)
    }


    /**
     * Test method for [MovableParentFacade.get] with existing data.
     */
    @Test
    fun getExistingData() {
        val domain = newDomain(1)
        val entity = newEntity(1)

        whenever(service.get(any())).thenReturn(domain)
        whenever(mapper.mapBack(anyDomain())).thenReturn(entity)

        val result = facade.get(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entity)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(1)
        verify(mapper).mapBack(domain)
        verifyNoMoreInteractions(service, mapper)
        verifyZeroInteractions(accountProvider, timeProvider, validator)
    }

    /**
     * Test method for [MovableParentFacade.get] with not existing data.
     */
    @Test
    fun getNotExistingData() {
        whenever(service.get(any())).thenReturn(null)

        val result = facade.get(Integer.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(Integer.MAX_VALUE)
        verifyNoMoreInteractions(service, mapper)
        verifyZeroInteractions(accountProvider, timeProvider, mapper, validator)
    }

    /**
     * Test method for [MovableParentFacade.add].
     */
    @Test
    fun add() {
        val entity = newEntity(null)
        val domain = newDomain(null)

        whenever(mapper.map(anyEntity())).thenReturn(domain)
        whenever(validator.validate(anyEntity(), any())).thenReturn(Result())
        initAddProviders()

        val result = facade.add(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).add(domain)
        verify(mapper).map(entity)
        verify(validator).validate(entity, ValidationType.NEW, ValidationType.DEEP)
        verifyNoMoreInteractions(service, mapper, validator)
        verifyAddProviders()
    }

    /**
     * Test method for [MovableParentFacade.add] with invalid data.
     */
    @Test
    fun addInvalidData() {
        val entity = newEntity(Integer.MAX_VALUE)

        whenever(validator.validate(anyEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.add(entity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(validator).validate(entity, ValidationType.NEW, ValidationType.DEEP)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(accountProvider, timeProvider, service, mapper)
    }

    /**
     * Test method for [MovableParentFacade.update].
     */
    @Test
    fun update() {
        val entity = newEntity(1)
        val domain = newDomain(1)

        initUpdateMock(domain)

        val result = facade.update(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service, atLeastOnce()).get(domain.id!!)
        verify(service).update(domain)
        verify(accountProvider, atLeastOnce()).getAccount()
        verify(timeProvider, atLeastOnce()).getTime()
        verify(mapper).map(entity)
        verify(validator).validate(entity, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP)
        verifyNoMoreInteractions(service, accountProvider, timeProvider, mapper, validator)
    }

    /**
     * Test method for [MovableParentFacade.update] with invalid data.
     */
    @Test
    fun updateInvalidData() {
        val entity = newEntity(Integer.MAX_VALUE)

        whenever(validator.validate(anyEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.update(entity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(validator).validate(entity, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.remove].
     */
    @Test
    fun remove() {
        val entity = newEntity(1)
        val domain = newDomain(1)

        whenever(service.get(any())).thenReturn(domain)
        whenever(validator.validate(anyEntity(), any())).thenReturn(Result())

        val result = facade.remove(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(1)
        verify(service).remove(domain)
        verify(validator).validate(entity, ValidationType.EXISTS)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.remove] with invalid data.
     */
    @Test
    fun removeInvalidData() {
        val entity = newEntity(Integer.MAX_VALUE)

        whenever(validator.validate(anyEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.remove(entity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(validator).validate(entity, ValidationType.EXISTS)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.duplicate].
     */
    @Test
    fun duplicate() {
        val entity = newEntity(1)
        val domain = newDomain(1)

        whenever(service.get(any())).thenReturn(domain)
        whenever(validator.validate(anyEntity(), any())).thenReturn(Result())

        val result = facade.duplicate(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(1)
        verify(service).duplicate(domain)
        verify(validator).validate(entity, ValidationType.EXISTS)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.duplicate] with invalid data.
     */
    @Test
    fun duplicateInvalidData() {
        val entity = newEntity(Integer.MAX_VALUE)

        whenever(validator.validate(anyEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.duplicate(entity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(validator).validate(entity, ValidationType.EXISTS)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.moveUp].
     */
    @Test
    fun moveUp() {
        val entity = newEntity(1)
        val domain = newDomain(1)

        whenever(service.get(any())).thenReturn(domain)
        whenever(validator.validate(anyEntity(), any())).thenReturn(Result())

        val result = facade.moveUp(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(1)
        verify(service).moveUp(domain)
        verify(validator).validate(entity, ValidationType.EXISTS, ValidationType.UP)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.moveUp] with invalid data.
     */
    @Test
    fun moveUpInvalidData() {
        val entity = newEntity(Integer.MAX_VALUE)

        whenever(validator.validate(anyEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.moveUp(entity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(validator).validate(entity, ValidationType.EXISTS, ValidationType.UP)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.moveDown].
     */
    @Test
    fun moveDown() {
        val entity = newEntity(1)
        val domain = newDomain(1)

        whenever(service.get(any())).thenReturn(domain)
        whenever(validator.validate(anyEntity(), any())).thenReturn(Result())

        val result = facade.moveDown(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(1)
        verify(service).moveDown(domain)
        verify(validator).validate(entity, ValidationType.EXISTS, ValidationType.DOWN)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.moveDown] with invalid data.
     */
    @Test
    fun moveDownInvalidData() {
        val entity = newEntity(Integer.MAX_VALUE)

        whenever(validator.validate(anyEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.moveDown(entity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(validator).validate(entity, ValidationType.EXISTS, ValidationType.DOWN)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableParentFacade.updatePositions].
     */
    @Test
    fun updatePositions() {
        val result = facade.updatePositions()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).updatePositions()
        verifyNoMoreInteractions(service)
        verifyZeroInteractions(accountProvider, timeProvider, mapper, validator)
    }

    /**
     * Initializes providers for add.
     */
    protected open fun initAddProviders() {
        // no init
    }

    /**
     * Verifies providers for add.
     */
    protected open fun verifyAddProviders() {
        verifyZeroInteractions(accountProvider, timeProvider)
    }

    /**
     * Initializes mock for update.
     *
     * @param domain domain
     */
    protected open fun initUpdateMock(domain: U) {
        whenever(service.get(any())).thenReturn(domain)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(mapper.map(anyEntity())).thenReturn(domain)
        whenever(validator.validate(anyEntity(), any())).thenReturn(Result())
    }

    /**
     * Returns facade for movable data for parent data.
     *
     * @return facade for movable data for parent data
     */
    protected abstract fun getFacade(): MovableParentFacade<T>

    /**
     * Returns entity.
     *
     * @param id ID
     * @return entity
     */
    protected abstract fun newEntity(id: Int?): T

    /**
     * Returns domain.
     *
     * @param id ID
     * @return domain
     */
    protected abstract fun newDomain(id: Int?): U

    /**
     * Returns any mock for entity.
     *
     * @return any mock for entity
     */
    protected abstract fun anyEntity(): T

    /**
     * Returns ny mock for domain.
     *
     * @return ny mock for domain
     */
    protected abstract fun anyDomain(): U

}
