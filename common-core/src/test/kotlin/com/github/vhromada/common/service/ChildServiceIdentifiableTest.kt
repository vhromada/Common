package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.stub.ChildServiceIdentifiableStub
import com.github.vhromada.common.stub.IdentifiableStub
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
 * A class represents test for class [ChildService] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ChildServiceIdentifiableTest {

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
     * Instance of [ChildService]
     */
    private lateinit var service: ChildService<Identifiable>

    /**
     * Copy item
     */
    private lateinit var copyItem: Identifiable

    /**
     * Parent item
     */
    private lateinit var parentItem: Identifiable

    /**
     * Initializes data.
     */
    @BeforeEach
    fun setUp() {
        copyItem = IdentifiableStub(10)
        parentItem = IdentifiableStub(20)
        service = ChildServiceIdentifiableStub(
            repository = repository,
            accountProvider = accountProvider,
            copy = { copyItem },
            data = { id -> repository.findById(id) },
            dataList = { repository.findAll() },
            parent = { parentItem },
            parentData = { repository.findAll() })
    }

    /**
     * Test method for [ChildService.get] with existing admin data.
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
     * Test method for [ChildService.get] with existing account data.
     */
    @Test
    fun getExistingAccountData() {
        val data = IdentifiableStub(1)

        whenever(repository.findById(any())).thenReturn(Optional.ofNullable(data))
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        val result = service.get(data.id!!)

        assertThat(result).contains(data)

        verify(repository).findById(data.id!!)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ChildService.get] with not existing admin data.
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
     * Test method for [ChildService.get] with not existing account data.
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
     * Test method for [ChildService.add].
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
     * Test method for [ChildService.update].
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
     * Test method for [ChildService.remove].
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
     * Test method for [ChildService.duplicate].
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
     * Test method for [ChildService.moveUp].
     */
    @Test
    fun moveUp() {
        service.moveUp(IdentifiableStub(1))

        verifyZeroInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ChildService.moveDown].
     */
    @Test
    fun moveDown() {
        service.moveDown(IdentifiableStub(1))

        verifyZeroInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ChildService.find] with data for admin.
     */
    @Test
    fun findAdminData() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ADMIN)

        val result = service.find(parentItem.id!!)

        assertThat(result).isEqualTo(dataList)

        verify(repository).findAll()
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(repository, accountProvider)
    }

    /**
     * Test method for [ChildService.find] with data for account.
     */
    @Test
    fun findAccountData() {
        val dataList = listOf(IdentifiableStub(1), IdentifiableStub(2))

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)

        val result = service.find(parentItem.id!!)

        assertThat(result).isEqualTo(dataList)

        verify(repository).findAll()
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
