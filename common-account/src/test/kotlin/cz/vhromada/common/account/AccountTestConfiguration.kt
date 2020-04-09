package cz.vhromada.common.account

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.support.SharedEntityManagerBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
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
                .addScripts("account.sql", "data.sql")
                .build()
    }

    @Bean
    fun entityManagerFactory(dataSource: DataSource): EntityManagerFactory? {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
        entityManagerFactoryBean.setPackagesToScan("cz.vhromada.common.account.domain")
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

}
