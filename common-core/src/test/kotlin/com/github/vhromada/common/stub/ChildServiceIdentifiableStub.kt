package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.service.AbstractChildService
import com.github.vhromada.common.service.ChildService
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * A class represents stub for [ChildService] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
class ChildServiceIdentifiableStub(
    repository: JpaRepository<Identifiable, Int>,
    accountProvider: AccountProvider,
    private val copy: () -> Identifiable,
    private val data: (id: Int) -> Optional<Identifiable>,
    private val dataList: () -> List<Identifiable>,
    private val parent: () -> Identifiable,
    private val parentData: () -> List<Identifiable>
) : AbstractChildService<Identifiable, Identifiable>(repository = repository, accountProvider = accountProvider) {

    override fun remove(data: Identifiable) {
        repository.delete(data)
    }

    override fun getCopy(data: Identifiable): Identifiable {
        return copy.invoke()
    }

    override fun getAccountData(account: Account, id: Int): Optional<Identifiable> {
        return data.invoke(id)
    }

    override fun findByParent(parent: Int): List<Identifiable> {
        return parentData.invoke()
    }

    override fun getParent(data: Identifiable): Identifiable {
        return parent.invoke()
    }

    override fun getAccountDataList(account: Account, parent: Int): List<Identifiable> {
        return dataList.invoke()
    }

}
