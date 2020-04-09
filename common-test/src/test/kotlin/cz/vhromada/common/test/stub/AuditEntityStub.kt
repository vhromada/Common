package cz.vhromada.common.test.stub

import cz.vhromada.common.domain.Audit
import cz.vhromada.common.domain.AuditEntity
import cz.vhromada.common.test.utils.TestConstants

/**
 * A class represents stub for [Audit].
 *
 * @author Vladimir Hromada
 */
class AuditEntityStub(
        override var id: Int?,
        override var position: Int? = null,
        override var audit: Audit? = Audit(TestConstants.ACCOUNT_ID, TestConstants.TIME)) : AuditEntity(audit)
