package cz.vhromada.common.account.repository

import cz.vhromada.common.account.domain.Account
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

}
