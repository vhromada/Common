package com.github.vhromada.common.service

import com.github.vhromada.common.domain.Audit
import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.TimeProvider
import com.github.vhromada.common.utils.sorted
import org.springframework.cache.Cache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

/**
 * An abstract class represents service for audible and movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
@Transactional
abstract class AbstractMovableService<T : AuditEntity>(
        private val repository: JpaRepository<T, Int>,
        private val accountProvider: AccountProvider,
        private val timeProvider: TimeProvider,
        private val cache: Cache,
        key: String) : MovableService<T> {

    /**
     * Cache key
     */
    private val cacheKey by lazy { key + accountProvider.getAccount().id }

    @Transactional(readOnly = true)
    override fun getAll(): List<T> {
        return getCachedData(true)
                .sorted()
    }

    override fun newData() {
        repository.deleteAll(getCachedData(false))
        cache.evictIfPresent(cacheKey)
    }

    @Transactional(readOnly = true)
    override fun get(id: Int): T? {
        return getCachedData(true)
                .firstOrNull { id == it.id }
    }

    override fun add(data: T) {
        data.position = 0
        data.audit = getAudit()
        val addedData = repository.save(data)
        addedData.position = addedData.id!! - 1
        repository.save(addedData)
        cache.evictIfPresent(cacheKey)
    }

    override fun update(data: T) {
        val updatedData = repository.save(data)
        val dataList = getCachedData(false)
                .toMutableList()
        updateItem(dataList, updatedData)
        cache.put(cacheKey, dataList)
    }

    override fun remove(data: T) {
        repository.delete(data)
        val dataList = getCachedData(false)
                .toMutableList()
        dataList.remove(data)
        cache.put(cacheKey, dataList)
    }

    override fun duplicate(data: T) {
        val copy = getCopy(data)
        copy.audit = getAudit()
        repository.save(copy)
        cache.evictIfPresent(cacheKey)
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
        cache.put(cacheKey, savedData)
    }

    /**
     * Returns account data.
     *
     * @param account account
     * @return account data
     */
    protected abstract fun getAccountData(account: Account): List<T>

    /**
     * Returns copy of data.
     *
     * @param data data
     * @return copy of data
     */
    protected abstract fun getCopy(data: T): T

    /**
     * Returns data.
     *
     * @return data
     */
    protected fun getData(): List<T> {
        val account = accountProvider.getAccount()
        if (account.roles.contains("ROLE_ADMIN")) {
            return repository.findAll()
        }
        return getAccountData(account)
    }

    /**
     * Updates positions.
     *
     * @param data data
     */
    protected fun updatePositions(data: List<T>) {
        val audit = getAudit()
        for (i in data.indices) {
            data[i].position = i
            data[i].modify(audit)
        }
    }

    /**
     * Returns audit
     *
     * @return audit
     */
    protected fun getAudit(): Audit {
        return Audit(accountProvider.getAccount().id, timeProvider.getTime())
    }

    /**
     * Returns list of data.
     *
     * @param cached true if returned data from repository should be cached
     * @return list of data
     */
    @Suppress("UNCHECKED_CAST")
    private fun getCachedData(cached: Boolean): List<T> {
        val cacheValue = cache.get(cacheKey)
        if (cacheValue == null) {
            val data = getData()
            if (cached) {
                cache.put(cacheKey, data)
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
        val audit = getAudit()
        data.position = other.position
        other.position = position
        data.modify(audit)
        other.modify(audit)
        val updatedData = repository.saveAll(listOf(data, other))
        updateItem(dataList, updatedData[0])
        updateItem(dataList, updatedData[1])
        cache.put(cacheKey, dataList)
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
