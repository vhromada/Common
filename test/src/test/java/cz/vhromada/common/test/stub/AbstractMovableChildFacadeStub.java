package cz.vhromada.common.test.stub;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.converter.MovableConverter;
import cz.vhromada.common.facade.AbstractMovableChildFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.validator.MovableValidator;

/**
 * A class represents stub for {@link AbstractMovableChildFacade}.
 *
 * @author Vladimir Hromada
 */
public class AbstractMovableChildFacadeStub extends AbstractMovableChildFacade<Movable, Movable, Movable, Movable> {

    /**
     * Creates a new instance of AbstractMovableChildFacadeStub.
     *
     * @param service         service for movable data
     * @param converter       converter for movable data
     * @param parentValidator validator for movable data for parent data
     * @param childValidator  validator for movable data for child data
     * @throws IllegalArgumentException if service for movable data is null
     *                                  or converter for movable data is null
     *                                  or validator for movable data for parent data is null
     *                                  or validator for movable data for child data is null
     */
    public AbstractMovableChildFacadeStub(final MovableService<Movable> service, final MovableConverter<Movable, Movable> converter,
        final MovableValidator<Movable> parentValidator, final MovableValidator<Movable> childValidator) {
        super(service, converter, parentValidator, childValidator);
    }

    @Override
    protected Movable getDomainData(final Integer id) {
        for (final Movable movable : getService().getAll()) {
            if (id.equals(movable.getId())) {
                return movable;
            }
        }

        return null;
    }

    @Override
    protected List<Movable> getDomainList(final Movable parent) {
        return List.of(getService().get(parent.getId()));
    }

    @Override
    protected Movable getForAdd(final Movable parent, final Movable data) {
        return getService().get(parent.getId());
    }

    @Override
    protected Movable getForUpdate(final Movable data) {
        return getDomainData(getDataForUpdate(data).getId());
    }

    @Override
    protected Movable getForRemove(final Movable data) {
        return getDomainData(data.getId());
    }

    @Override
    protected Movable getForDuplicate(final Movable data) {
        return getDomainData(data.getId());
    }

    @Override
    protected Movable getForMove(final Movable data, final boolean up) {
        return getDomainData(data.getId());
    }

}
