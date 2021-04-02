package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.service.ChildService
import com.github.vhromada.common.service.ParentService
import com.github.vhromada.common.stub.ChildFacadeIdentifiableStub
import com.github.vhromada.common.stub.IdentifiableStub
import com.github.vhromada.common.utils.TestConstants
import com.github.vhromada.common.validator.Validator
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

/**
 * A class represents test for class [ChildFacade] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ChildFacadeIdentifiableTest {

    /**
     * Instance of [ChildService]
     */
    @Mock
    private lateinit var childService: ChildService<Identifiable>

    /**
     * Instance of [ParentService]
     */
    @Mock
    private lateinit var parentService: ParentService<Identifiable>

    /**
     * Instance of [Mapper]
     */
    @Mock
    private lateinit var mapper: Mapper<Identifiable, Identifiable>

    /**
     * Instance of [Validator] for child data
     */
    @Mock
    private lateinit var childValidator: Validator<Identifiable, Identifiable>

    /**
     * Instance of [Validator] for parent data
     */
    @Mock
    private lateinit var parentValidator: Validator<Identifiable, Identifiable>

    /**
     * Instance of [ChildFacade]
     */
    private lateinit var facade: ChildFacade<Identifiable, Identifiable>

    /**
     * Parent
     */
    private lateinit var parent: Identifiable

    /**
     * Initializes facade.
     */
    @BeforeEach
    fun setUp() {
        parent = IdentifiableStub(20)
        facade = ChildFacadeIdentifiableStub(
            childService = childService,
            parentService = parentService,
            mapper = mapper,
            childValidator = childValidator,
            parentValidator = parentValidator,
            parent = { parent }
        )
    }

    /**
     * Test method for [ChildFacade.get] with existing data.
     */
    @Test
    fun getExistingData() {
        val entity = IdentifiableStub(1)
        val domain = IdentifiableStub(1)

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(any())).thenReturn(Result())
        whenever(mapper.mapBack(any<Identifiable>())).thenReturn(entity)

        val result = facade.get(entity.id!!)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entity)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).get(entity.id!!)
        verify(childValidator).validateExists(Optional.of(domain))
        verify(mapper).mapBack(domain)
        verifyNoMoreInteractions(childService, mapper, childValidator)
        verifyZeroInteractions(parentService, parentValidator)
    }

    /**
     * Test method for [ChildFacade.get] with not existing data.
     */
    @Test
    fun getNotExistingData() {
        whenever(childService.get(any())).thenReturn(Optional.empty())
        whenever(childValidator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.get(Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(Int.MAX_VALUE)
        verify(childValidator).validateExists(Optional.empty())
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.update].
     */
    @Test
    fun update() {
        val entity = IdentifiableStub(1)
        val domain = IdentifiableStub(1)

        whenever(childValidator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(mapper.map(any<Identifiable>())).thenReturn(domain)

        val result = facade.update(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).update(domain)
        verify(mapper).map(entity)
        verify(childValidator).validate(data = entity, update = true)
        verifyNoMoreInteractions(childService, mapper, childValidator)
        verifyZeroInteractions(parentService, parentValidator)
    }

    /**
     * Test method for [ChildFacade.update] with invalid data.
     */
    @Test
    fun updateInvalidData() {
        val entity = IdentifiableStub(Int.MAX_VALUE)

        whenever(childValidator.validate(data = any(), update = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.update(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childValidator).validate(data = entity, update = true)
        verifyNoMoreInteractions(childValidator)
        verifyZeroInteractions(childService, parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.remove].
     */
    @Test
    fun remove() {
        val domain = IdentifiableStub(1)

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(any())).thenReturn(Result())

        facade.remove(1)

        verify(childService).get(1)
        verify(childService).remove(domain)
        verify(childValidator).validateExists(Optional.of(domain))
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.remove] with invalid data.
     */
    @Test
    fun removeInvalidData() {
        whenever(childService.get(any())).thenReturn(Optional.empty())
        whenever(childValidator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.remove(Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(Int.MAX_VALUE)
        verify(childValidator).validateExists(Optional.empty())
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.duplicate].
     */
    @Test
    fun duplicate() {
        val domain = IdentifiableStub(1)

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(any())).thenReturn(Result())

        facade.duplicate(1)

        verify(childService).get(1)
        verify(childService).duplicate(domain)
        verify(childValidator).validateExists(Optional.of(domain))
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.duplicate] with invalid data.
     */
    @Test
    fun duplicateInvalidData() {
        whenever(childService.get(any())).thenReturn(Optional.empty())
        whenever(childValidator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.duplicate(Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(Int.MAX_VALUE)
        verify(childValidator).validateExists(Optional.empty())
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveUp].
     */
    @Test
    fun moveUp() {
        val domain = IdentifiableStub(1)
        val dataList = listOf(domain, IdentifiableStub(2))

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childService.find(any())).thenReturn(dataList)
        whenever(childValidator.validateExists(any())).thenReturn(Result())
        whenever(childValidator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(Result())

        val result = facade.moveUp(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).get(1)
        verify(childService).find(parent.id!!)
        verify(childService).moveUp(domain)
        verify(childValidator).validateExists(Optional.of(domain))
        verify(childValidator).validateMovingData(data = domain, list = dataList, up = true)
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveUp] not existing data.
     */
    @Test
    fun moveUpNotExisting() {
        val domain = IdentifiableStub(1)

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveUp(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(1)
        verify(childValidator).validateExists(Optional.of(domain))
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveUp] not movable.
     */
    @Test
    fun moveUpNotIdentifiable() {
        val domain = IdentifiableStub(1)
        val dataList = listOf(domain, IdentifiableStub(2))

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childService.find(any())).thenReturn(dataList)
        whenever(childValidator.validateExists(any())).thenReturn(Result())
        whenever(childValidator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveUp(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(1)
        verify(childService).find(parent.id!!)
        verify(childValidator).validateExists(Optional.of(domain))
        verify(childValidator).validateMovingData(data = domain, list = dataList, up = true)
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveDown].
     */
    @Test
    fun moveDown() {
        val domain = IdentifiableStub(1)
        val dataList = listOf(domain, IdentifiableStub(2))

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childService.find(any())).thenReturn(dataList)
        whenever(childValidator.validateExists(any())).thenReturn(Result())
        whenever(childValidator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(Result())

        val result = facade.moveDown(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).get(1)
        verify(childService).find(parent.id!!)
        verify(childService).moveDown(domain)
        verify(childValidator).validateExists(Optional.of(domain))
        verify(childValidator).validateMovingData(data = domain, list = dataList, up = false)
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveDown] not existing data.
     */
    @Test
    fun moveDownNotExisting() {
        val domain = IdentifiableStub(1)

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveDown(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(1)
        verify(childValidator).validateExists(Optional.of(domain))
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveDown] not movable.
     */
    @Test
    fun moveDownNotIdentifiable() {
        val domain = IdentifiableStub(1)
        val dataList = listOf(domain, IdentifiableStub(2))

        whenever(childService.get(any())).thenReturn(Optional.of(domain))
        whenever(childService.find(any())).thenReturn(dataList)
        whenever(childValidator.validateExists(any())).thenReturn(Result())
        whenever(childValidator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveDown(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(1)
        verify(childService).find(parent.id!!)
        verify(childValidator).validateExists(Optional.of(domain))
        verify(childValidator).validateMovingData(data = domain, list = dataList, up = false)
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.add].
     */
    @Test
    fun add() {
        val entity = IdentifiableStub(1)
        val domain = IdentifiableStub(1)

        whenever(parentService.get(any())).thenReturn(Optional.of(parent))
        whenever(mapper.map(any<Identifiable>())).thenReturn(domain)
        whenever(childValidator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(parentValidator.validateExists(any())).thenReturn(Result())

        val result = facade.add(parent = parent.id!!, data = entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).add(domain)
        verify(parentService).get(parent.id!!)
        verify(mapper).map(entity)
        verify(childValidator).validate(data = entity, update = false)
        verify(parentValidator).validateExists(Optional.of(parent))
        verifyNoMoreInteractions(childService, parentService, mapper, childValidator, parentValidator)
    }

    /**
     * Test method for [ChildFacade.add] with invalid parent.
     */
    @Test
    fun addInvalidParent() {
        val entity = IdentifiableStub(1)

        whenever(parentService.get(any())).thenReturn(Optional.empty())
        whenever(childValidator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(parentValidator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.add(parent = parent.id!!, data = entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(parentService).get(parent.id!!)
        verify(childValidator).validate(data = entity, update = false)
        verify(parentValidator).validateExists(Optional.empty())
        verifyNoMoreInteractions(parentService, childValidator, parentValidator)
        verifyZeroInteractions(childService, mapper)
    }

    /**
     * Test method for [ChildFacade.add] with invalid data.
     */
    @Test
    fun addInvalidData() {
        val entity = IdentifiableStub(1)

        whenever(parentService.get(any())).thenReturn(Optional.of(parent))
        whenever(childValidator.validate(data = any(), update = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)
        whenever(parentValidator.validateExists(any())).thenReturn(Result())

        val result = facade.add(parent = parent.id!!, data = entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(parentService).get(parent.id!!)
        verify(childValidator).validate(data = entity, update = false)
        verify(parentValidator).validateExists(Optional.of(parent))
        verifyNoMoreInteractions(parentService, childValidator, parentValidator)
        verifyZeroInteractions(childService, mapper)
    }

    /**
     * Test method for [ChildFacade.find].
     */
    @Test
    fun find() {
        val domainList = listOf(IdentifiableStub(1), IdentifiableStub(2))
        val entityList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        whenever(childService.find(any())).thenReturn(domainList)
        whenever(parentService.get(any())).thenReturn(Optional.of(parent))
        whenever(mapper.mapBack(any<List<Identifiable>>())).thenReturn(entityList)
        whenever(parentValidator.validateExists(any())).thenReturn(Result())

        val result = facade.find(parent.id!!)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entityList)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).find(parent.id!!)
        verify(parentService).get(parent.id!!)
        verify(mapper).mapBack(domainList)
        verify(parentValidator).validateExists(Optional.of(parent))
        verifyNoMoreInteractions(childService, parentService, mapper, parentValidator)
        verifyZeroInteractions(childValidator)
    }

    /**
     * Test method for [ChildFacade.find] with invalid data.
     */
    @Test
    fun findInvalidData() {
        whenever(parentService.get(any())).thenReturn(Optional.empty())
        whenever(parentValidator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.find(parent.id!!)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(parentService).get(parent.id!!)
        verify(parentValidator).validateExists(Optional.empty())
        verifyNoMoreInteractions(parentService, parentValidator)
        verifyZeroInteractions(childService, mapper, childValidator)
    }

}
