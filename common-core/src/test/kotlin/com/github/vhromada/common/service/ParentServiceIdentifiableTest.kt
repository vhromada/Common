package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.stub.IdentifiableStub
import com.github.vhromada.common.stub.ParentServiceIdentifiableStub
import com.github.vhromada.common.utils.TestConstants
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
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
 * A class represents test for class [ParentService] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ParentServiceIdentifiableTest {

    /**
     * Instance of [JpaRepository]
     */
    @Mock
    private lateinit var repository: JpaRepository<Identifiable, Int>

    /**
     * Instance of [AccountProvider]
     */
    @Mock
    private lateinit var accountProvider: AccountProvider

    /**
     * Instance of [ParentService]
     */
    private lateinit var service: ParentService<Identifiable>

    /**
     * Copy item
     */
    private lateinit var copyItem: Identifiable

    /**
     * Initializes data.
     */
    @BeforeEach
    fun setUp() {
        copyItem = IdentifiableStub(10)
        service = ParentServiceIdentifiableStub(
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
        val data = IdentifiableStub(1)

        whenever(repository.findById(any())).thenReturn(Optional.of(data))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        val result = service.get(data.id!!)

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
        val data = IdentifiableStub(1)

        whenever(repository.findById(any())).thenReturn(Optional.of(data))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        val result = service.get(data.id!!)

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

        val result = service.get(Int.MAX_VALUE)

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

        val result = service.get(Int.MAX_VALUE)

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
        val data = IdentifiableStub(null)

        whenever(repository.save(anyDomain())).thenAnswer(setId())

        val result = service.add(data)

        assertSoftly {
            it.assertThat(data.id).isEqualTo(1)
        }

        verify(repository).save(data)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(accountProvider)
        assertThat(result).isSameAs(data)
    }

    /**
     * Test method for [ParentService.update].
     */
    @Test
    fun update() {
        val data = IdentifiableStub(1)

        whenever(repository.save(anyDomain())).thenAnswer(copy())

        val result = service.update(data)

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
        val data = IdentifiableStub(1)

        service.remove(data)

        verify(repository).delete(data)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(accountProvider)
    }

    /**
     * Test method for [ParentService.duplicate].
     */
    @Test
    fun duplicate() {
        val copyArgumentCaptor = argumentCaptor<Identifiable>()

        whenever(repository.save(anyDomain())).thenAnswer(copy())

        val result = service.duplicate(IdentifiableStub(1))

        verify(repository).save(copyArgumentCaptor.capture())
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(accountProvider)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [ParentService.moveUp].
     */
    @Test
    fun moveUp() {
        service.moveUp(IdentifiableStub(1))

        verifyZeroInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.moveDown].
     */
    @Test
    fun moveDown() {
        service.moveDown(IdentifiableStub(1))

        verifyZeroInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.newData] with data for admin.
     */
    @Test
    fun newDataAdminData() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

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
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

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
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        val data = service.getAll()

        assertThat(data).isEqualTo(dataList)

        verify(repository).findAll()
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.getAll] with data for account.
     */
    @Test
    fun getAllAccountData() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        val data = service.getAll()

        assertThat(data).isEqualTo(dataList)

        verify(repository).findAll()
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ParentService.updatePositions] with data for admin.
     */
    @Test
    fun updatePositionsAdminData() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        service.updatePositions()

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
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        service.updatePositions()

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
    private fun anyDomain(): Identifiable {
        return any()
    }

    /**
     * Sets ID.
     *
     * @return mocked answer
     */
    private fun setId(): (InvocationOnMock) -> Identifiable {
        return {
            val item = it.arguments[0] as Identifiable
            item.id = 1
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
