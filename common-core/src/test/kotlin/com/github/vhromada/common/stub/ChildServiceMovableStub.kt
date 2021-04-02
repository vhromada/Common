package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.service.AbstractChildService
import com.github.vhromada.common.service.ChildService
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * A class represents stub for [ChildService] for [Movable].
 *
 * @author Vladimir Hromada
 */
class ChildServiceMovableStub(
    repository: JpaRepository<Movable, Int>,
    accountProvider: AccountProvider,
    private val copy: () -> Movable,
    private val data: (id: Int) -> Optional<Movable>,
    private val dataList: () -> List<Movable>,
    private val parent: () -> Movable,
    private val parentData: () -> List<Movable>
) : AbstractChildService<Movable, Movable>(repository = repository, accountProvider = accountProvider) {

    override fun remove(data: Movable) {
        repository.delete(data)
    }

    override fun getCopy(data: Movable): Movable {
        return copy.invoke()
    }

    override fun getAccountData(account: Account, id: Int): Optional<Movable> {
        return data.invoke(id)
    }

    override fun findByParent(parent: Int): List<Movable> {
        return parentData.invoke()
    }

    override fun getParent(data: Movable): Movable {
        return parent.invoke()
    }

    override fun getAccountDataList(account: Account, parent: Int): List<Movable> {
        return dataList.invoke()
    }

}
