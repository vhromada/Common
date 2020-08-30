package com.github.vhromada.common.domain

import com.github.vhromada.common.entity.Movable
import javax.persistence.Embedded
import javax.persistence.MappedSuperclass

/**
 * An abstract class represents audit entity.
 *
 * @author Vladimir Hromada
 */
@MappedSuperclass
abstract class AuditEntity(
        /**
         * Audit
         */
        @Embedded
        override var audit: Audit?) : Auditable, Movable
