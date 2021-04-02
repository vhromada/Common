package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.utils.sorted
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * An abstract class represents service for data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
abstract class AbstractService<T : Identifiable>(
    protected val repository: JpaRepository<T, Int>,
    protected val accountProvider: AccountProvider
) : Service<T> {

    override fun get(id: Int): Optional<T> {
        val account = accountProvider.getAccount()
        if (account.roles!!.contains("ROLE_ADMIN")) {
            return repository.findById(id)
        }
        return getAccountData(account = account, id = id)
    }

    @Transactional
    override fun add(data: T): T {
        if (data is Movable) {
            data.position = Int.MAX_VALUE
        }
        var addedData = repository.save(data)
        if (addedData is Movable) {
            addedData.position = addedData.id!! - 1
            addedData = repository.save(addedData)
        }
        return addedData
    }

    @Transactional
    override fun update(data: T): T {
        return repository.save(data)
    }

    @Transactional
    override fun duplicate(data: T): T {
        val copy = getCopy(data)
        return repository.save(copy)
    }

    @Transactional
    override fun moveUp(data: T) {
        move(data, true)
    }

    @Transactional
    override fun moveDown(data: T) {
        move(data, false)
    }

    /**
     * Returns copy of data.
     *
     * @param data data
     * @return copy of data
     */
    protected abstract fun getCopy(data: T): T

    /**
     * Returns account data.
     *
     * @param account account
     * @param id      ID
     * @return account data
     */
    protected abstract fun getAccountData(account: Account, id: Int): Optional<T>

    /**
     * Returns list of data.
     *
     * @param data data
     * @return list of data
     */
    protected abstract fun getDataList(data: T): List<T>

    /**
     * Moves data in list one position up or down.
     *
     * @param data data
     * @param up   if moving data up
     * @throws IllegalArgumentException if data is null
     */
    private fun move(data: T, up: Boolean) {
        if (data is Movable) {
            val dataList = getDataList(data)
                .sorted()
                .toMutableList()
            val index = dataList.indexOf(data)
            val other = dataList[if (up) index - 1 else index + 1]
            val position = data.position!!
            data.position = (other as Movable).position
            (other as Movable).position = position
            repository.saveAll(listOf(data, other))
        }
    }

}
