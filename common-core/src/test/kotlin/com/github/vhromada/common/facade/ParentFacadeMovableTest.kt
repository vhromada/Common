package com.github.vhromada.common.facade

import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.service.ParentService
import com.github.vhromada.common.stub.MovableStub
import com.github.vhromada.common.stub.ParentFacadeMovableStub
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
 * A class represents test for class [ParentFacade] for [Movable].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ParentFacadeMovableTest {

    /**
     * Instance of [ParentService]
     */
    @Mock
    private lateinit var service: ParentService<Movable>

    /**
     * Instance of [Mapper]
     */
    @Mock
    private lateinit var mapper: Mapper<Movable, Movable>

    /**
     * Instance of [Validator]
     */
    @Mock
    private lateinit var validator: Validator<Movable, Movable>

    /**
     * Instance of [ParentFacade]
     */
    private lateinit var facade: ParentFacade<Movable>

    /**
     * Initializes facade.
     */
    @BeforeEach
    fun setUp() {
        facade = ParentFacadeMovableStub(service = service, mapper = mapper, validator = validator)
    }

    /**
     * Test method for [ParentFacade.get] with existing data.
     */
    @Test
    fun getExistingData() {
        val entity = MovableStub(id = 1, position = 1)
        val domain = MovableStub(id = 1, position = 1)

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(data = any())).thenReturn(Result())
        whenever(mapper.mapBack(source = any<Movable>())).thenReturn(entity)

        val result = facade.get(id = entity.id!!)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entity)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(id = entity.id!!)
        verify(validator).validateExists(data = Optional.of(domain))
        verify(mapper).mapBack(source = domain)
        verifyNoMoreInteractions(service, mapper, validator)
    }

    /**
     * Test method for [ParentFacade.get] with not existing data.
     */
    @Test
    fun getNotExistingData() {
        whenever(service.get(id = any())).thenReturn(Optional.empty())
        whenever(validator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.get(id = Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(id = Int.MAX_VALUE)
        verify(validator).validateExists(data = Optional.empty())
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.update].
     */
    @Test
    fun update() {
        val entity = MovableStub(id = 1, position = 1)
        val domain = MovableStub(id = 1, position = 1)

        whenever(validator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(mapper.map(source = any<Movable>())).thenReturn(domain)

        val result = facade.update(data = entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).update(data = domain)
        verify(mapper).map(source = entity)
        verify(validator).validate(data = entity, update = true)
        verifyNoMoreInteractions(service, mapper, validator)
    }

    /**
     * Test method for [ParentFacade.update] with invalid data.
     */
    @Test
    fun updateInvalidData() {
        val entity = MovableStub(id = Int.MAX_VALUE, position = Int.MAX_VALUE)

        whenever(validator.validate(data = any(), update = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.update(data = entity)

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
        val domain = MovableStub(id = 1, position = 1)

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(data = any())).thenReturn(Result())

        facade.remove(id = 1)

        verify(service).get(id = 1)
        verify(service).remove(data = domain)
        verify(validator).validateExists(data = Optional.of(domain))
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.remove] with invalid data.
     */
    @Test
    fun removeInvalidData() {
        whenever(service.get(id = any())).thenReturn(Optional.empty())
        whenever(validator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.remove(id = Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(id = Int.MAX_VALUE)
        verify(validator).validateExists(data = Optional.empty())
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.duplicate].
     */
    @Test
    fun duplicate() {
        val domain = MovableStub(id = 1, position = 1)

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(data = any())).thenReturn(Result())

        facade.duplicate(id = 1)

        verify(service).get(id = 1)
        verify(service).duplicate(data = domain)
        verify(validator).validateExists(data = Optional.of(domain))
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.duplicate] with invalid data.
     */
    @Test
    fun duplicateInvalidData() {
        whenever(service.get(id = any())).thenReturn(Optional.empty())
        whenever(validator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.duplicate(id = Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(id = Int.MAX_VALUE)
        verify(validator).validateExists(data = Optional.empty())
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveUp].
     */
    @Test
    fun moveUp() {
        val domain = MovableStub(id = 1, position = 1)
        val dataList = listOf(domain, MovableStub(id = 2, position = 2))

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(service.getAll()).thenReturn(dataList)
        whenever(validator.validateExists(data = any())).thenReturn(Result())
        whenever(validator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(Result())

        val result = facade.moveUp(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(id = 1)
        verify(service).getAll()
        verify(service).moveUp(data = domain)
        verify(validator).validateExists(data = Optional.of(domain))
        verify(validator).validateMovingData(data = domain, list = dataList, up = true)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveUp] not existing data.
     */
    @Test
    fun moveUpNotExisting() {
        val domain = MovableStub(id = 1, position = 1)

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveUp(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(id = 1)
        verify(validator).validateExists(data = Optional.of(domain))
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveUp] not movable.
     */
    @Test
    fun moveUpNotMovable() {
        val domain = MovableStub(id = 1, position = 1)
        val dataList = listOf(domain, MovableStub(id = 2, position = 2))

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(service.getAll()).thenReturn(dataList)
        whenever(validator.validateExists(data = any())).thenReturn(Result())
        whenever(validator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveUp(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(id = 1)
        verify(service).getAll()
        verify(validator).validateExists(data = Optional.of(domain))
        verify(validator).validateMovingData(data = domain, list = dataList, up = true)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveDown].
     */
    @Test
    fun moveDown() {
        val domain = MovableStub(id = 1, position = 1)
        val dataList = listOf(domain, MovableStub(id = 2, position = 2))

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(service.getAll()).thenReturn(dataList)
        whenever(validator.validateExists(data = any())).thenReturn(Result())
        whenever(validator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(Result())

        val result = facade.moveDown(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).get(id = 1)
        verify(service).getAll()
        verify(service).moveDown(data = domain)
        verify(validator).validateExists(data = Optional.of(domain))
        verify(validator).validateMovingData(data = domain, list = dataList, up = false)
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveDown] not existing data.
     */
    @Test
    fun moveDownNotExisting() {
        val domain = MovableStub(id = 1, position = 1)

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(validator.validateExists(data = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveDown(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(id = 1)
        verify(validator).validateExists(data = Optional.of(domain))
        verifyNoMoreInteractions(service, validator)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [ParentFacade.moveDown] not movable.
     */
    @Test
    fun moveDownNotMovable() {
        val domain = MovableStub(id = 1, position = 1)
        val dataList = listOf(domain, MovableStub(id = 2, position = 2))

        whenever(service.get(id = any())).thenReturn(Optional.of(domain))
        whenever(service.getAll()).thenReturn(dataList)
        whenever(validator.validateExists(data = any())).thenReturn(Result())
        whenever(validator.validateMovingData(data = any(), list = any(), up = any())).thenReturn(TestConstants.INVALID_DATA_RESULT)

        val result = facade.moveDown(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(TestConstants.INVALID_DATA_RESULT.events())
        }

        verify(service).get(id = 1)
        verify(service).getAll()
        verify(validator).validateExists(data = Optional.of(domain))
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
        val entityList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))
        val domainList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

        whenever(service.getAll()).thenReturn(domainList)
        whenever(mapper.mapBack(source = any<List<Movable>>())).thenReturn(entityList)

        val result = facade.getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entityList)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).getAll()
        verify(mapper).mapBack(source = domainList)
        verifyNoMoreInteractions(service, mapper)
        verifyZeroInteractions(validator)
    }

    /**
     * Test method for [ParentFacade.add].
     */
    @Test
    fun add() {
        val entity = MovableStub(id = 1, position = 1)
        val domain = MovableStub(id = 1, position = 1)

        whenever(validator.validate(data = any(), update = any())).thenReturn(Result())
        whenever(mapper.map(source = any<Movable>())).thenReturn(domain)

        val result = facade.add(entity)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).add(data = domain)
        verify(mapper).map(source = entity)
        verify(validator).validate(data = entity, update = false)
        verifyNoMoreInteractions(service, mapper, validator)
    }

    /**
     * Test method for [ParentFacade.add] with invalid data.
     */
    @Test
    fun addInvalidData() {
        val entity = MovableStub(id = Int.MAX_VALUE, position = Int.MAX_VALUE)

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
