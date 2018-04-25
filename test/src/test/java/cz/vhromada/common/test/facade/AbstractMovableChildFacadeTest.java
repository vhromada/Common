package cz.vhromada.common.test.facade;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.facade.AbstractMovableChildFacade;
import cz.vhromada.common.facade.MovableChildFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.test.stub.AbstractMovableChildFacadeStub;
import cz.vhromada.common.test.stub.MovableStub;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.converter.Converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A class represents test for class {@link AbstractMovableChildFacade}.
 *
 * @author Vladimir Hromada
 */
class AbstractMovableChildFacadeTest extends MovableChildFacadeTest<Movable, Movable, Movable, Movable> {

    /**
     * Instance of {@link Movable}
     */
    private Movable movable;

    /**
     * {@inheritDoc}
     * Cleanup movable.
     */
    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        movable = null;
    }

    /**
     * Test method for {@link AbstractMovableChildFacade#AbstractMovableChildFacade(MovableService, Converter, MovableValidator, MovableValidator)} with null
     * service for movable data.
     */
    @Test
    void constructor_NullCatalogService() {
        assertThatThrownBy(() -> new AbstractMovableChildFacadeStub(null, getConverter(), getParentCatalogValidator(), getChildCatalogValidator()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableChildFacade#AbstractMovableChildFacade(MovableService, Converter, MovableValidator, MovableValidator)} with null
     * converter.
     */
    @Test
    void constructor_NullConverter() {
        assertThatThrownBy(() -> new AbstractMovableChildFacadeStub(getMovableService(), null, getParentCatalogValidator(), getChildCatalogValidator()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableChildFacade#AbstractMovableChildFacade(MovableService, Converter, MovableValidator, MovableValidator)} with null
     * parent common validator.
     */
    @Test
    void constructor_NullParentCatalogValidator() {
        assertThatThrownBy(() -> new AbstractMovableChildFacadeStub(getMovableService(), getConverter(), null, getChildCatalogValidator()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableChildFacade#AbstractMovableChildFacade(MovableService, Converter, MovableValidator, MovableValidator)} with null
     * child common validator.
     */
    @Test
    void constructor_NullChildCatalogValidator() {
        assertThatThrownBy(() -> new AbstractMovableChildFacadeStub(getMovableService(), getConverter(), getParentCatalogValidator(), null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Override
    protected MovableChildFacade<Movable, Movable> getCatalogChildFacade() {
        return new AbstractMovableChildFacadeStub(getMovableService(), getConverter(), getParentCatalogValidator(), getChildCatalogValidator());
    }

    @Override
    protected Movable newParentEntity(final Integer id) {
        return getMovable(id);
    }

    @Override
    protected Movable newParentDomain(final Integer id) {
        return getMovable(id);
    }

    @Override
    protected Movable newParentDomainWithChildren(final Integer id, final List<Movable> children) {
        return getMovable(id);
    }

    @Override
    protected Movable newChildEntity(final Integer id) {
        return getMovable(id);
    }

    @Override
    protected Movable newChildDomain(final Integer id) {
        return getMovable(id);
    }

    @Override
    protected Class<Movable> getParentEntityClass() {
        return Movable.class;
    }

    @Override
    protected Class<Movable> getParentDomainClass() {
        return Movable.class;
    }

    @Override
    protected Class<Movable> getChildEntityClass() {
        return Movable.class;
    }

    @Override
    protected Class<Movable> getChildDomainClass() {
        return Movable.class;
    }

    @Override
    protected void assertParentDeepEquals(final Movable expected, final Movable actual) {
    }

    /**
     * Returns movable object.
     *
     * @param id ID
     * @return movable object
     */
    private Movable getMovable(final Integer id) {
        if (movable == null) {
            movable = new MovableStub(id);
        } else {
            movable.setId(id);
        }

        return movable;
    }

}
