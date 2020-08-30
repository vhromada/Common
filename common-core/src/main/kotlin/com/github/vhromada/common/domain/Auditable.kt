package com.github.vhromada.common.domain

import java.io.Serializable

/**
 * An interface represents auditable object.
 *
 * @author Vladimir Hromada
 */
interface Auditable : Serializable {

    /**
     * Audit
     */
    var audit: Audit?

    /**
     * Modifies record
     *
     * @param audit audit
     */
    fun modify(audit: Audit) {
        this.audit = if (this.audit == null) {
            audit
        } else {
            this.audit!!.copy(updatedUser = audit.updatedUser, updatedTime = audit.updatedTime)
        }
    }

}
