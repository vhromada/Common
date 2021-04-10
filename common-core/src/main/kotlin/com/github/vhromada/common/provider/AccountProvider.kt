package com.github.vhromada.common.provider

import com.github.vhromada.common.entity.Account

/**
 * An interface represents provider for account.
 *
 * @author Vladimir Hromada
 */
@Suppress("unused")
interface AccountProvider {

    /**
     * Returns account.
     *
     * @return account
     */
    fun getAccount(): Account

}
