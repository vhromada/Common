package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.domain.Account

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
    fun map(source: Account): com.github.vhromada.common.entity.Account

}
