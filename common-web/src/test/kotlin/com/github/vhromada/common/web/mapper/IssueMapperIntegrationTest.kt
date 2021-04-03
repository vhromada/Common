package com.github.vhromada.common.web.mapper

import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.web.WebMapperTestConfiguration
import com.github.vhromada.common.web.entity.Issue
import com.github.vhromada.common.web.entity.IssueList
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * A class represents test for mapper between [Result] and [IssueList].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [WebMapperTestConfiguration::class])
class IssueMapperIntegrationTest {

    /**
     * Instance of [IssueMapper]
     */
    @Autowired
    private lateinit var mapper: IssueMapper

    /**
     * Test method for [IssueMapper.map].
     */
    @Test
    fun map() {
        val result = createResult()

        val issues = mapper.map(result = result)

        assertIssuesDeepEquals(result = result, issues = issues)
    }

    /**
     * Returns result.
     *
     * @return result
     */
    private fun createResult(): Result<String> {
        val result = Result.of(data = "test")
        result.addEvent(event = Event(severity = Severity.ERROR, key = "key", message = "message"))
        return result
    }

    /**
     * Asserts result and issues deep equals.
     *
     * @param result result
     * @param issues issues
     */
    private fun assertIssuesDeepEquals(result: Result<*>, issues: IssueList) {
        assertThat(issues.issues).hasSameSizeAs(result.events())
        for (i in issues.issues.indices) {
            assertIssueDeepEquals(event = result.events()[i], issue = issues.issues[i])
        }
    }

    /**
     * Asserts event and issue deep equals.
     *
     * @param event event
     * @param issue issue
     */
    private fun assertIssueDeepEquals(event: Event, issue: Issue) {
        assertSoftly {
            it.assertThat(issue.code).isEqualTo(event.key)
            it.assertThat(issue.message).isEqualTo(event.message)
        }
    }

}
