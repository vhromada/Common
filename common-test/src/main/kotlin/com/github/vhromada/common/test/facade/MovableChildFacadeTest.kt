package com.github.vhromada.common.test.facade

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.MovableChildFacade
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.TimeProvider
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.service.MovableService
import com.github.vhromada.common.test.utils.TestConstants
import com.github.vhromada.common.validator.MovableValidator
import com.github.vhromada.common.validator.ValidationType
import com.nhaarman.mockitokotlin2.KArgumentCaptor
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
 * An abstract class represents test for [MovableChildFacade].
 *
 * @param <S> type of child entity data
 * @param <T> type of child domain data
 * @param <U> type of parent entity data
 * @param <V> type of parent domain data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
@Suppress("FunctionName")
abstract class MovableChildFacadeTest<S : Movable, T : AuditEntity, U : Movable, V : AuditEntity> {

    /**
     * Instance of [MovableService]
     */
    @Mock
    protected lateinit var service: MovableService<V>

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
    protected lateinit var mapper: Mapper<S, T>

    /**
     * Instance of [MovableValidator]
     */
    @Mock
    protected lateinit var parentMovableValidator: MovableValidator<U>

    /**
     * Instance of [MovableValidator]
     */
    @Mock
    protected lateinit var childMovableValidator: MovableValidator<S>

    /**
     * Instance of [MovableChildFacade]
     */
    private lateinit var facade: MovableChildFacade<S, U>

    /**
     * Initializes facade for movable data.
     */
    @BeforeEach
    open fun setUp() {
        facade = getFacade()
    }

    /**
     * Test method for [MovableChildFacade.get] with existing data.
     */
    @Test
    fun getExistingData() {
        val childEntity = newChildEntity(1)

        whenever(service.getAll()).thenReturn(listOf(newParentDomain(1)))
        whenever(mapper.mapBack(anyChildDomain())).thenReturn(childEntity)

        val result = facade.get(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(childEntity)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(mapper).mapBack(newChildDomain(1))
        verifyNoMoreInteractions(service, mapper)
        verifyZeroInteractions(accountProvider, timeProvider, parentMovableValidator, childMovableValidator)
    }

    /**
     * Test method for [MovableChildFacade.get] with not existing data.
     */
    @Test
    fun getNotExistingData() {
        whenever(service.getAll()).thenReturn(listOf(newParentDomain(1)))

        val result = facade.get(Integer.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verifyNoMoreInteractions(service, mapper)
        verifyZeroInteractions(accountProvider, timeProvider, mapper, parentMovableValidator, childMovableValidator)
    }

    /**
     * Test method for [MovableChildFacade.add].
     */
    @Test
    fun add() {
        val parentEntity = newParentEntity(1)
        val childEntity = newChildEntity(null)
        val childDomain = newChildDomain(null)
        val argumentCaptor = argumentCaptorParentDomain()

        if (isFirstChild()) {
            whenever(service.get(any())).thenReturn(newParentDomain(1))
        } else {
            whenever(service.getAll()).thenReturn(listOf(newParentDomain(1)))
        }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(mapper.map(anyChildEntity())).thenReturn(childDomain)
        whenever(parentMovableValidator.validate(anyParentEntity(), any())).thenReturn(Result())
        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(Result())

        val result = facade.add(parentEntity, childEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        if (isFirstChild()) {
            verify(service).get(parentEntity.id!!)
        } else {
            verify(service).getAll()
        }
        verify(service).update(argumentCaptor.capture())
        verify(accountProvider).getAccount()
        verify(timeProvider).getTime()
        verify(parentMovableValidator).validate(parentEntity, ValidationType.EXISTS)
        verify(childMovableValidator).validate(childEntity, ValidationType.NEW, ValidationType.DEEP)
        verify(mapper).map(childEntity)
        verifyNoMoreInteractions(service, accountProvider, timeProvider, mapper, parentMovableValidator, childMovableValidator)

        assertParentDeepEquals(newParentDomainWithChildren(1, listOf(newChildDomain(1), childDomain)), argumentCaptor.lastValue)
    }

    /**
     * Test method for [MovableChildFacade.add] with invalid data.
     */
    @Test
    fun addInvalidData() {
        val parentEntity = newParentEntity(Integer.MAX_VALUE)
        val childEntity = newChildEntity(null)
        val invalidParentResult = Result.error<Unit>("PARENT_INVALID", "Parent must be valid.")
        val invalidChildResult = Result.error<Unit>("CHILD_INVALID", "Child must be valid.")

        whenever(parentMovableValidator.validate(anyParentEntity(), any())).thenReturn(invalidParentResult)
        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(invalidChildResult)

        val result = facade.add(parentEntity, childEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(invalidParentResult.events()[0], invalidChildResult.events()[0]))
        }

        verify(parentMovableValidator).validate(parentEntity, ValidationType.EXISTS)
        verify(childMovableValidator).validate(childEntity, ValidationType.NEW, ValidationType.DEEP)
        verifyNoMoreInteractions(parentMovableValidator, childMovableValidator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper)
    }

    /**
     * Test method for [MovableChildFacade.update].
     */
    @Test
    fun update() {
        val childEntity = newChildEntity(1)
        val childDomain = newChildDomain(1)
        val parentDomain = newParentDomain(1)
        val argumentCaptor = argumentCaptorParentDomain()

        whenever(service.getAll()).thenReturn(listOf(parentDomain))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(mapper.map(anyChildEntity())).thenReturn(childDomain)
        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(Result())

        val result = facade.update(childEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(service).update(argumentCaptor.capture())
        verify(accountProvider).getAccount()
        verify(timeProvider).getTime()
        verify(mapper).map(childEntity)
        verify(childMovableValidator).validate(childEntity, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP)
        verifyNoMoreInteractions(service, accountProvider, timeProvider, mapper, childMovableValidator)
        verifyZeroInteractions(parentMovableValidator)

        assertParentDeepEquals(parentDomain, argumentCaptor.lastValue)
    }

    /**
     * Test method for [MovableChildFacade.update] with invalid data.
     */
    @Test
    fun updateInvalidData() {
        val childEntity = newChildEntity(Integer.MAX_VALUE)

        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.update(childEntity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(childMovableValidator).validate(childEntity, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP)
        verifyNoMoreInteractions(childMovableValidator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper, parentMovableValidator)
    }

    /**
     * Test method for [MovableChildFacade.remove].
     */
    @Test
    fun remove() {
        val childEntity = newChildEntity(1)
        val parentDomain = newParentDomain(1)
        val argumentCaptor = argumentCaptorParentDomain()

        whenever(service.getAll()).thenReturn(listOf(parentDomain))
        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(Result())

        val result = facade.remove(childEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(service).update(argumentCaptor.capture())
        verify(childMovableValidator).validate(childEntity, ValidationType.EXISTS)
        verifyNoMoreInteractions(service, childMovableValidator)
        verifyZeroInteractions(accountProvider, timeProvider, mapper, parentMovableValidator)

        assertParentDeepEquals(getParentRemovedData(parentDomain, newChildDomain(1)), argumentCaptor.lastValue)
    }

    /**
     * Test method for [MovableChildFacade.remove] with invalid data.
     */
    @Test
    fun removeInvalidData() {
        val childEntity = newChildEntity(Integer.MAX_VALUE)

        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.remove(childEntity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(childMovableValidator).validate(childEntity, ValidationType.EXISTS)
        verifyNoMoreInteractions(childMovableValidator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper, parentMovableValidator)
    }

    /**
     * Test method for [MovableChildFacade.duplicate].
     */
    @Test
    fun duplicate() {
        val childEntity = newChildEntity(1)
        val childDomain = newChildDomain(null)
        childDomain.position = 0
        val argumentCaptor = argumentCaptorParentDomain()

        whenever(service.getAll()).thenReturn(listOf(newParentDomain(1)))
        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(Result())

        val result = facade.duplicate(childEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(service).update(argumentCaptor.capture())
        verify(childMovableValidator).validate(childEntity, ValidationType.EXISTS)
        verifyNoMoreInteractions(service, childMovableValidator)
        verifyZeroInteractions(accountProvider, timeProvider, mapper, parentMovableValidator)

        assertParentDeepEquals(newParentDomainWithChildren(1, listOf(newChildDomain(1), childDomain)), argumentCaptor.lastValue)
    }

    /**
     * Test method for [MovableChildFacade.duplicate] with invalid data.
     */
    @Test
    fun duplicateInvalidData() {
        val childEntity = newChildEntity(Integer.MAX_VALUE)

        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.duplicate(childEntity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(childMovableValidator).validate(childEntity, ValidationType.EXISTS)
        verifyNoMoreInteractions(childMovableValidator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper, parentMovableValidator)
    }

    /**
     * Test method for [MovableChildFacade.moveUp].
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveUp() {
        val childEntity = newChildEntity(2)
        val childDomain1 = newChildDomain(1)
        childDomain1.position = 1
        val childDomain2 = newChildDomain(2)
        childDomain2.position = 0
        val argumentCaptor = argumentCaptorParentDomain()

        whenever(service.getAll()).thenReturn(listOf(newParentDomainWithChildren(1, listOf(newChildDomain(1), newChildDomain(2)))))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(Result())

        val result = facade.moveUp(childEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(service).update(argumentCaptor.capture())
        verify(accountProvider, atLeastOnce()).getAccount()
        verify(timeProvider, atLeastOnce()).getTime()
        verify(childMovableValidator).validate(childEntity, ValidationType.EXISTS, ValidationType.UP)
        verifyNoMoreInteractions(service, accountProvider, timeProvider, childMovableValidator)
        verifyZeroInteractions(mapper, parentMovableValidator)

        assertParentDeepEquals(newParentDomainWithChildren(1, listOf(childDomain1, childDomain2)), argumentCaptor.lastValue)
    }

    /**
     * Test method for [MovableChildFacade.moveUp] with invalid data.
     */
    @Test
    fun moveUpInvalidData() {
        val childEntity = newChildEntity(Integer.MAX_VALUE)

        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.moveUp(childEntity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(childMovableValidator).validate(childEntity, ValidationType.EXISTS, ValidationType.UP)
        verifyNoMoreInteractions(childMovableValidator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper, parentMovableValidator)
    }

    /**
     * Test method for [MovableChildFacade.moveDown].
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveDown() {
        val childEntity = newChildEntity(1)
        val childDomain1 = newChildDomain(1)
        childDomain1.position = 1
        val childDomain2 = newChildDomain(2)
        childDomain2.position = 0
        val argumentCaptor = argumentCaptorParentDomain()

        whenever(service.getAll()).thenReturn(listOf(newParentDomainWithChildren(1, listOf(newChildDomain(1), newChildDomain(2)))))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(Result())

        val result = facade.moveDown(childEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(service).update(argumentCaptor.capture())
        verify(accountProvider, atLeastOnce()).getAccount()
        verify(timeProvider, atLeastOnce()).getTime()
        verify(childMovableValidator).validate(childEntity, ValidationType.EXISTS, ValidationType.DOWN)
        verifyNoMoreInteractions(service, accountProvider, timeProvider, childMovableValidator)
        verifyZeroInteractions(mapper, parentMovableValidator)

        assertParentDeepEquals(newParentDomainWithChildren(1, listOf(childDomain1, childDomain2)), argumentCaptor.lastValue)
    }

    /**
     * Test method for [MovableChildFacade.moveDown] with invalid data.
     */
    @Test
    fun moveDownInvalidData() {
        val childEntity = newChildEntity(Integer.MAX_VALUE)

        whenever(childMovableValidator.validate(anyChildEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.moveDown(childEntity)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(childMovableValidator).validate(childEntity, ValidationType.EXISTS, ValidationType.DOWN)
        verifyNoMoreInteractions(childMovableValidator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper, parentMovableValidator)
    }

    /**
     * Test method for [MovableChildFacade.find].
     */
    @Test
    fun find() {
        val parentEntity = newParentEntity(1)
        val expectedData = listOf(newChildEntity(1))

        if (isFirstChild()) {
            whenever(service.get(any())).thenReturn(newParentDomain(1))
        } else {
            whenever(service.getAll()).thenReturn(listOf(newParentDomain(1)))
        }
        whenever(mapper.mapBack(any<List<T>>())).thenReturn(expectedData)
        whenever(parentMovableValidator.validate(anyParentEntity(), any())).thenReturn(Result())

        val result = facade.find(parentEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        if (isFirstChild()) {
            verify(service).get(parentEntity.id!!)
        } else {
            verify(service).getAll()
        }
        verify(mapper).mapBack(listOf(newChildDomain(1)))
        verify(parentMovableValidator).validate(parentEntity, ValidationType.EXISTS)
        verifyNoMoreInteractions(service, mapper, parentMovableValidator)
        verifyZeroInteractions(accountProvider, timeProvider, childMovableValidator)
    }

    /**
     * Test method for [MovableChildFacade.find] with invalid data.
     */
    @Test
    fun findInvalidData() {
        val parentEntity = newParentEntity(1)

        whenever(parentMovableValidator.validate(anyParentEntity(), any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.find(parentEntity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(INVALID_DATA_RESULT.events())
        }

        verify(parentMovableValidator).validate(parentEntity, ValidationType.EXISTS)
        verifyNoMoreInteractions(parentMovableValidator)
        verifyZeroInteractions(service, accountProvider, timeProvider, mapper, childMovableValidator)
    }

    /**
     * Returns true if child if 1st parent child.
     *
     * @return true if child if 1st parent child
     */
    protected open fun isFirstChild(): Boolean {
        return true
    }

    /**
     * Returns facade for movable data for child data.
     *
     * @return facade for movable data for child data
     */
    protected abstract fun getFacade(): MovableChildFacade<S, U>

    /**
     * Returns parent entity.
     *
     * @param id ID
     * @return parent entity
     */
    protected abstract fun newParentEntity(id: Int): U

    /**
     * Returns parent domain.
     *
     * @param id ID
     * @return parent domain
     */
    @Suppress("SameParameterValue")
    protected abstract fun newParentDomain(id: Int): V

    /**
     * Returns parent domain with children.
     *
     * @param id       ID
     * @param children children
     * @return parent domain with children
     */
    @Suppress("SameParameterValue")
    protected abstract fun newParentDomainWithChildren(id: Int, children: List<T>): V

    /**
     * Returns child entity.
     *
     * @param id ID
     * @return child entity
     */
    protected abstract fun newChildEntity(id: Int?): S

    /**
     * Returns child domain.
     *
     * @param id ID
     * @return child domain
     */
    protected abstract fun newChildDomain(id: Int?): T

    /**
     * Returns parent removed data
     *
     * @param parent parent
     * @param child child
     * @return parent removed data
     */
    protected abstract fun getParentRemovedData(parent: V, child: T): V

    /**
     * Returns any mock for parent entity.
     *
     * @return any mock for parent entity
     */
    protected abstract fun anyParentEntity(): U

    /**
     * Returns any mock for child entity.
     *
     * @return any mock for child entity
     */
    protected abstract fun anyChildEntity(): S

    /**
     * Returns any mock for child domain.
     *
     * @return any mock for child domain
     */
    protected abstract fun anyChildDomain(): T

    /**
     * Returns argument captor for parent domain.
     *
     * @return argument captor for parent domain
     */
    protected abstract fun argumentCaptorParentDomain(): KArgumentCaptor<V>

    /**
     * Assert parent deep equals.
     *
     * @param expected expected
     * @param actual   actual
     */
    protected abstract fun assertParentDeepEquals(expected: V, actual: V)

}
