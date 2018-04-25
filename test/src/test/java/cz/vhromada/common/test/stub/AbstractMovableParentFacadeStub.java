package cz.vhromada.common.test.stub;

import cz.vhromada.common.Movable;
import cz.vhromada.common.facade.AbstractMovableParentFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.converter.Converter;

/**
 * A class represents stub for {@link AbstractMovableParentFacade}.
 *
 * @author Vladimir Hromada
 */
public class AbstractMovableParentFacadeStub extends AbstractMovableParentFacade<Movable, Movable> {

    /**
     * Creates a new instance of AbstractMovableParentFacadeStub.
     *
     * @param movableService   service for movable data
     * @param converter        converter
     * @param movableValidator validator for movable data
     * @throws IllegalArgumentException if service for movable data is null
     *                                  or converter is null
     *                                  or validator for movable data is null
     */
    public AbstractMovableParentFacadeStub(final MovableService<Movable> movableService, final Converter converter,
        final MovableValidator<Movable> movableValidator) {
        super(movableService, converter, movableValidator);
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
