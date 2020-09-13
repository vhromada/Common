package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.domain.Account
import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.entity.Credentials
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

    override fun mapCredentials(source: Credentials): com.github.vhromada.common.entity.Account {
        return com.github.vhromada.common.entity.Account(
                id = null,
                uuid = null,
                username = source.username,
                password = source.password,
                roles = null)
    }

}
