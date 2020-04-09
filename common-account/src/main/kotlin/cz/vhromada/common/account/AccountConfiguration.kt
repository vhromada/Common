package cz.vhromada.common.account

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * A class represents Spring configuration for account.
 *
 * @author Vladimir Hromada
 */
@Configuration
@ComponentScan("cz.vhromada.common.account")
@EnableJpaRepositories("cz.vhromada.common.account.repository")
@EntityScan("cz.vhromada.common.account.domain")
class AccountConfiguration
