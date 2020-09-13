package com.github.vhromada.common.account.validator.impl

import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.validator.AccountValidator
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Severity
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for account.
 *
 * @author Vladimir Hromada
 */
@Component("accountValidator")
class AccountValidatorImpl(private val accountService: AccountService,
                           private val roleRepository: RoleRepository) : AccountValidator {

    override fun validateNew(account: Account?): Result<Unit> {
        if (account == null) {
            return Result.error("ACCOUNT_NULL", "Account mustn't be null.")
        }
        val result = Result<Unit>()
        if (account.id != null) {
            result.addEvent(Event(Severity.ERROR, "ACCOUNT_ID_NOT_NULL", "ID must be null."))
        }
        if (account.uuid != null) {
            result.addEvent(Event(Severity.ERROR, "ACCOUNT_UUID_NOT_NULL", "UUID must be null."))
        }
        validateDeep(account, result)
        return result
    }

    override fun validateExist(account: Account?): Result<Unit> {
        if (account == null) {
            return Result.error("ACCOUNT_NULL", "Account mustn't be null.")
        }
        val result = Result<Unit>()
        if (account.id == null) {
            result.addEvent(Event(Severity.ERROR, "ACCOUNT_ID_NULL", "ID mustn't be null."))
        } else if (accountService.get(account.id!!).isEmpty) {
            result.addEvent(Event(Severity.ERROR, "ACCOUNT_NOT_EXIST", "Account doesn't exist."))
        }
        if (account.uuid == null) {
            result.addEvent(Event(Severity.ERROR, "ACCOUNT_UUID_NULL", "UUID mustn't be null."))
        }
        validateDeep(account, result)
        return result
    }

    /**
     * Validates account deeply.
     * <br></br>
     * Validation errors:
     *
     *  * Username is null
     *  * Password is null
     *  * Role doesn't exist in data storage
     *
     * @param account   validating account
     * @param result result with validation errors
     */
    private fun validateDeep(account: Account, result: Result<Unit>) {
        if (account.username == null) {
            result.addEvent(Event(Severity.ERROR, "ACCOUNT_USERNAME_NULL", "Username mustn't be null."))
        }
        if (account.password == null) {
            result.addEvent(Event(Severity.ERROR, "ACCOUNT_PASSWORD_NULL", "Password mustn't be null."))
        }
        if (account.roles != null) {
            account.roles!!.forEach {
                if (roleRepository.findByName(it).isEmpty) {
                    result.addEvent(Event(Severity.ERROR, "ROLE_NOT_EXIST", "Role doesn't exist."))
                }
            }
        }
    }

}
