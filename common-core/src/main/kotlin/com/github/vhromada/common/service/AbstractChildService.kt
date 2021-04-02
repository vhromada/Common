package com.github.vhromada.common.service

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.utils.sorted
import org.springframework.data.jpa.repository.JpaRepository

/**
 * An abstract class represents service for child data.
 *
 * @param <T> type of child data
 * @param <U> type of parent data
 * @author Vladimir Hromada
 */
abstract class AbstractChildService<T : Identifiable, U : Identifiable>(
    repository: JpaRepository<T, Int>,
    accountProvider: AccountProvider
) : AbstractService<T>(repository = repository, accountProvider = accountProvider), ChildService<T> {

    override fun find(parent: Int): List<T> {
        return getParentDataList(parent)
            .sorted()
    }

    final override fun getDataList(data: T): List<T> {
        val parent = getParent(data)
        return getParentDataList(parent.id!!)
    }

    /**
     * Returns list of data for specified parent.
     *
     * @param parent parent's ID
     * @return list of data for specified parent
     */
    protected abstract fun findByParent(parent: Int): List<T>

    /**
     * Returns parent.
     *
     * @param data data
     * @return parent
     */
    protected abstract fun getParent(data: T): U

    /**
     * Returns account list of data.
     *
     * @param account account
     * @param parent  parent
     * @return account list of data
     */
    protected abstract fun getAccountDataList(account: Account, parent: Int): List<T>

    /**
     * Returns list of data for parent.
     *
     * @param parent parent's ID
     * @return list of data for parent
     */
    private fun getParentDataList(parent: Int): List<T> {
        val account = accountProvider.getAccount()
        if (account.roles!!.contains("ROLE_ADMIN")) {
            return findByParent(parent)
        }
        return getAccountDataList(account = account, parent = parent)
    }

}
