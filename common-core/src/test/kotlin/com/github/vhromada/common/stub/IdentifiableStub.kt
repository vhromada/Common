package com.github.vhromada.common.stub

import com.github.vhromada.common.entity.Identifiable

/**
 * A class represents stub for [Identifiable].
 *
 * @author Vladimir Hromada
 */
class IdentifiableStub(
    override var id: Int?
) : Identifiable
