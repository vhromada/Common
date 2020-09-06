package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.validator.AccountValidator
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.provider.UuidProvider
import com.github.vhromada.common.result.Result
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for accounts.
 *
 * @author Vladimir Hromada
 */
@Component("accountFacade")
class AccountFacadeImpl(private val service: AccountService,
                        private val repository: RoleRepository,
                        private val mapper: Mapper<com.github.vhromada.common.account.domain.Account, Account>,
                        private val validator: AccountValidator,
                        private val passwordEncoder: PasswordEncoder,
                        private val uuidProvider: UuidProvider) : AccountFacade {

    override fun add(account: Account): Result<Unit> {
        val result = validator.validateNew(account)
        if (result.isOk()) {
            service.add(getForAdd(account))
        }
        return result
    }

    override fun update(account: Account): Result<Unit> {
        val result = validator.validateExist(account)
        if (result.isOk()) {
            service.update(getForUpdate(account))
        }
        return result
    }

    /**
     * Returns account for adding.
     *
     * @param account account
     * @return account for adding
     */
    private fun getForAdd(account: Account): com.github.vhromada.common.account.domain.Account {
        val domainAccount = mapper.mapBack(account)
        return domainAccount.copy(uuid = uuidProvider.getUuid(), password = getEncodedPassword(domainAccount.password), roles = getRoles(account))
    }

    /**
     * Returns account for updating.
     *
     * @param account account
     * @return account for updating
     */
    private fun getForUpdate(account: Account): com.github.vhromada.common.account.domain.Account {
        val domainAccount = mapper.mapBack(account)
        return domainAccount.copy(password = getEncodedPassword(domainAccount.password), roles = getRoles(account))
    }

    /**
     * Returns encoded password.
     *
     * @param password password
     * @return encoded password
     */
    private fun getEncodedPassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    /**
     * Returns roles.
     *
     * @param account account
     * @return roles
     */
    private fun getRoles(account: Account): List<Role> {
        val roles = account.roles ?: listOf("ROLE_USER")
        return roles.map { repository.findByName(it).get() }
    }

}
