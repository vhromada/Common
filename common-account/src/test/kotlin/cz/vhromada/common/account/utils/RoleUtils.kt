package cz.vhromada.common.account.utils

import cz.vhromada.common.account.domain.Role
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * A class represents utility class for roles.
 *
 * @author Vladimir Hromada
 */
object RoleUtils {

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
    private fun assertRoleDeepEquals(expected: Role?, actual: Role?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertSoftly {
            it.assertThat(expected!!.id).isEqualTo(actual!!.id)
            it.assertThat(expected.name).isEqualTo(actual.name)
        }
    }

    /**
     * Asserts roles deep equals.
     *
     * @param expected expected roles
     * @param actual   actual roles
     */
    fun assertRolesListDeepEquals(expected: List<Role?>?, actual: List<String?>?) {
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
    private fun assertRoleDeepEquals(expected: Role?, actual: String?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertSoftly {
            it.assertThat(expected!!.name).isEqualTo(actual)
        }
    }

}
