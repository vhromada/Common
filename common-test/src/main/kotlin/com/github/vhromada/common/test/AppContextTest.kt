package com.github.vhromada.common.test

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Suppress("unused")
abstract class AppContextTest {

    @Autowired
    @Suppress("SpringJavaAutowiredMembersInspection")
    private lateinit var context: ApplicationContext

    @Test
    fun app() {
        KotlinLogging.logger {}.info { "Spring context test - OK [beans=${context.beanDefinitionNames.size}]" }
    }

}
