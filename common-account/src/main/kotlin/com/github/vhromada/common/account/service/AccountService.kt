package com.github.vhromada.common.account.service

import com.github.vhromada.common.account.domain.Account
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.Optional

/**
 * An interface represents service for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountService : UserDetailsService {

    /**
     * Returns list of accounts.
     *
     * @return list of accounts
     */
    fun getAll(): List<Account>

    /**
     * Returns account with ID.
     *
     * @param id ID
     * @return account with ID
     */
    fun get(id: Int): Optional<Account>

    /**
     * Adds account. Sets new ID.
     *
     * @param account data
     */
    fun add(account: Account)

    /**
     * Updates account.
     *
     * @param account new value of account
     */
    fun update(account: Account)

}
