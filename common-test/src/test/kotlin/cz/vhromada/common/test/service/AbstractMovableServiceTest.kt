package cz.vhromada.common.test.service

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import cz.vhromada.common.Movable
import cz.vhromada.common.service.AbstractMovableService
import cz.vhromada.common.service.MovableService
import cz.vhromada.common.test.stub.AbstractMovableServiceStub
import cz.vhromada.common.test.stub.MovableStub
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.mockito.Mock
import org.springframework.data.jpa.repository.JpaRepository

/**
 * A class represents test for class [AbstractMovableService].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableServiceTest : MovableServiceTest<Movable>() {

    /**
     * Instance of [JpaRepository]
     */
    @Mock
    private lateinit var repository: JpaRepository<Movable, Int>

    override fun getRepository(): JpaRepository<Movable, Int> {
        return repository
    }

    override fun getService(): MovableService<Movable> {
        return AbstractMovableServiceStub(repository, cache, getCacheKey()) { getCopyItem() }
    }

    override fun getCacheKey(): String {
        return "data"
    }

    override fun getItem1(): Movable {
        return MovableStub(1, 0)
    }

    override fun getItem2(): Movable {
        return MovableStub(2, 1)
    }

    override fun getAddItem(): Movable {
        return MovableStub(null, 4)
    }

    override fun getCopyItem(): Movable {
        return MovableStub(10, 10)
    }

    override fun anyItem(): Movable {
        return any()
    }

    override fun argumentCaptorItem(): KArgumentCaptor<Movable> {
        return argumentCaptor()
    }

    override fun assertDataDeepEquals(expected: Movable, actual: Movable) {
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
