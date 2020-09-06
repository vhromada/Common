package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.domain.Account
import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.mapper.Mapper
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for account.
 *
 * @author Vladimir Hromada
 */
@Component("accountMapper")
class AccountMapper : Mapper<Account, com.github.vhromada.common.entity.Account> {

    override fun map(source: Account): com.github.vhromada.common.entity.Account {
        return com.github.vhromada.common.entity.Account(
                id = source.id!!,
                uuid = source.uuid,
                username = source.username,
                password = source.password,
                roles = source.roles.map { it.name })
    }

    override fun mapBack(source: com.github.vhromada.common.entity.Account): Account {
        return Account(
                id = source.id,
                uuid = source.uuid,
                username = source.username!!,
                password = source.password!!,
                roles = source.roles!!.map { Role(id = null, name = it) })
    }

}
