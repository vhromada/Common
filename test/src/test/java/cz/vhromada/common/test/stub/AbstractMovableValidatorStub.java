package cz.vhromada.common.test.stub;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.validator.AbstractMovableValidator;
import cz.vhromada.result.Event;
import cz.vhromada.result.Result;
import cz.vhromada.result.Severity;

/**
 * A class represents stub for {@link AbstractMovableValidator}.
 *
 * @author Vladimir Hromada
 */
public class AbstractMovableValidatorStub extends AbstractMovableValidator<Movable, Movable> {

    /**
     * Event key
     */
    private final String key;

    /**
     * Event value
     */
    private final String value;

    /**
     * Creates a new instance of AbstractMovableValidatorStub.
     *
     * @param name           name of entity
     * @param movableService service for movable data
     * @param key            event key
     * @param value          event value
     * @throws IllegalArgumentException if name of entity is null
     *                                  or service for  movable data is null
     */
    public AbstractMovableValidatorStub(final String name, final MovableService<Movable> movableService, final String key, final String value) {
        super(name, movableService);

        this.key = key;
        this.value = value;
    }

    @Override
    protected Movable getData(final Movable data) {
        return getMovableService().get(data.getId());
    }

    @Override
    protected List<Movable> getList(final Movable data) {
        return getMovableService().getAll();
    }

    @Override
    protected void validateDataDeep(final Movable data, final Result result) {
        result.addEvent(new Event(Severity.WARN, key, value));
    }

}
