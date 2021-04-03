package com.github.vhromada.common.account.facade.impl

import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.entity.Credentials
import com.github.vhromada.common.account.facade.AccountFacade
import com.github.vhromada.common.account.mapper.AccountMapper
import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.validator.AccountValidator
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.UuidProvider
import com.github.vhromada.common.result.Result
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.Optional

/**
 * A class represents implementation of facade for accounts.
 *
 * @author Vladimir Hromada
 */
@Component("accountFacade")
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class AccountFacadeImpl(
    private val accountService: AccountService,
    private val roleRepository: RoleRepository,
    private val accountMapper: AccountMapper,
    private val accountValidator: AccountValidator,
    private val passwordEncoder: PasswordEncoder,
    private val accountProvider: AccountProvider,
    private val uuidProvider: UuidProvider
) : AccountFacade {

    override fun getAll(): Result<List<Account>> {
        return Result.of(data = accountMapper.map(source = accountService.getAll()))
    }

    override fun get(id: Int): Result<Account> {
        val item = accountService.get(id = id)
        if (item.isPresent) {
            return Result.of(data = accountMapper.map(source = item.get()))
        }
        return Result()
    }

    override fun add(account: Account): Result<Unit> {
        val result = accountValidator.validateNew(account = account)
        if (result.isOk()) {
            accountService.add(account = getForAdd(account = account))
        }
        return result
    }

    override fun add(credentials: Credentials): Result<Unit> {
        val account = accountMapper.mapCredentials(source = credentials)
        return add(account = account)
    }

    override fun update(account: Account): Result<Unit> {
        val result = accountValidator.validateUpdate(account = account)
        if (result.isOk()) {
            accountService.update(account = getForUpdate(account = account))
        }
        return result
    }

    override fun update(credentials: Credentials): Result<Unit> {
        val account = accountProvider.getAccount()
            .copy(username = credentials.username, password = credentials.password)
        return update(account = account)
    }

    override fun findByUsername(username: String): Optional<Account> {
        return accountService.findByUsername(username = username)
            .map { accountMapper.map(source = it) }
    }

    /**
     * Returns account for adding.
     *
     * @param account account
     * @return account for adding
     */
    private fun getForAdd(account: Account): com.github.vhromada.common.account.domain.Account {
        val domainAccount = accountMapper.mapBack(source = account.copy(roles = account.roles ?: emptyList()))
        return domainAccount.copy(uuid = uuidProvider.getUuid(), password = getEncodedPassword(password = domainAccount.password), roles = mapRoles(roles = account.roles))
    }

    /**
     * Returns account for updating.
     *
     * @param account account
     * @return account for updating
     */
    private fun getForUpdate(account: Account): com.github.vhromada.common.account.domain.Account {
        val domainAccount = accountMapper.mapBack(source = account)
        return domainAccount.copy(password = getEncodedPassword(password = domainAccount.password), roles = mapRoles(roles = account.roles))
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
        return values.map { roleRepository.findByName(name = it).get() }
    }

}
