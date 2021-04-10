package com.github.vhromada.common.mapper

import com.github.vhromada.common.entity.Issue
import com.github.vhromada.common.entity.IssueList
import com.github.vhromada.common.mapper.impl.IssueMapperImpl
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Severity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * A class represents test for mapper between [Result] and [IssueList].
 *
 * @author Vladimir Hromada
 */
class IssueMapperTest {

    /**
     * Instance of [IssueMapper]
     */
    private lateinit var mapper: IssueMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = IssueMapperImpl()
    }

    /**
     * Test method for [IssueMapper.map].
     */
    @Test
    fun map() {
        val result = createResult()

        val issues = mapper.map(source = result)

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
