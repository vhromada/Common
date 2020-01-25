package cz.vhromada.common.service

import cz.vhromada.common.Movable
import cz.vhromada.common.utils.sorted
import org.springframework.cache.Cache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

/**
 * An abstract class represents service for movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
@Transactional
abstract class AbstractMovableService<T : Movable>(
        private val repository: JpaRepository<T, Int>,
        private val cache: Cache,
        private val key: String) : MovableService<T> {

    @Transactional(readOnly = true)
    override fun getAll(): List<T> {
        return getCachedData(true)
                .sorted()
    }

    override fun newData() {
        repository.deleteAll()
        cache.clear()
    }

    @Transactional(readOnly = true)
    override fun get(id: Int): T? {
        return getCachedData(true)
                .firstOrNull { id == it.id }
    }

    override fun add(data: T) {
        data.position = 0
        val addedData = repository.save(data)
        addedData.position = addedData.id!! - 1
        repository.save(addedData)
        cache.clear()
    }

    override fun update(data: T) {
        val updatedData = repository.save(data)
        val dataList = getCachedData(false)
                .toMutableList()
        updateItem(dataList, updatedData)
        cache.put(key, dataList)
    }

    override fun remove(data: T) {
        repository.delete(data)
        val dataList = getCachedData(false)
                .toMutableList()
        dataList.remove(data)
        cache.put(key, dataList)
    }

    override fun duplicate(data: T) {
        repository.save(getCopy(data))
        cache.clear()
    }

    override fun moveUp(data: T) {
        move(data, true)
    }

    override fun moveDown(data: T) {
        move(data, false)
    }

    override fun updatePositions() {
        val data = getCachedData(false)
                .sorted()
        updatePositions(data)
        val savedData = repository.saveAll(data)
        cache.put(key, savedData)
    }

    /**
     * Returns copy of data.
     *
     * @param data data
     * @return copy of data
     */
    protected abstract fun getCopy(data: T): T

    /**
     * Updates positions.
     *
     * @param data data
     */
    protected fun updatePositions(data: List<T>) {
        for (i in data.indices) {
            data[i].position = i
        }
    }

    /**
     * Returns list of data.
     *
     * @param cached true if returned data from repository should be cached
     * @return list of data
     */
    @Suppress("UNCHECKED_CAST")
    private fun getCachedData(cached: Boolean): List<T> {
        val cacheValue = cache.get(key)
        if (cacheValue == null) {
            val data = repository.findAll()
            if (cached) {
                cache.put(key, data)
            }
            return data
        }
        return cacheValue.get() as List<T>
    }

    /**
     * Moves data in list one position up or down.
     *
     * @param data data
     * @param up   if moving data up
     * @throws IllegalArgumentException if data is null
     */
    private fun move(data: T, up: Boolean) {
        val dataList = getCachedData(false)
                .sorted()
                .toMutableList()
        val index = dataList.indexOf(data)
        val other = dataList[if (up) index - 1 else index + 1]
        val position = data.position!!
        data.position = other.position
        other.position = position
        val updatedData = repository.saveAll(listOf(data, other))
        updateItem(dataList, updatedData[0])
        updateItem(dataList, updatedData[1])
        cache.put(key, dataList)
    }

    /**
     * Updates item if list of data.
     *
     * @param data list of data
     * @param item updating item
     */
    private fun updateItem(data: MutableList<T>, item: T) {
        data[data.indexOf(item)] = item
    }

}
