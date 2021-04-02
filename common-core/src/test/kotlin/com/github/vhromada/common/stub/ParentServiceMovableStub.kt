package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.service.AbstractParentService
import com.github.vhromada.common.service.ParentService
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * A class represents stub for [ParentService] for [Movable].
 *
 * @author Vladimir Hromada
 */
class ParentServiceMovableStub(
    repository: JpaRepository<Movable, Int>,
    accountProvider: AccountProvider,
    private val copy: () -> Movable,
    private val data: (id: Int) -> Optional<Movable>,
    private val dataList: () -> List<Movable>
) : AbstractParentService<Movable>(repository = repository, accountProvider = accountProvider) {

    override fun getCopy(data: Movable): Movable {
        return copy.invoke()
    }

    override fun getAccountData(account: Account, id: Int): Optional<Movable> {
        return data.invoke(id)
    }

    override fun getAccountDataList(account: Account): List<Movable> {
        return dataList.invoke()
    }

}
