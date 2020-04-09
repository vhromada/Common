package cz.vhromada.common.test.service

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import cz.vhromada.common.domain.AuditEntity
import cz.vhromada.common.provider.AccountProvider
import cz.vhromada.common.provider.TimeProvider
import cz.vhromada.common.service.AbstractMovableService
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.test.stub.AbstractMovableServiceStub
import cz.vhromada.common.test.stub.AuditEntityStub
import cz.vhromada.common.test.utils.TestConstants
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.mockito.Mock
import org.springframework.data.jpa.repository.JpaRepository

/**
 * A class represents test for class [AbstractMovableService].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableServiceTest : MovableServiceTest<AuditEntity>() {

    /**
     * Instance of [JpaRepository]
     */
    @Mock
    private lateinit var repository: JpaRepository<AuditEntity, Int>

    /**
     * Instance of [AccountProvider]
     */
    @Mock
    private lateinit var accountProvider: AccountProvider

    /**
     * Instance of [TimeProvider]
     */
    @Mock
    private lateinit var timeProvider: TimeProvider

    /**
     * Cache key
     */
    private val cacheKey = "data"

    override fun getRepository(): JpaRepository<AuditEntity, Int> {
        return repository
    }

    override fun getAccountProvider(): AccountProvider {
        return accountProvider
    }

    override fun getTimeProvider(): TimeProvider {
        return timeProvider
    }

    override fun getService(): MovableService<AuditEntity> {
        return AbstractMovableServiceStub(repository, accountProvider, timeProvider, cache, cacheKey, { getCopyItem() }, { repository.findAll() })
    }

    override fun getCacheKey(): String {
        return cacheKey + TestConstants.ACCOUNT_ID
    }

    override fun getItem1(): AuditEntity {
        return AuditEntityStub(1, 0)
    }

    override fun getItem2(): AuditEntity {
        return AuditEntityStub(2, 1)
    }

    override fun getAddItem(): AuditEntity {
        return AuditEntityStub(null, 4)
    }

    override fun getCopyItem(): AuditEntity {
        return AuditEntityStub(10, 10)
    }

    override fun initAllDataMock(data: List<AuditEntity>) {
        whenever(repository.findAll()).thenReturn(data)
    }

    override fun verifyAllDataMock() {
        verify(repository).findAll()
        verifyNoMoreInteractions(repository)
    }

    override fun anyItem(): AuditEntity {
        return any()
    }

    override fun argumentCaptorItem(): KArgumentCaptor<AuditEntity> {
        return argumentCaptor()
    }

    override fun assertDataDeepEquals(expected: AuditEntity, actual: AuditEntity) {
        assertSoftly { softly ->
            softly.assertThat(expected).isNotNull
            softly.assertThat(actual).isNotNull()
        }
        assertSoftly { softly ->
            softly.assertThat(actual.id).isEqualTo(expected.id)
            softly.assertThat(actual.position).isEqualTo(expected.position)
        }
    }

}
