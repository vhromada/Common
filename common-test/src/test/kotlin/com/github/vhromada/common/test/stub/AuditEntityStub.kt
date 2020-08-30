package com.github.vhromada.common.test.stub

import com.github.vhromada.common.domain.Audit
import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.test.utils.TestConstants

/**
 * A class represents stub for [Audit].
 *
 * @author Vladimir Hromada
 */
class AuditEntityStub(
        override var id: Int?,
        override var position: Int? = null,
        override var audit: Audit? = Audit(TestConstants.ACCOUNT_ID, TestConstants.TIME)) : AuditEntity(audit)
