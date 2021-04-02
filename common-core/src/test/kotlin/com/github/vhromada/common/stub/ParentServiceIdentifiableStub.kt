package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.entity.Identifiable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.service.AbstractParentService
import com.github.vhromada.common.service.ParentService
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * A class represents stub for [ParentService] for [Identifiable].
 *
 * @author Vladimir Hromada
 */
class ParentServiceIdentifiableStub(
    repository: JpaRepository<Identifiable, Int>,
    accountProvider: AccountProvider,
    private val copy: () -> Identifiable,
    private val data: (id: Int) -> Optional<Identifiable>,
    private val dataList: () -> List<Identifiable>
) : AbstractParentService<Identifiable>(repository = repository, accountProvider = accountProvider) {

    override fun getCopy(data: Identifiable): Identifiable {
        return copy.invoke()
    }

    override fun getAccountData(account: Account, id: Int): Optional<Identifiable> {
        return data.invoke(id)
    }

    override fun getAccountDataList(account: Account): List<Identifiable> {
        return dataList.invoke()
    }

}
