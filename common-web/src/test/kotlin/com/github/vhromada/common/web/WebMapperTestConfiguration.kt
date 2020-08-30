package com.github.vhromada.common.web

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * A class represents Spring configuration for tests of mappers.
 *
 * @author Vladimir Hromada
 */
@Configuration
@ComponentScan("com.github.vhromada.common.web.mapper")
class WebMapperTestConfiguration
