package cz.vhromada.common.test.stub;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.facade.AbstractMovableChildFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.utils.CollectionUtils;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.converter.Converter;

/**
 * A class represents stub for {@link AbstractMovableChildFacade}.
 *
 * @author Vladimir Hromada
 */
public class AbstractMovableChildFacadeStub extends AbstractMovableChildFacade<Movable, Movable, Movable, Movable> {

    /**
     * Creates a new instance of AbstractMovableChildFacadeStub.
     *
     * @param movableService         service for movable data
     * @param converter              converter
     * @param parentMovableValidator validator for movable data for parent data
     * @param childMovableValidator  validator for movable data for child data
     */
    public AbstractMovableChildFacadeStub(final MovableService<Movable> movableService, final Converter converter,
        final MovableValidator<Movable> parentMovableValidator, final MovableValidator<Movable> childMovableValidator) {
        super(movableService, converter, parentMovableValidator, childMovableValidator);
    }

    @Override
    protected Movable getDomainData(final Integer id) {
        for (final Movable movable : getMovableService().getAll()) {
            if (id.equals(movable.getId())) {
                return movable;
            }
        }

        return null;
    }

    @Override
    protected List<Movable> getDomainList(final Movable parent) {
        return CollectionUtils.newList(getMovableService().get(parent.getId()));
    }

    @Override
    protected Movable getForAdd(final Movable parent, final Movable data) {
        return getMovableService().get(parent.getId());
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

    @Override
    protected Class<Movable> getEntityClass() {
        return Movable.class;
    }

    @Override
    protected Class<Movable> getDomainClass() {
        return Movable.class;
    }

}
