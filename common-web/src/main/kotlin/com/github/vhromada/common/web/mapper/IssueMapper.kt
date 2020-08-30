package com.github.vhromada.common.web.mapper

import com.github.vhromada.common.result.Result
import com.github.vhromada.common.web.entity.IssueList

/**
 * An interface represents mapper between result and issues.
 *
 * @author Vladimir Hromada
 */
interface IssueMapper {

    /**
     * Maps result to issues.
     *
     * @param result result
     * @return mapped issues
     */
    fun map(result: Result<*>): IssueList

}
