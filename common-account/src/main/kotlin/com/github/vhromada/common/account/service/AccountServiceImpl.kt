package com.github.vhromada.common.account.service

import com.github.vhromada.common.account.mapper.AccountMapper
import com.github.vhromada.common.account.repository.AccountRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * A class represents implementation of service for accounts.
 *
 * @author Vladimir Hromada
 */
@Service("accountService")
class AccountServiceImpl(
        private val repository: AccountRepository,
        private val mapper: AccountMapper) : AccountService {

    override fun loadUserByUsername(username: String): UserDetails {
        val account = repository.findByUsername(username)
        if (!account.isPresent) {
            throw UsernameNotFoundException("No account found for username $username")
        }
        return mapper.map(account.get())
    }

}
