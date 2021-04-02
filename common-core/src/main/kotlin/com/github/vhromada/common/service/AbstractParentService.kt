package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.utils.sorted
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

/**
 * An abstract class represents service for parent data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
abstract class AbstractParentService<T : Identifiable>(
    repository: JpaRepository<T, Int>,
    accountProvider: AccountProvider
) : AbstractService<T>(repository = repository, accountProvider = accountProvider), ParentService<T> {

    @Transactional
    override fun remove(data: T) {
        repository.delete(data)
    }

    @Transactional
    override fun newData() {
        repository.deleteAll(getDataList())
    }

    override fun getAll(): List<T> {
        return getDataList()
            .sorted()
    }

    @Transactional
    override fun updatePositions() {
        val data = getDataList()
        for (i in data.indices) {
            val item = data[i]
            if (item is Movable) {
                item.updatePosition(i)
            }
        }
        repository.saveAll(data)
    }

    final override fun getDataList(data: T): List<T> {
        return getDataList()
    }

    /**
     * Returns account list of data.
     *
     * @param account account
     * @return account list of data
     */
    protected abstract fun getAccountDataList(account: Account): List<T>

    /**
     * Returns list of data.
     *
     * @return list of data
     */
    private fun getDataList(): List<T> {
        val account = accountProvider.getAccount()
        if (account.roles!!.contains("ROLE_ADMIN")) {
            return repository.findAll()
        }
        return getAccountDataList(account)
    }

}
