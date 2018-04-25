package cz.vhromada.common.test.facade;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cz.vhromada.common.Movable;
import cz.vhromada.common.facade.AbstractMovableParentFacade;
import cz.vhromada.common.facade.MovableParentFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.test.stub.AbstractMovableParentFacadeStub;
import cz.vhromada.common.test.stub.MovableStub;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.converter.Converter;

import org.junit.jupiter.api.Test;

/**
 * A class represents test for class {@link AbstractMovableParentFacade}.
 *
 * @author Vladimir Hromada
 */
class AbstractMovableParentFacadeTest extends MovableParentFacadeTest<Movable, Movable> {

    /**
     * Test method for {@link AbstractMovableParentFacade#AbstractMovableParentFacade(MovableService, Converter, MovableValidator)} with null
     * service for movable data.
     */
    @Test
    void constructor_NullCatalogService() {
        assertThatThrownBy(() -> new AbstractMovableParentFacadeStub(null, getConverter(), getMovableValidator())).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableParentFacade#AbstractMovableParentFacade(MovableService, Converter, MovableValidator)} with null converter.
     */
    @Test
    void constructor_NullConverter() {
        assertThatThrownBy(() -> new AbstractMovableParentFacadeStub(getMovableService(), null, getMovableValidator()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableParentFacade#AbstractMovableParentFacade(MovableService, Converter, MovableValidator)} with null
     * validator for movable data.
     */
    @Test
    void constructor_NullCatalogValidator() {
        assertThatThrownBy(() -> new AbstractMovableParentFacadeStub(getMovableService(), getConverter(), null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Override
    protected MovableParentFacade<Movable> getCatalogParentFacade() {
        return new AbstractMovableParentFacadeStub(getMovableService(), getConverter(), getMovableValidator());
    }

    @Override
    protected Movable newEntity(final Integer id) {
        return new MovableStub(id);
    }

    @Override
    protected Movable newDomain(final Integer id) {
        return new MovableStub(id);
    }

    @Override
    protected Class<Movable> getEntityClass() {
        return Movable.class;
    }

    @Override
    protected Class<Movable> getDomainClass() {
        return Movable.class;
    }

}
