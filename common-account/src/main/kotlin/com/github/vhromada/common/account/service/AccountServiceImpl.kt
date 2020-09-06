package com.github.vhromada.common.account.service

import com.github.vhromada.common.account.domain.Account
import com.github.vhromada.common.account.repository.AccountRepository
import com.github.vhromada.common.mapper.Mapper
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
        private val repository: AccountRepository,
        private val mapper: Mapper<Account, com.github.vhromada.common.entity.Account>) : AccountService {

    override fun get(id: Int): Optional<Account> {
        return repository.findById(id)
    }

    @Transactional
    override fun add(account: Account) {
        repository.save(account)
    }

    @Transactional
    override fun update(account: Account) {
        repository.save(account)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val account = repository.findByUsername(username)
        if (!account.isPresent) {
            throw UsernameNotFoundException("No account found for username $username")
        }
        return mapper.map(account.get())
    }

}
