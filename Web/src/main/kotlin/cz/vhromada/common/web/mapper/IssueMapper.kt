package cz.vhromada.common.web.mapper

import cz.vhromada.common.web.entity.IssueList
import cz.vhromada.validation.result.Result

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
