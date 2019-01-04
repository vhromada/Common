package cz.vhromada.common.test.stub;

import java.util.function.Supplier;

import cz.vhromada.common.Movable;
import cz.vhromada.common.repository.MovableRepository;
import cz.vhromada.common.service.AbstractMovableService;

import org.springframework.cache.Cache;

/**
 * A class represents stub for {@link AbstractMovableService}.
 *
 * @author Vladimir Hromada
 */
public class AbstractMovableServiceStub extends AbstractMovableService<Movable> {

    /**
     * Supplier for getting copy of data
     */
    private final Supplier<Movable> copySupplier;

    /**
     * Creates a new instance of AbstractMovableServiceStub.
     *
     * @param repository   repository for data
     * @param cache        cache for data
     * @param key          cache key
     * @param copySupplier supplier for getting copy of data
     * @throws IllegalArgumentException if repository for data is null
     *                                  or cache for data is null
     *                                  or cache key is null
     */
    public AbstractMovableServiceStub(final MovableRepository<Movable> repository, final Cache cache, final String key, final Supplier<Movable> copySupplier) {
        super(repository, cache, key);

        this.copySupplier = copySupplier;
    }

    @Override
    protected Movable getCopy(final Movable data) {
        return copySupplier.get();
    }

}
