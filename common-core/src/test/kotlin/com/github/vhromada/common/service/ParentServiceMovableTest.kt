package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.stub.MovableStub
import com.github.vhromada.common.stub.ParentServiceMovableStub
import com.github.vhromada.common.utils.TestConstants
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
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
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * A class represents test for class [ParentService] for [Movable].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ParentServiceMovableTest {

    /**
     * Instance of [JpaRepository]
     */
    @Mock
    private lateinit var repository: JpaRepository<Movable, Int>

    /**
     * Instance of [AccountProvider]
     */
    @Mock
    private lateinit var accountProvider: AccountProvider

    /**
     * Instance of [ParentService]
     */
    private lateinit var service: ParentService<Movable>

    /**
     * Copy item
     */
    private lateinit var copyItem: Movable

    /**
     * Initializes data.
     */
    @BeforeEach
    fun setUp() {
        copyItem = MovableStub(id = 10, position = 10)
        service = ParentServiceMovableStub(
            repository = repository,
            accountProvider = accountProvider,
            copy = { copyItem },
            data = { id -> repository.findById(id) },
            dataList = { repository.findAll() })
    }

    /**
     * Test method for [ParentService.get] with existing admin data.
     */
    @Test
    fun getExistingAdminData() {
        val data = MovableStub(id = 1, position = 1)

        whenever(repository.findById(any())).thenReturn(Optional.of(data))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        val result = service.get(id = data.id!!)

        assertThat(result).contains(data)

        verify(repository).findById(data.id!!)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.get] with existing account data.
     */
    @Test
    fun getExistingAccountData() {
        val data = MovableStub(id = 1, position = 1)

        whenever(repository.findById(any())).thenReturn(Optional.of(data))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        val result = service.get(id = data.id!!)

        assertThat(result).contains(data)

        verify(repository).findById(data.id!!)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.get] with not existing admin data.
     */
    @Test
    fun getNotExistingAdminData() {
        whenever(repository.findById(any())).thenReturn(Optional.empty())
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        val result = service.get(id = Int.MAX_VALUE)

        assertThat(result).isNotPresent

        verify(repository).findById(Int.MAX_VALUE)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.get] with not existing account data.
     */
    @Test
    fun getNotExistingAccountData() {
        whenever(repository.findById(any())).thenReturn(Optional.empty())
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        val result = service.get(id = Int.MAX_VALUE)

        assertThat(result).isNotPresent

        verify(repository).findById(Int.MAX_VALUE)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.add].
     */
    @Test
    fun add() {
        val data = MovableStub(id = null, position = null)

        whenever(repository.save(anyDomain())).thenAnswer(setIdAndPosition())

        val result = service.add(data = data)

        assertSoftly {
            it.assertThat(data.id).isEqualTo(1)
            it.assertThat(data.position).isEqualTo(2)
        }

        verify(repository, times(2)).save(data)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(accountProvider)
        assertThat(result).isSameAs(data)
    }

    /**
     * Test method for [ParentService.update].
     */
    @Test
    fun update() {
        val data = MovableStub(id = 1, position = 1)

        whenever(repository.save(anyDomain())).thenAnswer(copy())

        val result = service.update(data = data)

        verify(repository).save(data)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(accountProvider)
        assertThat(result).isSameAs(data)
    }

    /**
     * Test method for [ParentService.remove].
     */
    @Test
    fun remove() {
        val data = MovableStub(id = 1, position = 1)

        service.remove(data = data)

        verify(repository).delete(data)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(accountProvider)
    }

    /**
     * Test method for [ParentService.duplicate].
     */
    @Test
    fun duplicate() {
        val copyArgumentCaptor = argumentCaptor<Movable>()

        whenever(repository.save(anyDomain())).thenAnswer(copy())

        val result = service.duplicate(data = MovableStub(id = 1, position = 1))

        verify(repository).save(copyArgumentCaptor.capture())
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(accountProvider)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [ParentService.moveUp] with data for admin.
     */
    @Test
    fun moveUpAdminData() {
        val data1 = MovableStub(id = 1, position = 1)
        val data2 = MovableStub(id = 2, position = 2)
        val position1 = data1.position
        val position2 = data2.position

        whenever(repository.findAll()).thenReturn(listOf(data1, data2))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        service.moveUp(data = data2)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).findAll()
        verify(repository).saveAll(listOf(data2, data1))
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.moveUp] with data for account.
     */
    @Test
    fun moveUpAccountData() {
        val data1 = MovableStub(id = 1, position = 1)
        val data2 = MovableStub(id = 2, position = 2)
        val position1 = data1.position
        val position2 = data2.position

        whenever(repository.findAll()).thenReturn(listOf(data1, data2))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        service.moveUp(data = data2)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).findAll()
        verify(repository).saveAll(listOf(data2, data1))
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.moveDown] with data for admin.
     */
    @Test
    fun moveDownAdminData() {
        val data1 = MovableStub(id = 1, position = 1)
        val data2 = MovableStub(id = 2, position = 2)
        val position1 = data1.position
        val position2 = data2.position

        whenever(repository.findAll()).thenReturn(listOf(data1, data2))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        service.moveDown(data = data1)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).findAll()
        verify(repository).saveAll(listOf(data1, data2))
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.moveDown] with data for account.
     */
    @Test
    fun moveDownAccountData() {
        val data1 = MovableStub(id = 1, position = 1)
        val data2 = MovableStub(id = 2, position = 2)
        val position1 = data1.position
        val position2 = data2.position

        whenever(repository.findAll()).thenReturn(listOf(data1, data2))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        service.moveDown(data = data1)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).findAll()
        verify(repository).saveAll(listOf(data1, data2))
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.newData] with data for admin.
     */
    @Test
    fun newDataAdminData() {
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        service.newData()

        verify(repository).findAll()
        verify(repository).deleteAll(dataList)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.newData] with data for account.
     */
    @Test
    fun newDataAccountData() {
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        service.newData()

        verify(repository).findAll()
        verify(repository).deleteAll(dataList)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.getAll] with data for admin.
     */
    @Test
    fun getAllAdminData() {
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        val result = service.getAll()

        assertThat(result).isEqualTo(dataList)

        verify(repository).findAll()
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.getAll] with data for account.
     */
    @Test
    fun getAllAccountData() {
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        val result = service.getAll()

        assertThat(result).isEqualTo(dataList)

        verify(repository).findAll()
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.updatePositions] with data for admin.
     */
    @Test
    fun updatePositionsAdminData() {
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        service.updatePositions()

        for (i in dataList.indices) {
            assertThat(dataList[i].position).isEqualTo(i)
        }

        verify(repository).findAll()
        verify(repository).saveAll(dataList)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.updatePositions] with data for account.
     */
    @Test
    fun updatePositionsAccountData() {
        val dataList = listOf(MovableStub(id = 1, position = 1), MovableStub(id = 2, position = 2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        service.updatePositions()

        for (i in dataList.indices) {
            assertThat(dataList[i].position).isEqualTo(i)
        }

        verify(repository).findAll()
        verify(repository).saveAll(dataList)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Returns any mock for domain entity.
     *
     * @return any mock for domain entity
     */
    private fun anyDomain(): Movable {
        return any()
    }

    /**
     * Sets ID and position.
     *
     * @return mocked answer
     */
    private fun setIdAndPosition(): (InvocationOnMock) -> Movable {
        return {
            val item = it.arguments[0] as Movable
            item.id = 1
            item.position = 2
            item
        }
    }

    /**
     * Coping answer.
     *
     * @return mocked answer
     */
    private fun copy(): (InvocationOnMock) -> Any {
        return {
            it.arguments[0]
        }
    }

}
