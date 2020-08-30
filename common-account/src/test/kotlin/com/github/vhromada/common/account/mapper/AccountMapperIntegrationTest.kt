package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.utils.AccountUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * A class represents test for class [AccountMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AccountTestConfiguration::class])
class AccountMapperIntegrationTest {

    /**
     * Instance of [AccountMapper]
     */
    @Autowired
    private lateinit var mapper: AccountMapper

    @Test
    fun map() {
        val accountDomain = AccountUtils.getAccount(1)
        val account = mapper.map(accountDomain)

        AccountUtils.assertAccountDeepEquals(accountDomain, account)
    }

}
