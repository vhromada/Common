package cz.vhromada.common.test.stub;

import java.util.function.Supplier;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.AbstractMovableService;

import org.springframework.cache.Cache;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A class represents stub for {@link AbstractMovableService}.
 *
 * @author Vladimir Hromada
 */
public class AbstractMovableServiceStub extends AbstractMovableService<Movable> {

    /**
     * Supplier for getting copy of data
     */
    private final Supplier<Movable> movableCopySupplier;

    /**
     * Creates a new instance of AbstractMovableServiceStub.
     *
     * @param repository          repository for data
     * @param cache               cache for data
     * @param key                 cache key
     * @param movableCopySupplier supplier for getting copy of data
     * @throws IllegalArgumentException if repository for data is null
     *                                  or cache for data is null
     *                                  or cache key is null
     */
    public AbstractMovableServiceStub(final JpaRepository<Movable, Integer> repository, final Cache cache, final String key,
        final Supplier<Movable> movableCopySupplier) {
        super(repository, cache, key);

        this.movableCopySupplier = movableCopySupplier;
    }

    @Override
    protected Movable getCopy(final Movable data) {
        return movableCopySupplier.get();
    }

}
