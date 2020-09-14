package com.github.vhromada.common.account.service

import com.github.vhromada.common.account.domain.Account
import com.github.vhromada.common.account.mapper.AccountMapper
import com.github.vhromada.common.account.repository.AccountRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * A class represents implementation of service for accounts.
 *
 * @author Vladimir Hromada
 */
@Service("accountService")
class AccountServiceImpl(
        private val accountRepository: AccountRepository,
        private val accountMapper: AccountMapper) : AccountService {

    override fun getAll(): List<Account> {
        return accountRepository.findAll()
    }

    override fun get(id: Int): Optional<Account> {
        return accountRepository.findById(id)
    }

    @Transactional
    override fun add(account: Account) {
        accountRepository.save(account)
    }

    @Transactional
    override fun update(account: Account) {
        accountRepository.save(account)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val account = accountRepository.findByUsername(username)
        if (!account.isPresent) {
            throw UsernameNotFoundException("No account found for username $username")
        }
        return accountMapper.map(account.get())
    }

}
