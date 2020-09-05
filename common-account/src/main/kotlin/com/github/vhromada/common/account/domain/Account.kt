package com.github.vhromada.common.account.domain

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.io.Serializable
import java.util.Objects
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.persistence.SequenceGenerator
import javax.persistence.Table

/**
 * A class represents account.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "accounts")
data class Account(

        /**
         * ID
         */
        @Id
        @SequenceGenerator(name = "account_generator", sequenceName = "accounts_sq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_generator")
        val id: Int?,

        /**
         * UUID
         */
        val uuid: String,

        /**
         * Username
         */
        val username: String,

        /**
         * Password
         */
        val password: String,

        /**
         * Roles
         */
        @OneToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "account_roles", joinColumns = [JoinColumn(name = "account")], inverseJoinColumns = [JoinColumn(name = "role")])
        @OrderBy("id")
        @Fetch(FetchMode.SELECT)
        val roles: List<Role>) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is Account || id == null) {
            false
        } else {
            id == other.id
        }
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id)
    }

}
