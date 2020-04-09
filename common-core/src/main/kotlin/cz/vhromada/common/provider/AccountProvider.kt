package cz.vhromada.common.provider

import cz.vhromada.common.entity.Account

/**
 * An interface represents provider for account.
 *
 * @author Vladimir Hromada
 */
interface AccountProvider {

    /**
     * Returns account.
     *
     * @return account
     */
    fun getAccount(): Account

}
