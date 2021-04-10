package com.github.vhromada.common.mapper

import com.github.vhromada.common.entity.IssueList
import com.github.vhromada.common.result.Result

/**
 * An interface represents mapper between result and issues.
 *
 * @author Vladimir Hromada
 */
interface IssueMapper {

    /**
     * Maps result to issues.
     *
     * @param source result
     * @return mapped issues
     */
    fun map(source: Result<*>): IssueList

}
