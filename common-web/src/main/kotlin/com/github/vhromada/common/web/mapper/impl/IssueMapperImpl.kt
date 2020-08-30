package com.github.vhromada.common.web.mapper.impl

import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.web.entity.Issue
import com.github.vhromada.common.web.entity.IssueList
import com.github.vhromada.common.web.mapper.IssueMapper
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
