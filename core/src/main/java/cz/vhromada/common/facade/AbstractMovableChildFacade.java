package cz.vhromada.common.facade;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.converter.MovableConverter;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.utils.CollectionUtils;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.common.validator.ValidationType;
import cz.vhromada.validation.result.Result;
import cz.vhromada.validation.result.Status;

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
    private MovableService<V> service;

    /**
     * Converter for movable data
     */
    private MovableConverter<S, T> converter;

    /**
     * Validator for movable data for parent data
     */
    private MovableValidator<U> parentValidator;

    /**
     * Validator for movable data for child data
     */
    private MovableValidator<S> childValidator;

    /**
     * Creates a new instance of AbstractMovableChildFacade.
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
    public AbstractMovableChildFacade(final MovableService<V> service, final MovableConverter<S, T> converter, final MovableValidator<U> parentValidator,
        final MovableValidator<S> childValidator) {
        if (service == null) {
            throw new IllegalArgumentException("Service for data mustn't be null.");
        }
        if (converter == null) {
            throw new IllegalArgumentException("Converter for data mustn't be null.");
        }
        if (parentValidator == null) {
            throw new IllegalArgumentException("Validator for movable data for parent data mustn't be null.");
        }
        if (childValidator == null) {
            throw new IllegalArgumentException("Validator for movable data for child data mustn't be null.");
        }

        this.service = service;
        this.converter = converter;
        this.parentValidator = parentValidator;
        this.childValidator = childValidator;
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
        return Result.of(converter.convertBack(getDomainData(id)));
    }

    /**
     * Adds data. Sets new ID and position.
     * <br>
     * Validation errors:
     * <ul>
     * <li>Parent is null</li>
     * <li>Parent ID is null</li>
     * <li>Parent doesn't exist in data storage</li>
     * <li>Data is null</li>
     * <li>Data ID isn't null</li>
     * <li>Data position isn't null</li>
     * <li>Deep data validation errors</li>
     * </ul>
     *
     * @param parent parent
     * @param data   data
     * @return result with validation errors
     */
    @Override
    public Result<Void> add(final U parent, final S data) {
        final Result<Void> result = parentValidator.validate(parent, ValidationType.EXISTS);
        result.addEvents(childValidator.validate(data, ValidationType.NEW, ValidationType.DEEP).getEvents());
        if (Status.OK == result.getStatus()) {
            service.update(getForAdd(parent, getDataForAdd(data)));
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
        final Result<Void> result = childValidator.validate(data, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP);
        if (Status.OK == result.getStatus()) {
            service.update(getForUpdate(data));
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
        final Result<Void> result = childValidator.validate(data, ValidationType.EXISTS);
        if (Status.OK == result.getStatus()) {
            service.update(getForRemove(data));
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
        final Result<Void> result = childValidator.validate(data, ValidationType.EXISTS);
        if (Status.OK == result.getStatus()) {
            service.update(getForDuplicate(data));
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
        final Result<Void> result = childValidator.validate(data, ValidationType.EXISTS, ValidationType.UP);
        if (Status.OK == result.getStatus()) {
            service.update(getForMove(data, true));
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
        final Result<Void> result = childValidator.validate(data, ValidationType.EXISTS, ValidationType.DOWN);
        if (Status.OK == result.getStatus()) {
            service.update(getForMove(data, false));
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
        final Result<Void> validationResult = parentValidator.validate(parent, ValidationType.EXISTS);
        if (Status.OK == validationResult.getStatus()) {
            return Result.of(CollectionUtils.getSortedData(converter.convertBack(getDomainList(parent))));
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
    protected MovableService<V> getService() {
        return service;
    }

    /**
     * Returns data for add.
     *
     * @param data data
     * @return data for add
     */
    protected T getDataForAdd(final S data) {
        final T updatedData = converter.convert(data);
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
        return converter.convert(data);
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

}
