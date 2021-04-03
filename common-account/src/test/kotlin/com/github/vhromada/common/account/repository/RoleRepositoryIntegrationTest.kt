package com.github.vhromada.common.account.repository

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.utils.RoleUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

/**
 * A class represents integration test for class [RoleRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AccountTestConfiguration::class])
@Transactional
@Rollback
class RoleRepositoryIntegrationTest {

    /**
     * Instance of [EntityManager]
     */
    @Autowired
    @Qualifier("containerManagedEntityManager")
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [RoleRepository]
     */
    @Autowired
    private lateinit var roleRepository: RoleRepository

    /**
     * Test method for get roles.
     */
    @Test
    fun getRoles() {
        val roles = roleRepository.findAll()

        RoleUtils.assertRolesDeepEquals(expected = RoleUtils.getRoles(), actual = roles)

        assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

    /**
     * Test method for get role.
     */
    @Test
    fun getRole() {
        for (i in 1..RoleUtils.ROLES_COUNT) {
            val role = roleRepository.findById(i).orElse(null)

            RoleUtils.assertRoleDeepEquals(expected = RoleUtils.getRole(index = i), actual = role)
        }

        assertThat(roleRepository.findById(Int.MAX_VALUE)).isNotPresent

        assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

    /**
     * Test method for get role by name.
     */
    @Test
    fun findByName() {
        for (i in 1..RoleUtils.ROLES_COUNT) {
            val expectedRole = RoleUtils.getRole(index = i)
            val role = roleRepository.findByName(name = expectedRole.name).orElse(null)

            RoleUtils.assertRoleDeepEquals(expected = expectedRole, actual = role)
        }

        assertThat(roleRepository.findByName(name = "TEST")).isNotPresent

        assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

}
