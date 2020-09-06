package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.domain.Account
import com.github.vhromada.common.account.utils.AccountUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * A class represents test for mapper between [Account] and [com.github.vhromada.common.entity.Account]
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

    /**
     * Test method for [AccountMapper.map].
     */
    @Test
    fun map() {
        val accountDomain = AccountUtils.newAccountDomain(1)
        val account = mapper.map(accountDomain)

        AccountUtils.assertAccountDeepEquals(accountDomain, account)
    }

    /**
     * Test method for [AccountMapper.mapBack].
     */
    @Test
    fun mapBack() {
        val account = AccountUtils.newAccount(1)
        val accountDomain = mapper.mapBack(account)

        AccountUtils.assertAccountDeepEquals(accountDomain, account)
    }

}
