package com.github.vhromada.common.account

import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.provider.AccountProvider
import com.github.vhromada.common.provider.UuidProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.support.SharedEntityManagerBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.PlatformTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * A class represents Spring configuration for tests.
 *
 * @author Vladimir Hromada
 */
@Configuration
@Import(AccountConfiguration::class)
class AccountTestConfiguration {

    @Bean
    fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScripts("data.sql")
                .build()
    }

    @Bean
    fun entityManagerFactory(dataSource: DataSource): EntityManagerFactory? {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
        entityManagerFactoryBean.setPackagesToScan("com.github.vhromada.common.account.domain")
        entityManagerFactoryBean.dataSource = dataSource
        entityManagerFactoryBean.afterPropertiesSet()
        return entityManagerFactoryBean.getObject()
    }

    @Bean
    fun containerManagedEntityManager(entityManagerFactory: EntityManagerFactory): SharedEntityManagerBean {
        val entityManagerBean = SharedEntityManagerBean()
        entityManagerBean.entityManagerFactory = entityManagerFactory
        return entityManagerBean
    }

    @Bean
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory(dataSource)
        return transactionManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return object : PasswordEncoder {

            override fun encode(rawPassword: CharSequence): String {
                return rawPassword.toString()
            }

            override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
                return true
            }

        }
    }

    @Bean
    fun accountProvider(): AccountProvider {
        return object : AccountProvider {

            override fun getAccount(): Account {
                return AccountUtils.newAccount(1)
            }

        }
    }

    @Bean
    fun uuidProvider(): UuidProvider {
        return object : UuidProvider {

            override fun getUuid(): String {
                return "c01cb46d-0acf-402b-9d76-d12a75b98f8a"
            }

        }
    }

}
