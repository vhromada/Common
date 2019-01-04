package cz.vhromada.common.facade;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.converter.MovableConverter;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.common.validator.ValidationType;
import cz.vhromada.validation.result.Result;
import cz.vhromada.validation.result.Status;

/**
 * An abstract class facade for movable data for parent data.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
public abstract class AbstractMovableParentFacade<T extends Movable, U extends Movable> implements MovableParentFacade<T> {

    /**
     * Service for movable data
     */
    private final MovableService<U> service;

    /**
     * Converter for movable data
     */
    private final MovableConverter<T, U> converter;

    /**
     * Validator for movable data
     */
    private final MovableValidator<T> validator;

    /**
     * Creates a new instance of AbstractMovableParentFacade.
     *
     * @param service   service for movable data
     * @param converter converter for movable data
     * @param validator validator for movable data
     * @throws IllegalArgumentException if service for movable data is null
     *                                  or converter for movable data is null
     *                                  or validator for movable data is null
     */
    public AbstractMovableParentFacade(final MovableService<U> service, final MovableConverter<T, U> converter, final MovableValidator<T> validator) {
        if (service == null) {
            throw new IllegalArgumentException("Service for data mustn't be null.");
        }
        if (converter == null) {
            throw new IllegalArgumentException("Converter for data mustn't be null.");
        }
        if (validator == null) {
            throw new IllegalArgumentException("Validator for data mustn't be null.");
        }

        this.service = service;
        this.converter = converter;
        this.validator = validator;
    }

    /**
     * Creates new data.
     *
     * @return result
     */
    @Override
    public Result<Void> newData() {
        service.newData();
        return new Result<>();
    }

    /**
     * Returns list of data.
     *
     * @return result with list of data
     */
    @Override
    public Result<List<T>> getAll() {
        return Result.of(converter.convertBack(service.getAll()));
    }

    /**
     * Returns data with ID or null if there aren't such data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>ID is null</li>
     * </ul>
     *
     * @param id ID
     * @return result with data or validation errors
     */
    @Override
    public Result<T> get(final Integer id) {
        if (id == null) {
            return Result.error("ID_NULL", "ID mustn't be null.");
        }
        return Result.of(converter.convertBack(service.get(id)));
    }

    /**
     * Adds data. Sets new ID and position.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID isn't null</li>
     * <li>Position isn't null</li>
     * <li>Deep data validation errors</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    @Override
    public Result<Void> add(final T data) {
        final Result<Void> result = validator.validate(data, ValidationType.NEW, ValidationType.DEEP);
        if (Status.OK == result.getStatus()) {
            service.add(getDataForAdd(data));
        }
        return result;
    }

    /**
     * Updates data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Position is null</li>
     * <li>Deep data validation errors</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data new value of data
     * @return result with validation errors
     */
    @Override
    public Result<Void> update(final T data) {
        final Result<Void> result = validator.validate(data, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP);
        if (Status.OK == result.getStatus()) {
            service.update(getDataForUpdate(data));
        }
        return result;
    }

    /**
     * Removes data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    @Override
    public Result<Void> remove(final T data) {
        final Result<Void> result = validator.validate(data, ValidationType.EXISTS);
        if (Status.OK == result.getStatus()) {
            service.remove(service.get(data.getId()));
        }
        return result;
    }

    /**
     * Duplicates data.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    @Override
    public Result<Void> duplicate(final T data) {
        final Result<Void> result = validator.validate(data, ValidationType.EXISTS);
        if (Status.OK == result.getStatus()) {
            service.duplicate(service.get(data.getId()));
        }
        return result;
    }

    /**
     * Moves data in list one position up.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Data can't be moved up</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    @Override
    public Result<Void> moveUp(final T data) {
        final Result<Void> result = validator.validate(data, ValidationType.EXISTS, ValidationType.UP);
        if (Status.OK == result.getStatus()) {
            service.moveUp(service.get(data.getId()));
        }
        return result;
    }

    /**
     * Moves data in list one position up.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID is null</li>
     * <li>Data can't be moved down</li>
     * <li>Data doesn't exist in data storage</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    @Override
    public Result<Void> moveDown(final T data) {
        final Result<Void> result = validator.validate(data, ValidationType.EXISTS, ValidationType.DOWN);
        if (Status.OK == result.getStatus()) {
            service.moveDown(service.get(data.getId()));
        }
        return result;
    }

    /**
     * Updates positions.
     *
     * @return result
     */
    @Override
    public Result<Void> updatePositions() {
        service.updatePositions();
        return new Result<>();
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
     * Returns data for add.
     *
     * @param data data
     * @return data for add
     */
    protected U getDataForAdd(final T data) {
        return converter.convert(data);
    }

    /**
     * Returns data for update.
     *
     * @param data data
     * @return data for update
     */
    protected U getDataForUpdate(final T data) {
        return converter.convert(data);
    }

}
