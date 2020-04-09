package cz.vhromada.common.account.mapper

import cz.vhromada.common.account.domain.Account

/**
 * An interface represents mapper for account.
 *
 * @author Vladimir Hromada
 */
interface AccountMapper {

    /**
     * Maps account.
     *
     * @param source source account
     * @return mapped account
     */
    fun map(source: Account): cz.vhromada.common.entity.Account

}
