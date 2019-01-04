package cz.vhromada.common.test.stub;

import cz.vhromada.common.Movable;
import cz.vhromada.common.converter.MovableConverter;
import cz.vhromada.common.facade.AbstractMovableParentFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.validator.MovableValidator;

/**
 * A class represents stub for {@link AbstractMovableParentFacade}.
 *
 * @author Vladimir Hromada
 */
public class AbstractMovableParentFacadeStub extends AbstractMovableParentFacade<Movable, Movable> {

    /**
     * Creates a new instance of AbstractMovableParentFacadeStub.
     *
     * @param service   service for movable data
     * @param converter converter for movable data
     * @param validator validator for movable data
     * @throws IllegalArgumentException if service for movable data is null
     *                                  or converter for movable data is null
     *                                  or validator for movable data is null
     */
    public AbstractMovableParentFacadeStub(final MovableService<Movable> service, final MovableConverter<Movable, Movable> converter,
        final MovableValidator<Movable> validator) {
        super(service, converter, validator);
    }

}
