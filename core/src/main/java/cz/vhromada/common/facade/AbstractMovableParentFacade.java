package cz.vhromada.common.facade;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.common.validator.ValidationType;
import cz.vhromada.converter.Converter;
import cz.vhromada.result.Result;
import cz.vhromada.result.Status;

import org.springframework.util.Assert;

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
    private final MovableService<U> movableService;

    /**
     * Converter
     */
    private final Converter converter;

    /**
     * Validator for movable data
     */
    private final MovableValidator<T> movableValidator;

    /**
     * Creates a new instance of AbstractMovableParentFacade.
     *
     * @param movableService   service for movable data
     * @param converter        converter
     * @param movableValidator validator for movable data
     * @throws IllegalArgumentException if service for movable data is null
     *                                  or converter is null
     *                                  or validator for movable data is null
     */
    public AbstractMovableParentFacade(final MovableService<U> movableService, final Converter converter, final MovableValidator<T> movableValidator) {
        Assert.notNull(movableService, "Service for movable data mustn't be null.");
        Assert.notNull(converter, "Converter mustn't be null.");
        Assert.notNull(movableValidator, "Validator for movable data mustn't be null.");

        this.movableService = movableService;
        this.converter = converter;
        this.movableValidator = movableValidator;
    }

    /**
     * Creates new data.
     *
     * @return result
     */
    @Override
    public Result<Void> newData() {
        movableService.newData();

        return new Result<>();
    }

    /**
     * Returns list of data.
     *
     * @return result with list of data
     */
    @Override
    public Result<List<T>> getAll() {
        return Result.of(converter.convertCollection(movableService.getAll(), getEntityClass()));
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

        return Result.of(converter.convert(movableService.get(id), getEntityClass()));
    }

    /**
     * Adds data. Sets new ID and position.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID isn't null</li>
     * <li>Deep data validation errors</li>
     * </ul>
     *
     * @param data data
     * @return result with validation errors
     */
    @Override
    public Result<Void> add(final T data) {
        final Result<Void> result = movableValidator.validate(data, ValidationType.NEW, ValidationType.DEEP);

        if (Status.OK == result.getStatus()) {
            movableService.add(getDataForAdd(data));
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
        final Result<Void> result = movableValidator.validate(data, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP);

        if (Status.OK == result.getStatus()) {
            movableService.update(getDataForUpdate(data));
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
        final Result<Void> result = movableValidator.validate(data, ValidationType.EXISTS);

        if (Status.OK == result.getStatus()) {
            movableService.remove(movableService.get(data.getId()));
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
        final Result<Void> result = movableValidator.validate(data, ValidationType.EXISTS);

        if (Status.OK == result.getStatus()) {
            movableService.duplicate(movableService.get(data.getId()));
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
        final Result<Void> result = movableValidator.validate(data, ValidationType.EXISTS, ValidationType.UP);

        if (Status.OK == result.getStatus()) {
            movableService.moveUp(movableService.get(data.getId()));
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
        final Result<Void> result = movableValidator.validate(data, ValidationType.EXISTS, ValidationType.DOWN);

        if (Status.OK == result.getStatus()) {
            movableService.moveDown(movableService.get(data.getId()));
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
        movableService.updatePositions();

        return new Result<>();
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
     * Returns data for add.
     *
     * @param data data
     * @return data for add
     */
    protected U getDataForAdd(final T data) {
        return converter.convert(data, getDomainClass());
    }

    /**
     * Returns data for update.
     *
     * @param data data
     * @return data for update
     */
    protected U getDataForUpdate(final T data) {
        return converter.convert(data, getDomainClass());
    }

    /**
     * Returns entity class.
     *
     * @return entity class.
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Returns domain class.
     *
     * @return domain class.
     */
    protected abstract Class<U> getDomainClass();

}
