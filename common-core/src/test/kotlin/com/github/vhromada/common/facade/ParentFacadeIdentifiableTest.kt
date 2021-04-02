package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.service.ParentService
import com.github.vhromada.common.stub.IdentifiableStub
import com.github.vhromada.common.stub.ParentFacadeIdentifiableStub
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
 * A class represents test for class [ParentFacade] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ParentFacadeIdentifiableTest {

    /**
     * Instance of [ParentService]
     */
    @Mock
    private lateinit var service: ParentService<Identifiable>

    /**
     * Instance of [Mapper]
     */
    @Mock
    private lateinit var mapper: Mapper<Identifiable, Identifiable>

    /**
     * Instance of [Validator]
     */
    @Mock
    private lateinit var validator: Validator<Identifiable, Identifiable>

    /**
     * Instance of [ParentFacade]
     */
    private lateinit var facade: ParentFacade<Identifiable>

    /**
     * Initializes facade.
     */
    @BeforeEach
    fun setUp() {
        facade = ParentFacadeIdentifiableStub(service = service, mapper = mapper, validator = validator)
    }

    /**
     * Test method for [ParentFacade.get] with existing data.
     */
    @Test
    fun getExistingData() {
        val entity = IdentifiableStub(1)
        val domain = IdentifiableStub(1)

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(any())).thenReturn(Result())
        whenever(mapper.mapBack(any<Identifiable>())).thenReturn(entity)

        val result = facade.get(entity.id!!)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entity)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(entity.id!!)
        verify(validator).validateExists(Optional.of(domain))
        verify(mapper).mapBack(domain)
        verifyNoMoreInteractions(service, mapper, validator)
    }

    /**
     * Test method for [ParentFacade.get] with not existing data.
     */
    @Test
    fun getNotExistingData() {
        whenever(service.get(any())).thenReturn(Optional.empty())
        whenever(validator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.get(Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(Int.MAX_VALUE)
        verify(validator).validateExists(Optional.empty())
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.update].
     */
    @Test
    fun update() {
        val entity = IdentifiableStub(1)
        val domain = IdentifiableStub(1)

        whenever(validator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(mapper.map(any<Identifiable>())).thenReturn(domain)

        val result = facade.update(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).update(domain)
        verify(mapper).map(entity)
        verify(validator).validate(data = entity, update = true)
        verifyNoMoreInteractions(service, mapper, validator)
    }

    /**
     * Test method for [ParentFacade.update] with invalid data.
     */
    @Test
    fun updateInvalidData() {
        val entity = IdentifiableStub(Int.MAX_VALUE)

        whenever(validator.validate(data = any(), update = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.update(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(validator).validate(data = entity, update = true)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, mapper)
    }

    /**
     * Test method for [ParentFacade.remove].
     */
    @Test
    fun remove() {
        val domain = IdentifiableStub(1)

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(any())).thenReturn(Result())

        facade.remove(1)

        verify(service).get(1)
        verify(service).remove(domain)
        verify(validator).validateExists(Optional.of(domain))
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.remove] with invalid data.
     */
    @Test
    fun removeInvalidData() {
        whenever(service.get(any())).thenReturn(Optional.empty())
        whenever(validator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.remove(Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(Int.MAX_VALUE)
        verify(validator).validateExists(Optional.empty())
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.duplicate].
     */
    @Test
    fun duplicate() {
        val domain = IdentifiableStub(1)

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(any())).thenReturn(Result())

        facade.duplicate(1)

        verify(service).get(1)
        verify(service).duplicate(domain)
        verify(validator).validateExists(Optional.of(domain))
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.duplicate] with invalid data.
     */
    @Test
    fun duplicateInvalidData() {
        whenever(service.get(any())).thenReturn(Optional.empty())
        whenever(validator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.duplicate(Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(Int.MAX_VALUE)
        verify(validator).validateExists(Optional.empty())
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveUp].
     */
    @Test
    fun moveUp() {
        val domain = IdentifiableStub(1)
        val dataList = listOf(domain, IdentifiableStub(2))

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(service.getAll()).thenReturn(dataList)
        whenever(validator.validateExists(any())).thenReturn(Result())
        whenever(validator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(Result())

        val result = facade.moveUp(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(1)
        verify(service).getAll()
        verify(service).moveUp(domain)
        verify(validator).validateExists(Optional.of(domain))
        verify(validator).validateMovingData(data = domain, list = dataList, up = true)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveUp] not existing data.
     */
    @Test
    fun moveUpNotExisting() {
        val domain = IdentifiableStub(1)

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveUp(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(1)
        verify(validator).validateExists(Optional.of(domain))
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveUp] not movable.
     */
    @Test
    fun moveUpNotIdentifiable() {
        val domain = IdentifiableStub(1)
        val dataList = listOf(domain, IdentifiableStub(2))

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(service.getAll()).thenReturn(dataList)
        whenever(validator.validateExists(any())).thenReturn(Result())
        whenever(validator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveUp(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(1)
        verify(service).getAll()
        verify(validator).validateExists(Optional.of(domain))
        verify(validator).validateMovingData(data = domain, list = dataList, up = true)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveDown].
     */
    @Test
    fun moveDown() {
        val domain = IdentifiableStub(1)
        val dataList = listOf(domain, IdentifiableStub(2))

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(service.getAll()).thenReturn(dataList)
        whenever(validator.validateExists(any())).thenReturn(Result())
        whenever(validator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(Result())

        val result = facade.moveDown(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(1)
        verify(service).getAll()
        verify(service).moveDown(domain)
        verify(validator).validateExists(Optional.of(domain))
        verify(validator).validateMovingData(data = domain, list = dataList, up = false)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveDown] not existing data.
     */
    @Test
    fun moveDownNotExisting() {
        val domain = IdentifiableStub(1)

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveDown(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(1)
        verify(validator).validateExists(Optional.of(domain))
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveDown] not movable.
     */
    @Test
    fun moveDownNotIdentifiable() {
        val domain = IdentifiableStub(1)
        val dataList = listOf(domain, IdentifiableStub(2))

        whenever(service.get(any())).thenReturn(Optional.of(domain))
        whenever(service.getAll()).thenReturn(dataList)
        whenever(validator.validateExists(any())).thenReturn(Result())
        whenever(validator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveDown(1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(1)
        verify(service).getAll()
        verify(validator).validateExists(Optional.of(domain))
        verify(validator).validateMovingData(data = domain, list = dataList, up = false)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.newData].
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
        verifyZeroInteractions(mapper, validator)
    }

    /**
     * Test method for [ParentFacade.getAll].
     */
    @Test
    fun getAll() {
        val entityList = listOf(IdentifiableStub(1), IdentifiableStub(2))
        val domainList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        whenever(service.getAll()).thenReturn(domainList)
        whenever(mapper.mapBack(any<List<Identifiable>>())).thenReturn(entityList)

        val result = facade.getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entityList)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(mapper).mapBack(domainList)
        verifyNoMoreInteractions(service, mapper)
        verifyZeroInteractions(validator)
    }

    /**
     * Test method for [ParentFacade.add].
     */
    @Test
    fun add() {
        val entity = IdentifiableStub(1)
        val domain = IdentifiableStub(1)

        whenever(validator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(mapper.map(any<Identifiable>())).thenReturn(domain)

        val result = facade.add(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).add(domain)
        verify(mapper).map(entity)
        verify(validator).validate(data = entity, update = false)
        verifyNoMoreInteractions(service, mapper, validator)
    }

    /**
     * Test method for [ParentFacade.add] with invalid data.
     */
    @Test
    fun addInvalidData() {
        val entity = IdentifiableStub(Int.MAX_VALUE)

        whenever(validator.validate(data = any(), update = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.add(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(validator).validate(data = entity, update = false)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, mapper)
    }

    /**
     * Test method for [ParentFacade.updatePositions].
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
        verifyZeroInteractions(mapper, validator)
    }

}
