package com.github.vhromada.common.account.domain

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

/**
 * A class represents role.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "roles")
data class Role(
    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "role_generator", sequenceName = "roles_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
    val id: Int?,

    /**
     * Name
     */
    @Column(name = "role_name")
    val name: String
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is Role || id == null) {
            false
        } else {
            id == other.id
        }
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id)
    }

}
