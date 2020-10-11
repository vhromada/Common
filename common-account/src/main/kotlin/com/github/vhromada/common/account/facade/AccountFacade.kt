package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.entity.Credentials
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Result
import java.util.Optional

/**
 * An interface represents facade for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountFacade {

    /**
     * Returns list of accounts.
     *
     * @return result with list of accounts
     */
    fun getAll(): Result<List<Account>>

    /**
     * Returns account with ID or null if there isn't such data.
     *
     * @param id ID
     * @return result with account or validation errors
     */
    fun get(id: Int): Result<Account>

    /**
     * Adds account. Sets new ID and UUID.
     * <br></br>
     * Validation errors:
     *
     *  * ID isn't null
     *  * UUID isn't null
     *  * Username is null
     *  * Password is null
     *  * Role doesn't exist in data storage
     *  * Username exists in data storage
     *
     * @param account account
     * @return result with validation errors
     */
    fun add(account: Account): Result<Unit>

    /**
     * Adds account.
     * <br></br>
     * Validation errors:
     *
     *  * Username is null
     *  * Password is null
     *  * Username exists in data storage
     *
     * @param credentials credentials
     * @return result with validation errors
     */
    fun add(credentials: Credentials): Result<Unit>

    /**
     * Updates account.
     * <br></br>
     * Validation errors:
     *
     *  * ID is null
     *  * UUID is null
     *  * Username is null
     *  * Password is null
     *  * Account doesn't exist in data storage
     *  * Role doesn't exist in data storage
     *  * Username exists in data storage
     *
     * @param account new value of account
     * @return result with validation errors
     */
    fun update(account: Account): Result<Unit>

    /**
     * Updates account.
     * <br></br>
     * Validation errors:
     *
     *  * Username is null
     *  * Password is null
     *  * Username exists in data storage
     *
     * @param credentials credentials
     * @return result with validation errors
     */
    fun update(credentials: Credentials): Result<Unit>

    /**
     * Find account by username.
     *
     * @param username username
     * @return account
     */
    fun findByUsername(username: String): Optional<Account>

}