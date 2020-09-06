package com.github.vhromada.common.test.service

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.TimeProvider
import com.github.vhromada.common.service.MovableService
import com.github.vhromada.common.test.utils.TestConstants
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
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
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper
import org.springframework.data.jpa.repository.JpaRepository

/**
 * ID
 */
private const val ID = 5

/**
 * Admin
 */
private val ADMIN = TestConstants.ACCOUNT.copy(roles = listOf("ROLE_ADMIN"))

/**
 * An abstract class represents test for [MovableService].
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
@Suppress("FunctionName")
abstract class MovableServiceTest<T : AuditEntity> {

    /**
     * Instance of [Cache]
     */
    @Mock
    protected lateinit var cache: Cache

    /**
     * Instance of [MovableService]
     */
    private lateinit var service: MovableService<T>

    /**
     * Instance of [JpaRepository]
     */
    private lateinit var repository: JpaRepository<T, Int>

    /**
     * Instance of [AccountProvider]
     */
    private lateinit var accountProvider: AccountProvider

    /**
     * Instance of [TimeProvider]
     */
    private lateinit var timeProvider: TimeProvider

    /**
     * Data list
     */
    private lateinit var dataList: List<T>

    /**
     * Initializes data.
     */
    @BeforeEach
    open fun setUp() {
        repository = getRepository()
        service = getService()
        accountProvider = getAccountProvider()
        timeProvider = getTimeProvider()
        dataList = listOf(getItem1(), getItem2())
    }

    /**
     * Test method for [MovableService.newData] with cached data.
     */
    @Test
    fun newDataCachedData() {
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.newData()

        verify(repository).deleteAll(dataList)
        verify(accountProvider).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).evictIfPresent(getCacheKey())
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)
    }

    /**
     * Test method for [MovableService.newData] with not cached data for admin.
     */
    @Test
    fun newDataNotCachedAdminData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.newData()

        verify(repository).findAll()
        verify(repository).deleteAll(dataList)
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).evictIfPresent(getCacheKey())
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)
    }

    /**
     * Test method for [MovableService.newData] with not cached data for account.
     */
    @Test
    fun newDataNotCachedAccountData() {
        initAllDataMock(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.newData()

        verify(repository).deleteAll(dataList)
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).evictIfPresent(getCacheKey())
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(timeProvider)
        verifyAllDataMock()
    }

    /**
     * Test method for [MovableService.getAll] with cached data.
     */
    @Test
    fun getAllCachedData() {
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        val data = service.getAll()

        assertThat(data).isEqualTo(dataList)

        verify(accountProvider).getAccount()
        verify(cache).get(getCacheKey())
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(repository, timeProvider)
    }

    /**
     * Test method for [MovableService.getAll] with not cached data for admin.
     */
    @Test
    fun getAllNotCachedAdminData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.getAll()

        assertThat(data).isEqualTo(dataList)

        verify(repository).findAll()
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)
    }

    /**
     * Test method for [MovableService.getAll] with not cached data for account.
     */
    @Test
    fun getAllNotCachedAccountData() {
        initAllDataMock(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.getAll()

        assertThat(data).isEqualTo(dataList)

        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(timeProvider)
        verifyAllDataMock()
    }

    /**
     * Test method for [MovableService.get] with cached existing data.
     */
    @Test
    fun getCachedExistingData() {
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        val data = service.get(dataList[0].id!!)

        assertThat(data).isPresent
        assertThat(data.get()).isEqualTo(dataList[0])

        verify(accountProvider).getAccount()
        verify(cache).get(getCacheKey())
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(repository, timeProvider)
    }

    /**
     * Test method for [MovableService.get] with cached not existing data.
     */
    @Test
    fun getCachedNotExistingData() {
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        val data = service.get(Int.MAX_VALUE)

        assertThat(data).isNotPresent

        verify(accountProvider).getAccount()
        verify(cache).get(getCacheKey())
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(repository, timeProvider)
    }

    /**
     * Test method for [MovableService.get] with not cached existing data for admin.
     */
    @Test
    fun getNotCachedExistingAdminData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.get(dataList[0].id!!)

        assertThat(data).isPresent
        assertThat(data.get()).isEqualTo(dataList[0])

        verify(repository).findAll()
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)
    }

    /**
     * Test method for [MovableService.get] with not cached existing data for account.
     */
    @Test
    fun getNotCachedExistingAccountData() {
        initAllDataMock(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.get(dataList[0].id!!)

        assertThat(data).isPresent
        assertThat(data.get()).isEqualTo(dataList[0])

        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(timeProvider)
        verifyAllDataMock()
    }

    /**
     * Test method for [MovableService.get] with not cached not existing data for admin.
     */
    @Test
    fun getNotCachedNotExistingAdminData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.get(Int.MAX_VALUE)

        assertThat(data).isNotPresent

        verify(repository).findAll()
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)
    }

    /**
     * Test method for [MovableService.get] with not cached not existing data for account.
     */
    @Test
    fun getNotCachedNotExistingAccountData() {
        initAllDataMock(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.get(Int.MAX_VALUE)

        assertThat(data).isNotPresent

        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(timeProvider)
        verifyAllDataMock()
    }

    /**
     * Test method for [MovableService.add].
     */
    @Test
    fun add() {
        val data = getAddItem()

        whenever(repository.save(anyItem())).thenAnswer(setIdAndPosition())
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)

        service.add(data)

        assertAddResult(data)

        verify(repository, times(2)).save(data)
        verify(accountProvider, times(2)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).evictIfPresent(getCacheKey())
        verifyNoMoreInteractions(repository, accountProvider, timeProvider, cache)
    }

    /**
     * Test method for [MovableService.update] with cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun updateCachedData() {
        val data = dataList[0]
        data.position = 10

        whenever(repository.save(anyItem())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.update(data)

        assertSoftly {
            it.assertThat(dataList.size).isEqualTo(2)
            it.assertThat(dataList[0]).isEqualTo(data)
        }

        verify(repository).save(data)
        verify(accountProvider).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)
    }

    /**
     * Test method for [MovableService.update] with not cached data for admin.
     */
    @Test
    fun updateNotCachedAdminData() {
        val data = dataList[0]
        data.position = 10

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(repository.save(anyItem())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.update(data)

        assertSoftly {
            it.assertThat(dataList.size).isEqualTo(2)
            it.assertThat(dataList[0]).isEqualTo(data)
        }

        verify(repository).findAll()
        verify(repository).save(data)
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)
    }

    /**
     * Test method for [MovableService.update] with not cached data for account.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun updateNotCachedAccountData() {
        val data = dataList[0]
        data.position = 10

        initAllDataMock(dataList)
        whenever(repository.save(anyItem())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.update(data)

        assertSoftly {
            it.assertThat(dataList.size).isEqualTo(2)
            it.assertThat(dataList[0]).isEqualTo(data)
        }

        verify(repository).save(data)
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(timeProvider)
        verifyAllDataMock()
    }


    /**
     * Test method for [MovableService.remove] with cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun removeCachedData() {
        val data = dataList[0]
        val cacheArgumentCaptor = argumentCaptor<List<T>>()

        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.remove(data)

        verify(repository).delete(data)
        verify(accountProvider).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(eq(getCacheKey()), cacheArgumentCaptor.capture())
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)

        val cacheData = cacheArgumentCaptor.lastValue
        assertSoftly {
            it.assertThat(cacheData.size).isEqualTo(dataList.size - 1)
            it.assertThat(cacheData.contains(data)).isFalse
        }
    }

    /**
     * Test method for [MovableService.remove] with not cached data for admin.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun removeNotCachedAdminData() {
        val data = dataList[0]
        val cacheArgumentCaptor = argumentCaptor<List<T>>()

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.remove(data)

        verify(repository).findAll()
        verify(repository).delete(data)
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(eq(getCacheKey()), cacheArgumentCaptor.capture())
        verifyNoMoreInteractions(repository, accountProvider, cache)
        verifyZeroInteractions(timeProvider)

        val cacheData = cacheArgumentCaptor.lastValue
        assertSoftly {
            it.assertThat(cacheData.size).isEqualTo(dataList.size - 1)
            it.assertThat(cacheData.contains(data)).isFalse
        }
    }


    /**
     * Test method for [MovableService.remove] with not cached data for account.
     */
    @Test
    fun removeNotCachedAccountData() {
        val data = dataList[0]
        val cacheArgumentCaptor = argumentCaptor<List<T>>()

        initAllDataMock(dataList)
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.remove(data)

        verify(repository).delete(data)
        verify(accountProvider, times(2)).getAccount()
        verify(cache).get(getCacheKey())
        verify(cache).put(eq(getCacheKey()), cacheArgumentCaptor.capture())
        verifyNoMoreInteractions(accountProvider, cache)
        verifyZeroInteractions(timeProvider)
        verifyAllDataMock()

        val cacheData = cacheArgumentCaptor.lastValue
        assertSoftly {
            it.assertThat(cacheData.size).isEqualTo(dataList.size - 1)
            it.assertThat(cacheData.contains(data)).isFalse
        }
    }

    /**
     * Test method for [MovableService.duplicate].
     */
    @Test
    fun duplicate() {
        val copy = getCopyItem()
        val copyArgumentCaptor = argumentCaptorItem()

        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)

        service.duplicate(dataList[0])

        verify(repository).save(copyArgumentCaptor.capture())
        verify(accountProvider, times(2)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).evictIfPresent(getCacheKey())
        verifyNoMoreInteractions(repository, accountProvider, timeProvider, cache)

        val copyArgument = copyArgumentCaptor.lastValue
        assertDataDeepEquals(copy, copyArgument)
    }

    /**
     * Test method for [MovableService.moveUp] with cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveUpCachedData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.moveUp(data2)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).saveAll(listOf(data2, data1))
        verify(accountProvider, times(2)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, timeProvider, cache)
    }

    /**
     * Test method for [MovableService.moveUp] with not cached data for admin.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveUpNotCachedAdminData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.moveUp(data2)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).findAll()
        verify(accountProvider, times(3)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, timeProvider, cache)
    }

    /**
     * Test method for [MovableService.moveUp] with not cached data for account.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveUpNotCachedAccountData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        initAllDataMock(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.moveUp(data2)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(accountProvider, times(3)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(accountProvider, timeProvider, cache)
        verifyAllDataMock()
    }

    /**
     * Test method for [MovableService.moveDown] with cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveDownCachedData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.moveDown(data1)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).saveAll(listOf(data1, data2))
        verify(accountProvider, times(2)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, timeProvider, cache)
    }

    /**
     * Test method for [MovableService.moveDown] with not cached data for admin.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveDownNotCachedAdminData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.moveDown(data1)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).findAll()
        verify(repository).saveAll(listOf(data1, data2))
        verify(accountProvider, times(3)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, timeProvider, cache)
    }

    /**
     * Test method for [MovableService.moveDown] with not cached data for account.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveDownNotCachedAccountData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        initAllDataMock(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.moveDown(data1)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).saveAll(listOf(data1, data2))
        verify(accountProvider, times(3)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(accountProvider, timeProvider, cache)
        verifyAllDataMock()
    }

    /**
     * Test method for [MovableService.updatePositions] with cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun updatePositionsCachedData() {
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.updatePositions()

        for (i in dataList.indices) {
            val data = dataList[i]
            assertThat(data.position).isEqualTo(i)
        }

        verify(repository).saveAll(dataList)
        verify(accountProvider, times(2)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, timeProvider, cache)
    }

    /**
     * Test method for [MovableService.updatePositions] with not cached data for admin.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun updatePositionsNotCachedAdminData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(ADMIN)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.updatePositions()

        for (i in dataList.indices) {
            val data = dataList[i]
            assertThat(data.position).isEqualTo(i)
        }

        verify(repository).findAll()
        verify(repository).saveAll(dataList)
        verify(accountProvider, times(3)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, accountProvider, timeProvider, cache)
    }

    /**
     * Test method for [MovableService.updatePositions] with not cached data for account.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun updatePositionsNotCachedAccountData() {
        initAllDataMock(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(accountProvider.getAccount()).thenReturn(TestConstants.ACCOUNT)
        whenever(timeProvider.getTime()).thenReturn(TestConstants.TIME)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.updatePositions()

        for (i in dataList.indices) {
            val data = dataList[i]
            assertThat(data.position).isEqualTo(i)
        }

        verify(repository).saveAll(dataList)
        verify(accountProvider, times(3)).getAccount()
        verify(timeProvider).getTime()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(accountProvider, timeProvider, cache)
        verifyAllDataMock()
    }

    /**
     * Returns instance of [JpaRepository].
     *
     * @return instance of [JpaRepository]
     */
    protected abstract fun getRepository(): JpaRepository<T, Int>

    /**
     * Returns instance of [AccountProvider].
     *
     * @return instance of [AccountProvider]
     */
    protected abstract fun getAccountProvider(): AccountProvider

    /**
     * Returns instance of [TimeProvider].
     *
     * @return instance of [TimeProvider]
     */
    protected abstract fun getTimeProvider(): TimeProvider

    /**
     * Returns instance of [MovableService].
     *
     * @return instance of [MovableService]
     */
    protected abstract fun getService(): MovableService<T>

    /**
     * Returns cache key.
     *
     * @return cache key
     */
    protected abstract fun getCacheKey(): String

    /**
     * Returns 1st item in data list.
     *
     * @return 1st item in data list
     */
    protected abstract fun getItem1(): T

    /**
     * Returns 2nd item in data list.
     *
     * @return 2nd item in data list
     */
    protected abstract fun getItem2(): T

    /**
     * Returns add item
     *
     * @return add item
     */
    protected abstract fun getAddItem(): T

    /**
     * Returns copy item.
     *
     * @return copy item
     */
    protected abstract fun getCopyItem(): T

    /**
     * Init mock for getting data.
     *
     * @param data data
     */
    protected abstract fun initAllDataMock(data: List<T>)

    /**
     * Verify mock for getting data.
     */
    protected abstract fun verifyAllDataMock()

    /**
     * Returns any mock for item.
     *
     * @return any mock for item
     */
    protected abstract fun anyItem(): T

    /**
     * Returns argument captor for item.
     *
     * @return argument captor for item
     */
    protected abstract fun argumentCaptorItem(): KArgumentCaptor<T>

    /**
     * Asserts data deep equals.
     *
     * @param expected expected data
     * @param actual   actual data
     */
    protected abstract fun assertDataDeepEquals(expected: T, actual: T)

    /**
     * Sets ID and position.
     *
     * @return mocked answer
     */
    private fun setIdAndPosition(): (InvocationOnMock) -> Movable {
        return {
            val movable = it.arguments[0] as Movable
            if (movable.id == null) {
                movable.id = ID
                movable.position = ID - 1
            }
            movable
        }
    }

    /**
     * Asserts result of [MovableService.add]
     *
     * @param data add item
     */
    private fun assertAddResult(data: T) {
        assertSoftly {
            it.assertThat(data.id).isEqualTo(ID)
            it.assertThat(data.position).isEqualTo(ID - 1)
            it.assertThat(data.audit).isNotNull
        }
    }

}
