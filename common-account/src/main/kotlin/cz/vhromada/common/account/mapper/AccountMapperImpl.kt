package cz.vhromada.common.account.mapper

import cz.vhromada.common.account.domain.Account
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for account.
 *
 * @author Vladimir Hromada
 */
@Component("accountMapper")
class AccountMapperImpl : AccountMapper {

    override fun map(source: Account): cz.vhromada.common.entity.Account {
        return cz.vhromada.common.entity.Account(
                id = source.id!!,
                username = source.username,
                password = source.password,
                roles = source.roles.map { it.name })
    }

}
