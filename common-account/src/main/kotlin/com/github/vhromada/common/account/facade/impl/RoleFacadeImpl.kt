package com.github.vhromada.common.account.facade.impl

import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.account.facade.RoleFacade
import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.validator.AccountValidator
import com.github.vhromada.common.account.validator.RoleValidator
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for roles.
 *
 * @author Vladimir Hromada
 */
@Component("roleFacade")
class RoleFacadeImpl(private val accountService: AccountService,
                     private val roleRepository: RoleRepository,
                     private val roleMapper: Mapper<Role, String>,
                     private val accountValidator: AccountValidator,
                     private val roleValidator: RoleValidator) : RoleFacade {

    override fun getAll(): Result<List<String>> {
        return Result.of(roleMapper.map(roleRepository.findAll()))
    }

    override fun updateRoles(account: Account, roles: UpdateRoles): Result<Unit> {
        val result = accountValidator.validateExist(account)
        if (result.isError()) {
            return result
        }
        result.addEvents(roleValidator.validateUpdateRoles(roles).events())
        if (result.isError()) {
            return result
        }
        val domainAccount = accountService.get(account.id!!)
                .map { it.copy(roles = mapRoles(roles.roles)) }
                .get()
        accountService.update(domainAccount)
        return result
    }

    /**
     * Returns roles.
     *
     * @param roles roles
     * @return converted roles
     */
    private fun mapRoles(roles: List<String?>?): List<Role> {
        return roles!!
                .filterNotNull()
                .map { roleRepository.findByName(it).get() }
    }

}
