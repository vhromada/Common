package com.github.vhromada.common.domain

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable

/**
 * A class represents audit.
 *
 * @author Vladimir Hromada
 */
@Embeddable
data class Audit(

        /**
         * Identifier of user who created record
         */
        @Column(name = "created_user")
        val createdUser: String,

        /**
         * Timestamp when record was created
         */
        @Column(name = "created_time")
        val createdTime: LocalDateTime,

        /**
         * Identifier uf user who modified record
         */
        @Column(name = "updated_user")
        val updatedUser: String,

        /**
         * Timestamp when record was modified
         */
        @Column(name = "updated_time")
        val updatedTime: LocalDateTime) {

    constructor(user: String, time: LocalDateTime) : this(user, time, user, time)

}
