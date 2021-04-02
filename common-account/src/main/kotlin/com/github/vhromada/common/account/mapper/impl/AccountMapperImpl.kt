package com.github.vhromada.common.account.mapper.impl

import com.github.vhromada.common.account.domain.Account
import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.entity.Credentials
import com.github.vhromada.common.account.mapper.AccountMapper
import com.github.vhromada.common.mapper.Mapper
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for account.
 *
 * @author Vladimir Hromada
 */
@Component("accountMapper")
class AccountMapperImpl(private val roleMapper: Mapper<Role, String>) : AccountMapper {

    override fun map(source: Account): com.github.vhromada.common.entity.Account {
        return com.github.vhromada.common.entity.Account(
            id = source.id!!,
            uuid = source.uuid,
            username = source.username,
            password = source.password,
            roles = roleMapper.map(source.roles)
        )
    }

    override fun mapBack(source: com.github.vhromada.common.entity.Account): Account {
        return Account(
            id = source.id,
            uuid = source.uuid,
            username = source.username!!,
            password = source.password!!,
            roles = roleMapper.mapBack(source.roles!!)
        )
    }

    override fun mapCredentials(source: Credentials): com.github.vhromada.common.entity.Account {
        return com.github.vhromada.common.entity.Account(
            id = null,
            uuid = null,
            username = source.username,
            password = source.password,
            roles = null
        )
    }

}
