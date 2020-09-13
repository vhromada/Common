package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.entity.Credentials
import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.account.mapper.AccountMapper
import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.validator.AccountValidator
import com.github.vhromada.common.account.validator.RoleValidator
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.provider.AccountProvider
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
class AccountFacadeImpl(private val accountService: AccountService,
                        private val roleRepository: RoleRepository,
                        private val accountMapper: AccountMapper,
                        private val accountValidator: AccountValidator,
                        private val roleValidator: RoleValidator,
                        private val passwordEncoder: PasswordEncoder,
                        private val accountProvider: AccountProvider,
                        private val uuidProvider: UuidProvider) : AccountFacade {

    override fun add(account: Account): Result<Unit> {
        val result = accountValidator.validateNew(account)
        if (result.isOk()) {
            accountService.add(getForAdd(account))
        }
        return result
    }

    override fun add(credentials: Credentials): Result<Unit> {
        val account = accountMapper.mapCredentials(credentials)
        return add(account)
    }

    override fun update(account: Account): Result<Unit> {
        val result = accountValidator.validateExist(account)
        if (result.isOk()) {
            accountService.update(getForUpdate(account))
        }
        return result
    }

    override fun update(credentials: Credentials): Result<Unit> {
        val account = accountProvider.getAccount()
                .copy(username = credentials.username, password = credentials.password)
        return update(account)
    }

    override fun updateRoles(roles: UpdateRoles): Result<Unit> {
        val result = roleValidator.validateUpdateRoles(roles)
        if (result.isError()) {
            return result
        }
        val account = accountProvider.getAccount()
        val domainAccount = accountMapper.mapBack(account)
                .copy(roles = mapRoles(roles.roles!!.filterNotNull()))
        accountService.update(domainAccount)
        return result
    }

    /**
     * Returns account for adding.
     *
     * @param account account
     * @return account for adding
     */
    private fun getForAdd(account: Account): com.github.vhromada.common.account.domain.Account {
        val domainAccount = accountMapper.mapBack(account)
        return domainAccount.copy(uuid = uuidProvider.getUuid(), password = getEncodedPassword(domainAccount.password), roles = mapRoles(account.roles))
    }

    /**
     * Returns account for updating.
     *
     * @param account account
     * @return account for updating
     */
    private fun getForUpdate(account: Account): com.github.vhromada.common.account.domain.Account {
        val domainAccount = accountMapper.mapBack(account)
        return domainAccount.copy(password = getEncodedPassword(domainAccount.password), roles = mapRoles(account.roles))
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
     * Returns converted roles.
     *
     * @param roles roles
     * @return converted roles
     */
    private fun mapRoles(roles: List<String>?): List<Role> {
        val values = roles ?: listOf("ROLE_USER")
        return values.map { roleRepository.findByName(it).get() }
    }

}
