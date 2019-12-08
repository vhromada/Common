package cz.vhromada.common.test.service

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import cz.vhromada.common.Movable
import cz.vhromada.common.service.MovableService
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
 * An abstract class represents test for [MovableService].
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
@Suppress("FunctionName")
abstract class MovableServiceTest<T : Movable> {

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
        dataList = listOf(getItem1(), getItem2())
    }

    /**
     * Test method for [MovableService.newData].
     */
    @Test
    fun newData() {
        service.newData()

        verify(repository).deleteAll()
        verify(cache).clear()
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.getAll] with cached data.
     */
    @Test
    fun getAll_CachedData() {
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        val data = service.getAll()

        assertThat(data).isEqualTo(dataList)

        verify(cache).get(getCacheKey())
        verifyNoMoreInteractions(cache)
        verifyZeroInteractions(repository)
    }

    /**
     * Test method for [MovableService.getAll] with not cached data.
     */
    @Test
    fun getAll_NotCachedData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.getAll()

        assertThat(data).isEqualTo(dataList)

        verify(repository).findAll()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.get] with cached existing data.
     */
    @Test
    fun get_CachedExistingData() {
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        val data = service.get(dataList[0].id!!)

        assertThat(data).isEqualTo(dataList[0])

        verify(cache).get(getCacheKey())
        verifyNoMoreInteractions(cache)
        verifyZeroInteractions(repository)
    }

    /**
     * Test method for [MovableService.get] with cached not existing data.
     */
    @Test
    fun get_CachedNotExistingData() {
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        val data = service.get(Integer.MAX_VALUE)

        assertThat(data).isNull()

        verify(cache).get(getCacheKey())
        verifyNoMoreInteractions(cache)
        verifyZeroInteractions(repository)
    }

    /**
     * Test method for [MovableService.get] with not cached existing data.
     */
    @Test
    fun get_NotCachedExistingData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.get(dataList[0].id!!)

        assertThat(data).isEqualTo(dataList[0])

        verify(repository).findAll()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.get] with not cached not existing data.
     */
    @Test
    fun get_NotCachedNotExistingData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(cache.get(any<String>())).thenReturn(null)

        val data = service.get(Integer.MAX_VALUE)

        assertThat(data).isNull()

        verify(repository).findAll()
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.add] with cached data.
     */
    @Test
    fun add() {
        val data = getAddItem()

        whenever(repository.save(anyItem())).thenAnswer(setIdAndPosition())

        service.add(data)

        assertAddResult(data)

        verify(repository, times(2)).save(data)
        verify(cache).clear()
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.update] with cached data.
     */
    @Test
    fun update_CachedData() {
        val data = dataList[0]
        data.position = 10

        whenever(repository.save(anyItem())).thenAnswer { it.arguments[0] }
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.update(data)

        assertSoftly {
            it.assertThat(dataList.size).isEqualTo(2)
            it.assertThat(dataList[0]).isEqualTo(data)
        }

        verify(repository).save(data)
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.update] with not cached data.
     */
    @Test
    fun update_NotCachedData() {
        val data = dataList[0]
        data.position = 10

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(repository.save(anyItem())).thenAnswer { it.arguments[0] }
        whenever(cache.get(any<String>())).thenReturn(null)

        service.update(data)

        assertSoftly {
            it.assertThat(dataList.size).isEqualTo(2)
            it.assertThat(dataList[0]).isEqualTo(data)
        }

        verify(repository).findAll()
        verify(repository).save(data)
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.remove] with cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun remove_CachedData() {
        val data = dataList[0]
        val cacheArgumentCaptor = argumentCaptor<List<T>>()

        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.remove(data)

        verify(repository).delete(data)
        verify(cache).get(getCacheKey())
        verify(cache).put(eq(getCacheKey()), cacheArgumentCaptor.capture())
        verifyNoMoreInteractions(repository, cache)

        val cacheData = cacheArgumentCaptor.lastValue
        assertSoftly {
            it.assertThat(cacheData.size).isEqualTo(dataList.size - 1)
            it.assertThat(cacheData.contains(data)).isFalse
        }
    }

    /**
     * Test method for [MovableService.remove] with not cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun remove_NotCachedData() {
        val data = dataList[0]
        val cacheArgumentCaptor = argumentCaptor<List<T>>()

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(cache.get(any<String>())).thenReturn(null)

        service.remove(data)

        verify(repository).findAll()
        verify(repository).delete(data)
        verify(cache).get(getCacheKey())
        verify(cache).put(eq(getCacheKey()), cacheArgumentCaptor.capture())
        verifyNoMoreInteractions(repository, cache)

        val cacheData = cacheArgumentCaptor.lastValue
        assertSoftly {
            it.assertThat(cacheData.size).isEqualTo(dataList.size - 1)
            it.assertThat(cacheData.contains(data)).isFalse
        }
    }

    /**
     * Test method for [MovableService.duplicate] with cached data.
     */
    @Test
    fun duplicate() {
        val copy = getCopyItem()
        val copyArgumentCaptor = argumentCaptorItem()

        service.duplicate(dataList[0])

        verify(repository).save(copyArgumentCaptor.capture())
        verify(cache).clear()
        verifyNoMoreInteractions(repository, cache)

        val copyArgument = copyArgumentCaptor.lastValue
        assertDataDeepEquals(copy, copyArgument)
    }

    /**
     * Test method for [MovableService.moveUp] with cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveUp_CachedData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.moveUp(data2)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).saveAll(listOf(data2, data1))
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.moveUp] with not cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveUp_NotCachedData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(cache.get(any<String>())).thenReturn(null)

        service.moveUp(data2)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).findAll()
        verify(repository).saveAll(listOf(data2, data1))
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.moveDown] with cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveDown_CachedData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.moveDown(data1)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).saveAll(listOf(data1, data2))
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.moveDown] with not cached data.
     */
    @Test
    @Suppress("DuplicatedCode")
    fun moveDown_NotCachedData() {
        val data1 = dataList[0]
        val position1 = data1.position
        val data2 = dataList[1]
        val position2 = data2.position

        whenever(repository.findAll()).thenReturn(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(cache.get(any<String>())).thenReturn(null)

        service.moveDown(data1)

        assertSoftly {
            it.assertThat(data1.position).isEqualTo(position2)
            it.assertThat(data2.position).isEqualTo(position1)
        }

        verify(repository).findAll()
        verify(repository).saveAll(listOf(data1, data2))
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.updatePositions] with cached data.
     */
    @Test
    fun updatePositions_CachedData() {
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(cache.get(any<String>())).thenReturn(SimpleValueWrapper(dataList))

        service.updatePositions()

        for (i in dataList.indices) {
            val data = dataList[i]
            assertThat(data.position).isEqualTo(i)
        }

        verify(repository).saveAll(dataList)
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Test method for [MovableService.updatePositions] with not cached data.
     */
    @Test
    fun updatePositions_NotCachedData() {
        whenever(repository.findAll()).thenReturn(dataList)
        whenever(repository.saveAll(any<List<T>>())).thenAnswer { it.arguments[0] }
        whenever(cache.get(any<String>())).thenReturn(null)

        service.updatePositions()

        for (i in dataList.indices) {
            val data = dataList[i]
            assertThat(data.position).isEqualTo(i)
        }

        verify(repository).findAll()
        verify(repository).saveAll(dataList)
        verify(cache).get(getCacheKey())
        verify(cache).put(getCacheKey(), dataList)
        verifyNoMoreInteractions(repository, cache)
    }

    /**
     * Returns instance of [JpaRepository].
     *
     * @return instance of [JpaRepository]
     */
    protected abstract fun getRepository(): JpaRepository<T, Int>

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
        }
    }

}
