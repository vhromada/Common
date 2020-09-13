package com.github.vhromada.common.account.validator

import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.result.Result

/**
 * An interface represents validator for role.
 *
 * @author Vladimir Hromada
 */
interface RoleValidator {

    /**
     * Validates updating roles.
     * <br></br>
     * Validation errors:
     *
     *  * Updating roles are null
     *  * Updating roles contains null
     *  * Role doesn't exist in data storage
     *
     * @param roles validating roles
     * @return result with validation errors
     */
    fun validateUpdateRoles(roles: UpdateRoles?): Result<Unit>

}