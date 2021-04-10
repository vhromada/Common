package com.github.vhromada.common.entity

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

/**
 * A class represents audit.
 *
 * @author Vladimir Hromada
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
@Suppress("unused")
abstract class Audit {

    /**
     * Identifier of user who created record
     */
    @CreatedBy
    @Column(name = "created_user")
    var createdUser: String? = null

    /**
     * Timestamp when record was created
     */
    @CreatedDate
    @Column(name = "created_time")
    var createdTime: LocalDateTime? = null

    /**
     * Identifier uf user who modified record
     */
    @LastModifiedBy
    @Column(name = "updated_user")
    var updatedUser: String? = null

    /**
     * Timestamp when record was modified
     */
    @LastModifiedDate
    @Column(name = "updated_time")
    var updatedTime: LocalDateTime? = null

}
