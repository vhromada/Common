package cz.vhromada.common.validator;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.utils.CollectionUtils;
import cz.vhromada.result.Event;
import cz.vhromada.result.Result;
import cz.vhromada.result.Severity;

import org.springframework.util.Assert;

/**
 * An abstract class represents validator for movable data.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
public abstract class AbstractMovableValidator<T extends Movable, U extends Movable> implements MovableValidator<T> {

    /**
     * Name of entity
     */
    private final String name;

    /**
     * Prefix for validation keys
     */
    private final String prefix;

    /**
     * Service for movable data
     */
    private final MovableService<U> movableService;

    /**
     * Creates a new instance of AbstractMovableValidator.
     *
     * @param name           name of entity
     * @param movableService service for  movable data
     * @throws IllegalArgumentException if name of entity is null
     *                                  or service for  movable data is null
     */
    public AbstractMovableValidator(final String name, final MovableService<U> movableService) {
        Assert.notNull(name, "Name of entity mustn't be null.");
        Assert.notNull(movableService, "Service for  movable data mustn't be null.");

        this.name = name;
        this.prefix = name.toUpperCase();
        this.movableService = movableService;
    }

    @Override
    public Result<Void> validate(final T data, final ValidationType... validationTypes) {
        if (data == null) {
            return Result.error(prefix + "_NULL", name + " mustn't be null.");
        }

        final Result<Void> result = new Result<>();
        final List<ValidationType> validationTypeList = CollectionUtils.newList(validationTypes);
        if (validationTypeList.contains(ValidationType.NEW)) {
            validateNewData(data, result);
        }
        if (validationTypeList.contains(ValidationType.EXISTS)) {
            validateExistingData(data, result);
        }
        if (validationTypeList.contains(ValidationType.DEEP)) {
            validateDataDeep(data, result);
        }
        if (validationTypeList.contains(ValidationType.UP)) {
            validateMovingData(data, result, true);
        }
        if (validationTypeList.contains(ValidationType.DOWN)) {
            validateMovingData(data, result, false);
        }

        return result;
    }

    /**
     * Returns service for movable data.
     *
     * @return service for movable data
     */
    protected MovableService<U> getMovableService() {
        return movableService;
    }

    /**
     * Returns data from repository.
     *
     * @param data data
     * @return data from repository
     */
    protected Movable getData(final T data) {
        return movableService.get(data.getId());
    }

    /**
     * Returns list of data from repository.
     *
     * @param data data
     * @return list of data from repository
     */
    protected List<? extends Movable> getList(final T data) {
        return movableService.getAll();
    }

    /**
     * Validates data deeply.
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    protected abstract void validateDataDeep(T data, Result<Void> result);

    /**
     * Validates new data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>ID isn't null</li>
     * </ul>
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    private void validateNewData(final T data, final Result<Void> result) {
        if (data.getId() != null) {
            result.addEvent(new Event(Severity.ERROR, prefix + "_ID_NOT_NULL", "ID must be null."));
        }
    }

    /**
     * Validates existing data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>ID is null</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    private void validateExistingData(final T data, final Result<Void> result) {
        if (data.getId() == null) {
            result.addEvent(new Event(Severity.ERROR, prefix + "_ID_NULL", "ID mustn't be null."));
        } else if (getData(data) == null) {
            result.addEvent(new Event(Severity.ERROR, prefix + "_NOT_EXIST", name + " doesn't exist."));
        }
    }

    /**
     * Validates moving data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Not movable data</li>
     * </ul>
     *
     * @param data   validating data
     * @param result result with validation errors
     * @param up     true if data is moving up
     */
    private void validateMovingData(final T data, final Result<Void> result, final boolean up) {
        if (data.getId() != null) {
            final Movable domainData = getData(data);
            if (domainData != null) {
                final List<? extends Movable> list = getList(data);
                final int index = list.indexOf(domainData);
                if (up && index <= 0) {
                    result.addEvent(new Event(Severity.ERROR, prefix + "_NOT_MOVABLE", name + " can't be moved up."));
                } else if (!up && (index < 0 || index >= list.size() - 1)) {
                    result.addEvent(new Event(Severity.ERROR, prefix + "_NOT_MOVABLE", name + " can't be moved down."));
                }
            }
        }
    }

}
