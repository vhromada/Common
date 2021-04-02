package com.github.vhromada.common.account.mapper

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.utils.AccountUtils
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * A class represents test for mapper for account
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

        AccountUtils.assertAccountDeepEquals(account, accountDomain)
    }

    /**
     * Test method for [AccountMapper.mapBack].
     */
    @Test
    fun mapBack() {
        val account = AccountUtils.newAccount(1)
        val accountDomain = mapper.mapBack(account)

        AccountUtils.assertAccountDeepEquals(account, accountDomain)
    }

    /**
     * Test method for [AccountMapper.mapCredentials].
     */
    @Test
    fun mapCredentials() {
        val credentials = AccountUtils.newCredentials()
        val account = mapper.mapCredentials(credentials)

        assertSoftly {
            it.assertThat(account.id).isNull()
            it.assertThat(account.uuid).isNull()
            it.assertThat(account.username).isEqualTo(credentials.username)
            it.assertThat(account.password).isEqualTo(credentials.password)
            it.assertThat(account.roles).isNull()
        }
    }

}
