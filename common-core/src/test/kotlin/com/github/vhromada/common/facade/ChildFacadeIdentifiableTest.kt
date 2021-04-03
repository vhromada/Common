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
        parent = IdentifiableStub(id = 20)
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
        val entity = IdentifiableStub(id = 1)
        val domain = IdentifiableStub(id = 1)

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(data = any())).thenReturn(Result())
        whenever(mapper.mapBack(source = any<Identifiable>())).thenReturn(entity)

        val result = facade.get(id = entity.id!!)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entity)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).get(id = entity.id!!)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verify(mapper).mapBack(source = domain)
        verifyNoMoreInteractions(childService, mapper, childValidator)
        verifyZeroInteractions(parentService, parentValidator)
    }

    /**
     * Test method for [ChildFacade.get] with not existing data.
     */
    @Test
    fun getNotExistingData() {
        whenever(childService.get(id = any())).thenReturn(Optional.empty())
        whenever(childValidator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.get(id = Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(id = Int.MAX_VALUE)
        verify(childValidator).validateExists(data = Optional.empty())
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.update].
     */
    @Test
    fun update() {
        val entity = IdentifiableStub(id = 1)
        val domain = IdentifiableStub(id = 1)

        whenever(childValidator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(mapper.map(source = any<Identifiable>())).thenReturn(domain)

        val result = facade.update(data = entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).update(data = domain)
        verify(mapper).map(source = entity)
        verify(childValidator).validate(data = entity, update = true)
        verifyNoMoreInteractions(childService, mapper, childValidator)
        verifyZeroInteractions(parentService, parentValidator)
    }

    /**
     * Test method for [ChildFacade.update] with invalid data.
     */
    @Test
    fun updateInvalidData() {
        val entity = IdentifiableStub(id = Int.MAX_VALUE)

        whenever(childValidator.validate(data = any(), update = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.update(data = entity)

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
        val domain = IdentifiableStub(id = 1)

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(data = any())).thenReturn(Result())

        facade.remove(id = 1)

        verify(childService).get(id = 1)
        verify(childService).remove(data = domain)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.remove] with invalid data.
     */
    @Test
    fun removeInvalidData() {
        whenever(childService.get(id = any())).thenReturn(Optional.empty())
        whenever(childValidator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.remove(id = Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(id = Int.MAX_VALUE)
        verify(childValidator).validateExists(data = Optional.empty())
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.duplicate].
     */
    @Test
    fun duplicate() {
        val domain = IdentifiableStub(id = 1)

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(data = any())).thenReturn(Result())

        facade.duplicate(id = 1)

        verify(childService).get(id = 1)
        verify(childService).duplicate(data = domain)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.duplicate] with invalid data.
     */
    @Test
    fun duplicateInvalidData() {
        whenever(childService.get(id = any())).thenReturn(Optional.empty())
        whenever(childValidator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.duplicate(id = Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(id = Int.MAX_VALUE)
        verify(childValidator).validateExists(data = Optional.empty())
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveUp].
     */
    @Test
    fun moveUp() {
        val domain = IdentifiableStub(id = 1)
        val dataList = listOf(domain, IdentifiableStub(id = 2))

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childService.find(parent = any())).thenReturn(dataList)
        whenever(childValidator.validateExists(data = any())).thenReturn(Result())
        whenever(childValidator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(Result())

        val result = facade.moveUp(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).get(id = 1)
        verify(childService).find(parent = parent.id!!)
        verify(childService).moveUp(data = domain)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verify(childValidator).validateMovingData(data = domain, list = dataList, up = true)
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveUp] not existing data.
     */
    @Test
    fun moveUpNotExisting() {
        val domain = IdentifiableStub(id = 1)

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveUp(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(id = 1)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveUp] not movable.
     */
    @Test
    fun moveUpNotIdentifiable() {
        val domain = IdentifiableStub(id = 1)
        val dataList = listOf(domain, IdentifiableStub(id = 2))

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childService.find(parent = any())).thenReturn(dataList)
        whenever(childValidator.validateExists(data = any())).thenReturn(Result())
        whenever(childValidator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveUp(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(id = 1)
        verify(childService).find(parent = parent.id!!)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verify(childValidator).validateMovingData(data = domain, list = dataList, up = true)
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveDown].
     */
    @Test
    fun moveDown() {
        val domain = IdentifiableStub(id = 1)
        val dataList = listOf(domain, IdentifiableStub(id = 2))

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childService.find(parent = any())).thenReturn(dataList)
        whenever(childValidator.validateExists(data = any())).thenReturn(Result())
        whenever(childValidator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(Result())

        val result = facade.moveDown(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).get(id = 1)
        verify(childService).find(parent = parent.id!!)
        verify(childService).moveDown(data = domain)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verify(childValidator).validateMovingData(data = domain, list = dataList, up = false)
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveDown] not existing data.
     */
    @Test
    fun moveDownNotExisting() {
        val domain = IdentifiableStub(id = 1)

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childValidator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveDown(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(id = 1)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.moveDown] not movable.
     */
    @Test
    fun moveDownNotIdentifiable() {
        val domain = IdentifiableStub(id = 1)
        val dataList = listOf(domain, IdentifiableStub(id = 2))

        whenever(childService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(childService.find(parent = any())).thenReturn(dataList)
        whenever(childValidator.validateExists(data = any())).thenReturn(Result())
        whenever(childValidator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveDown(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(childService).get(id = 1)
        verify(childService).find(parent = parent.id!!)
        verify(childValidator).validateExists(data = Optional.of(domain))
        verify(childValidator).validateMovingData(data = domain, list = dataList, up = false)
        verifyNoMoreInteractions(childService, childValidator)
        verifyZeroInteractions(parentService, mapper, parentValidator)
    }

    /**
     * Test method for [ChildFacade.add].
     */
    @Test
    fun add() {
        val entity = IdentifiableStub(id = 1)
        val domain = IdentifiableStub(id = 1)

        whenever(parentService.get(id = any())).thenReturn(Optional.of(parent))
        whenever(mapper.map(source = any<Identifiable>())).thenReturn(domain)
        whenever(childValidator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(parentValidator.validateExists(data = any())).thenReturn(Result())

        val result = facade.add(parent = parent.id!!, data = entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).add(data = domain)
        verify(parentService).get(id = parent.id!!)
        verify(mapper).map(source = entity)
        verify(childValidator).validate(data = entity, update = false)
        verify(parentValidator).validateExists(data = Optional.of(parent))
        verifyNoMoreInteractions(childService, parentService, mapper, childValidator, parentValidator)
    }

    /**
     * Test method for [ChildFacade.add] with invalid parent.
     */
    @Test
    fun addInvalidParent() {
        val entity = IdentifiableStub(id = 1)

        whenever(parentService.get(id = any())).thenReturn(Optional.empty())
        whenever(childValidator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(parentValidator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.add(parent = parent.id!!, data = entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(parentService).get(id = parent.id!!)
        verify(childValidator).validate(data = entity, update = false)
        verify(parentValidator).validateExists(data = Optional.empty())
        verifyNoMoreInteractions(parentService, childValidator, parentValidator)
        verifyZeroInteractions(childService, mapper)
    }

    /**
     * Test method for [ChildFacade.add] with invalid data.
     */
    @Test
    fun addInvalidData() {
        val entity = IdentifiableStub(id = 1)

        whenever(parentService.get(id = any())).thenReturn(Optional.of(parent))
        whenever(childValidator.validate(data = any(), update = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)
        whenever(parentValidator.validateExists(data = any())).thenReturn(Result())

        val result = facade.add(parent = parent.id!!, data = entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(parentService).get(id = parent.id!!)
        verify(childValidator).validate(data = entity, update = false)
        verify(parentValidator).validateExists(data = Optional.of(parent))
        verifyNoMoreInteractions(parentService, childValidator, parentValidator)
        verifyZeroInteractions(childService, mapper)
    }

    /**
     * Test method for [ChildFacade.find].
     */
    @Test
    fun find() {
        val domainList = listOf(IdentifiableStub(id = 1), IdentifiableStub(id = 2))
        val entityList = listOf(IdentifiableStub(id = 1), IdentifiableStub(id = 2))

        whenever(childService.find(parent = any())).thenReturn(domainList)
        whenever(parentService.get(id = any())).thenReturn(Optional.of(parent))
        whenever(mapper.mapBack(source = any<List<Identifiable>>())).thenReturn(entityList)
        whenever(parentValidator.validateExists(data = any())).thenReturn(Result())

        val result = facade.find(parent = parent.id!!)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entityList)
            it.assertThat(result.events()).isEmpty()
        }

        verify(childService).find(parent = parent.id!!)
        verify(parentService).get(id = parent.id!!)
        verify(mapper).mapBack(source = domainList)
        verify(parentValidator).validateExists(data = Optional.of(parent))
        verifyNoMoreInteractions(childService, parentService, mapper, parentValidator)
        verifyZeroInteractions(childValidator)
    }

    /**
     * Test method for [ChildFacade.find] with invalid data.
     */
    @Test
    fun findInvalidData() {
        whenever(parentService.get(id = any())).thenReturn(Optional.empty())
        whenever(parentValidator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.find(parent = parent.id!!)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(parentService).get(id = parent.id!!)
        verify(parentValidator).validateExists(data = Optional.empty())
        verifyNoMoreInteractions(parentService, parentValidator)
        verifyZeroInteractions(childService, mapper, childValidator)
    }

}
