package com.github.vhromada.common.account.repository

import com.github.vhromada.common.account.domain.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for roles.
 *
 * @author Vladimir Hromada
 */
interface RoleRepository : JpaRepository<Role, Int> {

    /**
     * Search role by name.
     *
     * @param name name
     * @return role
     */
    fun findByName(name: String): Optional<Role>

}
