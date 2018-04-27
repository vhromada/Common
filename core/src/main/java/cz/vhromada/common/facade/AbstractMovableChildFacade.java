package cz.vhromada.common.facade;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.utils.CollectionUtils;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.common.validator.ValidationType;
import cz.vhromada.converter.Converter;
import cz.vhromada.result.Result;
import cz.vhromada.result.Status;

import org.springframework.util.Assert;

/**
 * An abstract class facade for movable data for child data.
 *
 * @param <S> type of child entity data
 * @param <T> type of child domain data
 * @param <U> type of parent entity data
 * @param <V> type of domain repository data
 * @author Vladimir Hromada
 */
public abstract class AbstractMovableChildFacade<S extends Movable, T extends Movable, U extends Movable, V extends Movable>
    implements MovableChildFacade<S, U> {

    /**
     * Service for movable data
     */
    private MovableService<V> movableService;

    /**
     * Converter
     */
    private Converter converter;

    /**
     * Validator for movable data for parent data
     */
    private MovableValidator<U> parentMovableValidator;

    /**
     * Validator for movable data for child data
     */
    private MovableValidator<S> childMovableValidator;

    /**
     * Creates a new instance of AbstractMovableChildFacade.
     *
     * @param movableService         service for movable data
     * @param converter              converter
     * @param parentMovableValidator validator for movable data for parent data
     * @param childMovableValidator  validator for movable data for child data
     * @throws IllegalArgumentException if service for movable data is null
     *                                  or converter is null
     *                                  or validator for movable data for parent data is null
     *                                  or validator for movable data for child data is null
     */
    public AbstractMovableChildFacade(final MovableService<V> movableService, final Converter converter, final MovableValidator<U> parentMovableValidator,
        final MovableValidator<S> childMovableValidator) {
        Assert.notNull(movableService, "Service for movable data mustn't be null.");
        Assert.notNull(converter, "Converter mustn't be null.");
        Assert.notNull(parentMovableValidator, "Validator for movable data for parent data mustn't be null.");
        Assert.notNull(childMovableValidator, "Validator for movable data for child data mustn't be null.");

        this.movableService = movableService;
        this.converter = converter;
        this.parentMovableValidator = parentMovableValidator;
        this.childMovableValidator = childMovableValidator;
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
    public Result<S> get(final Integer id) {
        if (id == null) {
            return Result.error("ID_NULL", "ID mustn't be null.");
        }

        return Result.of(converter.convert(getDomainData(id), getEntityClass()));
    }

    /**
     * Adds data. Sets new ID and position.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Data is null</li>
     * <li>ID isn't null</li>
     * <li>Deep data validation errors</li>
     * <li>Parent is null</li>
     * <li>Parent ID is null</li>
     * <li>Parent doesn't exist in data storage</li>
     * </ul>
     *
     * @param parent parent
     * @param data   data
     * @return result with validation errors
     */
    @Override
    public Result<Void> add(final U parent, final S data) {
        final Result<Void> result = parentMovableValidator.validate(parent, ValidationType.EXISTS);
        result.addEvents(childMovableValidator.validate(data, ValidationType.NEW, ValidationType.DEEP).getEvents());

        if (Status.OK == result.getStatus()) {
            movableService.update(getForAdd(parent, getDataForAdd(data)));
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
    public Result<Void> update(final S data) {
        final Result<Void> result = childMovableValidator.validate(data, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP);

        if (Status.OK == result.getStatus()) {
            movableService.update(getForUpdate(data));
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
    public Result<Void> remove(final S data) {
        final Result<Void> result = childMovableValidator.validate(data, ValidationType.EXISTS);

        if (Status.OK == result.getStatus()) {
            movableService.update(getForRemove(data));
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
    public Result<Void> duplicate(final S data) {
        final Result<Void> result = childMovableValidator.validate(data, ValidationType.EXISTS);

        if (Status.OK == result.getStatus()) {
            movableService.update(getForDuplicate(data));
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
    public Result<Void> moveUp(final S data) {
        final Result<Void> result = childMovableValidator.validate(data, ValidationType.EXISTS, ValidationType.UP);

        if (Status.OK == result.getStatus()) {
            movableService.update(getForMove(data, true));
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
    public Result<Void> moveDown(final S data) {
        final Result<Void> result = childMovableValidator.validate(data, ValidationType.EXISTS, ValidationType.DOWN);

        if (Status.OK == result.getStatus()) {
            movableService.update(getForMove(data, false));
        }

        return result;
    }

    /**
     * Returns list of child for specified parent.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Parent is null</li>
     * <li>ID is null</li>
     * <li>Parent doesn't exist in data storage</li>
     * </ul>
     *
     * @param parent parent
     * @return result with list of data
     */
    @Override
    public Result<List<S>> find(final U parent) {
        final Result<Void> validationResult = parentMovableValidator.validate(parent, ValidationType.EXISTS);

        if (Status.OK == validationResult.getStatus()) {
            return Result.of(CollectionUtils.getSortedData(converter.convertCollection(getDomainList(parent), getEntityClass())));
        }

        final Result<List<S>> result = new Result<>();
        result.addEvents(validationResult.getEvents());

        return result;
    }

    /**
     * Returns service for movable data.
     *
     * @return service for movable data
     */
    protected MovableService<V> getMovableService() {
        return movableService;
    }

    /**
     * Returns data for add.
     *
     * @param data data
     * @return data for add
     */
    protected T getDataForAdd(final S data) {
        final T updatedData = converter.convert(data, getDomainClass());
        updatedData.setPosition(Integer.MAX_VALUE);

        return updatedData;
    }

    /**
     * Returns data for add.
     *
     * @param data data
     * @return data for add
     */
    protected T getDataForUpdate(final S data) {
        return converter.convert(data, getDomainClass());
    }

    /**
     * Returns domain data with specified ID.
     *
     * @param id ID
     * @return domain data with specified ID
     */
    protected abstract T getDomainData(Integer id);

    /**
     * Returns data for specified parent.
     *
     * @param parent parent
     * @return data for specified parent
     */
    protected abstract List<T> getDomainList(U parent);

    /**
     * Returns data for add.
     *
     * @param parent parent
     * @param data   data
     * @return data for add
     */
    protected abstract V getForAdd(U parent, T data);

    /**
     * Returns data for update.
     *
     * @param data data
     * @return data for update
     */
    protected abstract V getForUpdate(S data);

    /**
     * Returns data for remove.
     *
     * @param data data
     * @return data for remove
     */
    protected abstract V getForRemove(S data);

    /**
     * Returns data for duplicate.
     *
     * @param data data
     * @return data for duplicate
     */
    protected abstract V getForDuplicate(S data);

    /**
     * Returns data for duplicate.
     *
     * @param data data
     * @param up   true if moving data up
     * @return data for duplicate
     */
    protected abstract V getForMove(S data, boolean up);

    /**
     * Returns entity class.
     *
     * @return entity class.
     */
    protected abstract Class<S> getEntityClass();

    /**
     * Returns domain class.
     *
     * @return domain class.
     */
    protected abstract Class<T> getDomainClass();

}
