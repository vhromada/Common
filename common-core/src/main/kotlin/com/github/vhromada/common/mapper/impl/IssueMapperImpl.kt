package com.github.vhromada.common.mapper.impl

import com.github.vhromada.common.entity.Issue
import com.github.vhromada.common.entity.IssueList
import com.github.vhromada.common.mapper.IssueMapper
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper between result and issues.
 *
 * @author Vladimir Hromada
 */
@Component
class IssueMapperImpl : IssueMapper {

    override fun map(source: Result<*>): IssueList {
        return IssueList(issues = source.events().map { mapIssue(event = it) })
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
