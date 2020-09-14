package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Result

/**
 * An interface represents facade for roles.
 *
 * @author Vladimir Hromada
 */
interface RoleFacade {

    /**
     * Returns list of roles.
     *
     * @return result with list of roles
     */
    fun getAll(): Result<List<String>>

    /**
     * Updates roles.
     * <br></br>
     * Validation errors:
     *
     *  * Account ID is null
     *  * Account doesn't exist in data storage
     *  * Roles are null
     *  * Roles contains null
     *  * Role doesn't exist in data storage
     *
     * @param account account
     * @param roles   roles
     * @return result with validation errors
     */
    fun updateRoles(account: Account, roles: UpdateRoles): Result<Unit>

}
