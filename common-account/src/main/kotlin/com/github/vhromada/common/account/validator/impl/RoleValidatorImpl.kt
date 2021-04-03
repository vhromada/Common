package com.github.vhromada.common.account.validator.impl

import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.validator.RoleValidator
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Severity
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for role.
 *
 * @author Vladimir Hromada
 */
@Component("roleValidator")
class RoleValidatorImpl(private val roleRepository: RoleRepository) : RoleValidator {

    override fun validateUpdateRoles(roles: UpdateRoles?): Result<Unit> {
        if (roles?.roles == null) {
            return Result.error(key = "ROLES_NULL", message = "Roles mustn't be null.")
        }
        if (roles.roles.contains(null)) {
            return Result.error(key = "ROLES_CONTAIN_NULL", message = "Roles mustn't contain null value.")
        }
        val result = Result<Unit>()
        roles.roles.forEach {
            if (roleRepository.findByName(it!!).isEmpty) {
                result.addEvent(Event(severity = Severity.ERROR, key = "ROLE_NOT_EXIST", message = "Role doesn't exist."))
            }
        }
        return result
    }

}
