package com.github.vhromada.common.account.repository

import com.github.vhromada.common.account.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountRepository : JpaRepository<Account, Int> {

    /**
     * Search account by username.
     *
     * @param username username
     * @return account
     */
    fun findByUsername(username: String): Optional<Account>

    /**
     * Search account by uuid.
     *
     * @param uuid uuid
     * @return account
     */
    fun findByUuid(uuid: String): Optional<Account>

}
