package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.utils.RoleUtils
import com.github.vhromada.common.mapper.Mapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * A class represents test for mapper between [Role] and [String].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AccountTestConfiguration::class])
class RoleMapperIntegrationTest {

    /**
     * Instance of [Mapper]
     */
    @Autowired
    private lateinit var mapper: Mapper<Role, String>

    /**
     * Test method for [Mapper.map].
     */
    @Test
    fun map() {
        val roleDomain = RoleUtils.getRole(index = 1)
        val role = mapper.map(source = roleDomain)

        RoleUtils.assertRoleDeepEquals(expected = role, actual = roleDomain)
    }

    /**
     * Test method for [Mapper.mapBack].
     */
    @Test
    fun mapBack() {
        val role = RoleUtils.getRole(index = 1).name
        val roleDomain = mapper.mapBack(source = role)

        RoleUtils.assertRoleDeepEquals(expected = role, actual = roleDomain)
    }

}
