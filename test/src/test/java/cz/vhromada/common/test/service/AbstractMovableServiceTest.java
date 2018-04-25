package cz.vhromada.common.test.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.AbstractMovableService;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.test.stub.AbstractMovableServiceStub;
import cz.vhromada.common.test.stub.MovableStub;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A class represents test for class {@link AbstractMovableService}.
 *
 * @author Vladimir Hromada
 */
class AbstractMovableServiceTest extends MovableServiceTest<Movable> {

    /**
     * Instance of {@link JpaRepository}
     */
    @Mock
    private JpaRepository<Movable, Integer> repository;

    /**
     * Test method for {@link AbstractMovableService#AbstractMovableService(JpaRepository, Cache, String)} with null repository.
     */
    @Test
    void constructor_NullMovableRepository() {
        assertThatThrownBy(() -> new AbstractMovableServiceStub(null, getCache(), getCacheKey(), this::getCopyItem))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableService#AbstractMovableService(JpaRepository, Cache, String)} with null cache for data.
     */
    @Test
    void constructor_NullCache() {
        assertThatThrownBy(() -> new AbstractMovableServiceStub(repository, null, getCacheKey(), this::getCopyItem))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableService#AbstractMovableService(JpaRepository, Cache, String)} with null cache key.
     */
    @Test
    void constructor_NullCacheKey() {
        assertThatThrownBy(() -> new AbstractMovableServiceStub(repository, getCache(), null, this::getCopyItem)).isInstanceOf(IllegalArgumentException.class);
    }

    @Override
    protected JpaRepository<Movable, Integer> getRepository() {
        return repository;
    }

    @Override
    protected MovableService<Movable> getMovableService() {
        return new AbstractMovableServiceStub(repository, getCache(), getCacheKey(), this::getCopyItem);
    }

    @Override
    protected String getCacheKey() {
        return "data";
    }

    @Override
    protected Movable getItem1() {
        return new MovableStub(1, 0);
    }

    @Override
    protected Movable getItem2() {
        return new MovableStub(2, 1);
    }

    @Override
    protected Movable getAddItem() {
        return new MovableStub(null, 4);
    }

    @Override
    protected Movable getCopyItem() {
        return new MovableStub(10, 10);
    }

    @Override
    protected Class<Movable> getItemClass() {
        return Movable.class;
    }

    @Override
    protected void assertDataDeepEquals(final Movable expected, final Movable actual) {
        assertSoftly(softly -> {
            softly.assertThat(expected).isNotNull();
            softly.assertThat(actual).isNotNull();
        });
        assertSoftly(softly -> {
            softly.assertThat(actual.getId()).isEqualTo(expected.getId());
            softly.assertThat(actual.getPosition()).isEqualTo(expected.getPosition());
        });
    }

}
