package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.entity.Credentials
import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Result

/**
 * An interface represents facade for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountFacade {

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
     *
     * @param credentials credentials
     * @return result with validation errors
     */
    fun update(credentials: Credentials): Result<Unit>

    /**
     * Updates roles.
     * <br></br>
     * Validation errors:
     *
     *  * Roles are null
     *  * Roles contains null
     *  * Role doesn't exist in data storage
     *
     * @param roles roles
     * @return result with validation errors
     */
    fun updateRoles(roles: UpdateRoles): Result<Unit>

}