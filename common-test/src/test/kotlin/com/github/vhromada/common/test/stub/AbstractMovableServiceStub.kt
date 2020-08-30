package com.github.vhromada.common.test.stub

import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.TimeProvider
import com.github.vhromada.common.service.AbstractMovableService
import org.springframework.cache.Cache
import org.springframework.data.jpa.repository.JpaRepository

/**
 * A class represents stub for [AbstractMovableService].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableServiceStub(
        repository: JpaRepository<AuditEntity, Int>,
        accountProvider: AccountProvider,
        timeProvider: TimeProvider,
        cache: Cache,
        key: String,
        private val copy: () -> AuditEntity,
        private val data: () -> List<AuditEntity>) : AbstractMovableService<AuditEntity>(repository, accountProvider, timeProvider, cache, key) {

    override fun getAccountData(account: Account): List<AuditEntity> {
        return data.invoke()
    }

    override fun getCopy(data: AuditEntity): AuditEntity {
        return copy.invoke()
    }

}
