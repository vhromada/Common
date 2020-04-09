package cz.vhromada.common.test.stub

import cz.vhromada.common.domain.AuditEntity
import cz.vhromada.common.entity.Account
import cz.vhromada.common.provider.AccountProvider
import cz.vhromada.common.provider.TimeProvider
import cz.vhromada.common.service.AbstractMovableService
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
