package cz.vhromada.common.test.stub

import cz.vhromada.common.Movable
import cz.vhromada.common.service.AbstractMovableService
import org.springframework.cache.Cache
import org.springframework.data.jpa.repository.JpaRepository

/**
 * A class represents stub for [AbstractMovableService].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableServiceStub(
        repository: JpaRepository<Movable, Int>,
        cache: Cache,
        key: String,
        private val copy: () -> Movable) : AbstractMovableService<Movable>(repository, cache, key) {

    override fun getCopy(data: Movable): Movable {
        return copy.invoke()
    }

}
