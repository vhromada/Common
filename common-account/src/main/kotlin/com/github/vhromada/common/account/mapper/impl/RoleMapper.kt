package com.github.vhromada.common.account.mapper.impl

import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.mapper.Mapper
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper forrole.
 *
 * @author Vladimir Hromada
 */
@Component("roleMapper")
class RoleMapper : Mapper<Role, String> {

    override fun map(source: Role): String {
        return source.name
    }

    override fun mapBack(source: String): Role {
        return Role(id = null, name = source)
    }

}