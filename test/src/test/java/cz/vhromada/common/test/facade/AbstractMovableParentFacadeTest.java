package cz.vhromada.common.test.facade;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cz.vhromada.common.Movable;
import cz.vhromada.common.converter.MovableConverter;
import cz.vhromada.common.facade.AbstractMovableParentFacade;
import cz.vhromada.common.facade.MovableParentFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.test.stub.AbstractMovableParentFacadeStub;
import cz.vhromada.common.test.stub.MovableStub;
import cz.vhromada.common.validator.MovableValidator;

import org.junit.jupiter.api.Test;

/**
 * A class represents test for class {@link AbstractMovableParentFacade}.
 *
 * @author Vladimir Hromada
 */
class AbstractMovableParentFacadeTest extends MovableParentFacadeTest<Movable, Movable> {

    /**
     * Test method for {@link AbstractMovableParentFacade#AbstractMovableParentFacade(MovableService, MovableConverter, MovableValidator)}
     * with null service for movable data.
     */
    @Test
    void constructor_NullMovableService() {
        assertThatThrownBy(() -> new AbstractMovableParentFacadeStub(null, getConverter(), getValidator())).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableParentFacade#AbstractMovableParentFacade(MovableService, MovableConverter, MovableValidator)}
     * with null converter for movable data.
     */
    @Test
    void constructor_NullConverter() {
        assertThatThrownBy(() -> new AbstractMovableParentFacadeStub(getService(), null, getValidator())).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableParentFacade#AbstractMovableParentFacade(MovableService, MovableConverter, MovableValidator)}
     * with null validator for movable data.
     */
    @Test
    void constructor_NullMovableValidator() {
        assertThatThrownBy(() -> new AbstractMovableParentFacadeStub(getService(), getConverter(), null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Override
    protected MovableParentFacade<Movable> getFacade() {
        return new AbstractMovableParentFacadeStub(getService(), getConverter(), getValidator());
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
