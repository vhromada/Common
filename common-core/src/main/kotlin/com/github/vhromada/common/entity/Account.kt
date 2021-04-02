package com.github.vhromada.common.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * A class represents account.
 *
 * @author Vladimir Hromada
 */
data class Account(
    /**
     * ID
     */
    val id: Int?,

    /**
     * UUID
     */
    val uuid: String?,

    /**
     * Username
     */
    private val username: String?,

    /**
     * Password
     */
    private val password: String?,

    /**
     * Roles
     */
    val roles: List<String>?
) : UserDetails {

    override fun getAuthorities(): List<GrantedAuthority> {
        return roles!!.map { SimpleGrantedAuthority(it) }
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String? {
        return username
    }

    override fun getPassword(): String? {
        return password
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

}
