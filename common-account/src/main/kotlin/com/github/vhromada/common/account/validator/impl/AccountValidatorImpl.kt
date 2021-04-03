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
class AccountValidatorImpl(
    private val accountService: AccountService,
    private val roleRepository: RoleRepository
) : AccountValidator {

    override fun validateNew(account: Account?): Result<Unit> {
        return validate(account = account, ValidationType.NEW)
    }

    override fun validateUpdate(account: Account?): Result<Unit> {
        return validate(account = account, ValidationType.EXISTS, ValidationType.UPDATE)
    }

    override fun validateExist(account: Account?): Result<Unit> {
        return validate(account = account, ValidationType.EXISTS)
    }

    /**
     * Validates account.
     *
     * @param account         validating account
     * @param validationTypes types of validation
     * @return result with validation errors
     */
    private fun validate(account: Account?, vararg validationTypes: ValidationType): Result<Unit> {
        if (account == null) {
            return Result.error(key = "ACCOUNT_NULL", message = "Account mustn't be null.")
        }

        val result = Result<Unit>()
        if (validationTypes.contains(ValidationType.NEW)) {
            if (account.id != null) {
                result.addEvent(Event(severity = Severity.ERROR, key = "ACCOUNT_ID_NOT_NULL", message = "ID must be null."))
            }
            if (account.uuid != null) {
                result.addEvent(Event(severity = Severity.ERROR, key = "ACCOUNT_UUID_NOT_NULL", message = "UUID must be null."))
            }
            validateDeep(account = account, result = result)
        }
        if (validationTypes.contains(ValidationType.UPDATE)) {
            if (account.uuid == null) {
                result.addEvent(Event(severity = Severity.ERROR, key = "ACCOUNT_UUID_NULL", message = "UUID mustn't be null."))
            }
            validateDeep(account = account, result = result)
        }
        if (validationTypes.contains(ValidationType.EXISTS)) {
            if (account.id == null) {
                result.addEvent(Event(severity = Severity.ERROR, key = "ACCOUNT_ID_NULL", message = "ID mustn't be null."))
            } else if (accountService.get(account.id!!).isEmpty) {
                result.addEvent(Event(severity = Severity.ERROR, key = "ACCOUNT_NOT_EXIST", message = "Account doesn't exist."))
            }
        }
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
     *  * Username exists in data storage
     *
     * @param account   validating account
     * @param result result with validation errors
     */
    private fun validateDeep(account: Account, result: Result<Unit>) {
        if (account.username == null) {
            result.addEvent(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_NULL", message = "Username mustn't be null."))
        } else if (hasDifferentUsername(account = account)) {
            val storedAccount = accountService.findByUsername(username = account.username!!)
            if (storedAccount.isPresent) {
                result.addEvent(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_ALREADY_EXIST", message = "Username already exists."))
            }
        }
        if (account.password == null) {
            result.addEvent(Event(severity = Severity.ERROR, key = "ACCOUNT_PASSWORD_NULL", message = "Password mustn't be null."))
        }
        if (account.roles != null) {
            account.roles!!.forEach {
                if (roleRepository.findByName(name = it).isEmpty) {
                    result.addEvent(Event(severity = Severity.ERROR, key = "ROLE_NOT_EXIST", message = "Role doesn't exist."))
                }
            }
        }
    }

    /**
     * Returns true if account has different username than stored data.
     *
     * @param account account
     * @return true if account has different username than stored data
     */
    private fun hasDifferentUsername(account: Account): Boolean {
        if (account.id == null || account.username == null) {
            return true
        }
        val storedAccount = accountService.get(id = account.id!!)
        if (storedAccount.isEmpty) {
            return true
        }
        return !account.username.equals(storedAccount.get().username)
    }

    /**
     * An enum represents type of validation.
     */
    enum class ValidationType {

        /**
         * New entity validation
         */
        NEW,

        /**
         * Update entity validation
         */
        UPDATE,

        /**
         * Existing entity validation
         */
        EXISTS

    }

}
