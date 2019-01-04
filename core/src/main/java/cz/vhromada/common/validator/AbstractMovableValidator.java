package cz.vhromada.common.validator;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.validation.result.Event;
import cz.vhromada.validation.result.Result;
import cz.vhromada.validation.result.Severity;

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
    private final MovableService<U> service;

    /**
     * Creates a new instance of AbstractMovableValidator.
     *
     * @param name    name of entity
     * @param service service for movable data
     * @throws IllegalArgumentException if name of entity is null
     *                                  or service for movable data is null
     */
    public AbstractMovableValidator(final String name, final MovableService<U> service) {
        if (name == null) {
            throw new IllegalArgumentException("Name of entity mustn't be null.");
        }
        if (service == null) {
            throw new IllegalArgumentException("Service for movable dat mustn't be null.");
        }

        this.name = name;
        this.prefix = name.toUpperCase();
        this.service = service;
    }

    @Override
    public Result<Void> validate(final T data, final ValidationType... validationTypes) {
        if (data == null) {
            return Result.error(prefix + "_NULL", name + " mustn't be null.");
        }

        final Result<Void> result = new Result<>();
        final List<ValidationType> validationTypeList = List.of(validationTypes);
        if (validationTypeList.contains(ValidationType.NEW)) {
            validateNewData(data, result);
        }
        if (validationTypeList.contains(ValidationType.UPDATE)) {
            validateUpdateData(data, result);
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
    protected MovableService<U> getService() {
        return service;
    }

    /**
     * Returns data from repository.
     *
     * @param data data
     * @return data from repository
     */
    protected Movable getData(final T data) {
        return service.get(data.getId());
    }

    /**
     * Returns list of data from repository.
     *
     * @param data data
     * @return list of data from repository
     */
    protected List<? extends Movable> getList(final T data) {
        return service.getAll();
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
     * <li>Position isn't null</li>
     * </ul>
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    private void validateNewData(final T data, final Result<Void> result) {
        if (data.getId() != null) {
            result.addEvent(new Event(Severity.ERROR, prefix + "_ID_NOT_NULL", "ID must be null."));
        }
        if (data.getPosition() != null) {
            result.addEvent(new Event(Severity.ERROR, prefix + "_POSITION_NOT_NULL", "Position must be null."));
        }
    }

    /**
     * Validates new data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Position is null</li>
     * </ul>
     *
     * @param data   validating data
     * @param result result with validation errors
     */
    private void validateUpdateData(final T data, final Result<Void> result) {
        if (data.getPosition() == null) {
            result.addEvent(new Event(Severity.ERROR, prefix + "_POSITION_NULL", "Position mustn't be null."));
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
