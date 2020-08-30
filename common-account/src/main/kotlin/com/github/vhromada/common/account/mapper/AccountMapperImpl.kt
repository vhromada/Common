package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.domain.Account
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for account.
 *
 * @author Vladimir Hromada
 */
@Component("accountMapper")
class AccountMapperImpl : AccountMapper {

    override fun map(source: Account): com.github.vhromada.common.entity.Account {
        return com.github.vhromada.common.entity.Account(
                id = source.id!!,
                username = source.username,
                password = source.password,
                roles = source.roles.map { it.name })
    }

}
