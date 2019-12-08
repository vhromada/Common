package cz.vhromada.common.web

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * A class represents Spring configuration for tests of mappers.
 *
 * @author Vladimir Hromada
 */
@Configuration
@ComponentScan("cz.vhromada.common.web.mapper")
class WebMapperTestConfiguration
