package cz.vhromada.common.web.mapper.impl

import cz.vhromada.common.result.Event
import cz.vhromada.common.result.Result
import cz.vhromada.common.web.entity.Issue
import cz.vhromada.common.web.entity.IssueList
import cz.vhromada.common.web.mapper.IssueMapper
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper between result and issues.
 *
 * @author Vladimir Hromada
 */
@Component
class IssueMapperImpl : IssueMapper {

    override fun map(result: Result<*>): IssueList {
        return IssueList(result.events().map { mapIssue(it) })
    }

    /**
     * Maps event to issue.
     *
     * @param event event
     * @return mapped issue
     */
    private fun mapIssue(event: Event): Issue {
        return Issue(code = event.key, message = event.message)
    }

}
