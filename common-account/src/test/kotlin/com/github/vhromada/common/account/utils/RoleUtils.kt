package com.github.vhromada.common.account.utils

import com.github.vhromada.common.account.domain.Role
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import javax.persistence.EntityManager

/**
 * A class represents utility class for roles.
 *
 * @author Vladimir Hromada
 */
object RoleUtils {

    /**
     * Count of roles
     */
    const val ROLES_COUNT = 2

    /**
     * Returns roles.
     *
     * @return roles
     */
    fun getRoles(): List<Role> {
        val roles = mutableListOf<Role>()
        for (i in 0 until ROLES_COUNT) {
            roles.add(getRole(i + 1))
        }

        return roles
    }

    /**
     * Returns role for index.
     *
     * @param index index
     * @return role for index
     */
    fun getRole(index: Int): Role {
        val name = when (index) {
            1 -> "ADMIN"
            2 -> "USER"
            else -> throw IllegalArgumentException("Bad index")
        }

        return Role(
                id = index,
                name = "ROLE_$name")
    }

    /**
     * Returns count of roles.
     *
     * @param entityManager entity manager
     * @return count of roles
     */
    @Suppress("CheckStyle")
    fun getRolesCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(r.id) FROM Role r", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Asserts roles deep equals.
     *
     * @param expected expected roles
     * @param actual   actual roles
     */
    fun assertRolesDeepEquals(expected: List<Role?>?, actual: List<Role?>?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertThat(expected!!.size).isEqualTo(actual!!.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertRoleDeepEquals(expected[i], actual[i])
            }
        }
    }

    /**
     * Asserts role deep equals.
     *
     * @param expected expected role
     * @param actual   actual role
     */
    fun assertRoleDeepEquals(expected: Role?, actual: Role?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertSoftly {
            it.assertThat(actual!!.id).isEqualTo(expected!!.id)
            it.assertThat(actual.name).isEqualTo(expected.name)
        }
    }

    /**
     * Asserts roles deep equals.
     *
     * @param expected expected roles
     * @param actual   actual roles
     */
    fun assertRolesListDeepEquals(expected: List<String?>?, actual: List<Role?>?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertThat(expected!!.size).isEqualTo(actual!!.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertRoleDeepEquals(expected[i], actual[i])
            }
        }
    }

    /**
     * Asserts role deep equals.
     *
     * @param expected expected role
     * @param actual   actual role
     */
    fun assertRoleDeepEquals(expected: String?, actual: Role?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertSoftly {
            it.assertThat(actual!!.name).isEqualTo(expected)
        }
    }

}
