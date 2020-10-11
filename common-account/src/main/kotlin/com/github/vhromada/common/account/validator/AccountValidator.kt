package com.github.vhromada.common.account.validator

import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Result

/**
 * An interface represents validator for account.
 *
 * @author Vladimir Hromada
 */
interface AccountValidator {

    /**
     * Validates new account.
     * <br></br>
     * Validation errors:
     *
     *  * Account is null
     *  * ID isn't null
     *  * UUID isn't null
     *  * Username is null
     *  * Password is null
     *  * Role doesn't exist in data storage
     *  * Username exists in data storage
     *
     * @param account validating account
     * @return result with validation errors
     */
    fun validateNew(account: Account?): Result<Unit>

    /**
     * Validates updating account.
     * <br></br>
     * Validation errors:
     *
     *  * Account is null
     *  * ID is null
     *  * UUID is null
     *  * Username is null
     *  * Password is null
     *  * Account doesn't exist in data storage
     *  * Role doesn't exist in data storage
     *  * Username exists in data storage
     *
     * @param account validating account
     * @return result with validation errors
     */
    fun validateUpdate(account: Account?): Result<Unit>

    /**
     * Validates existing account.
     * <br></br>
     * Validation errors:
     *
     *  * Account is null
     *  * ID is null
     *  * Account doesn't exist in data storage
     *
     * @param account validating account
     * @return result with validation errors
     */
    fun validateExist(account: Account?): Result<Unit>

}
