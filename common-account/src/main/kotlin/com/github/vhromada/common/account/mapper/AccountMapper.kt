package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.domain.Account
import com.github.vhromada.common.account.entity.Credentials
import com.github.vhromada.common.mapper.Mapper

/**
 * An interface represents mapper for account.
 *
 * @author Vladimir Hromada
 */
interface AccountMapper : Mapper<Account, com.github.vhromada.common.entity.Account> {

    /**
     * Maps credentials.
     *
     * @param source credentials
     * @return converted account
     */
    fun mapCredentials(source: Credentials): com.github.vhromada.common.entity.Account

}
