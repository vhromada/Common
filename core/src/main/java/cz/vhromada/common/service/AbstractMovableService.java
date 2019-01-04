package cz.vhromada.common.service;

import java.util.ArrayList;
import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.repository.MovableRepository;
import cz.vhromada.common.utils.CollectionUtils;

import org.springframework.cache.Cache;
import org.springframework.transaction.annotation.Transactional;

/**
 * An abstract class represents service for movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
@Transactional
public abstract class AbstractMovableService<T extends Movable> implements MovableService<T> {

    /**
     * Message for invalid data
     */
    private static final String NULL_DATA_MESSAGE = "Data mustn't be null.";

    /**
     * Repository for data
     */
    private final MovableRepository<T> repository;

    /**
     * Cache for data
     */
    private final Cache cache;

    /**
     * Cache key
     */
    private final String key;

    /**
     * Creates a new instance of AbstractMovableService.
     *
     * @param repository repository for data
     * @param cache      cache for data
     * @param key        cache key
     * @throws IllegalArgumentException if repository for data is null
     *                                  or cache for data is null
     *                                  or cache key is null
     */
    public AbstractMovableService(final MovableRepository<T> repository, final Cache cache, final String key) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository for data mustn't be null.");
        }
        if (cache == null) {
            throw new IllegalArgumentException("Cache mustn't be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Cache key mustn't be null.");
        }

        this.repository = repository;
        this.cache = cache;
        this.key = key;
    }

    @Override
    public void newData() {
        repository.removeAll();
        cache.clear();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> getAll() {
        return CollectionUtils.getSortedData(getCachedData(true));
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public T get(final Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID mustn't be null.");
        }

        return getCachedData(true).stream()
            .filter(item -> id.equals(item.getId()))
            .findFirst()
            .orElse(null);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void add(final T data) {
        if (data == null) {
            throw new IllegalArgumentException(NULL_DATA_MESSAGE);
        }

        data.setPosition(0);
        final T addedData = repository.add(data);
        addedData.setPosition(addedData.getId() - 1);
        repository.update(addedData);
        cache.clear();
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void update(final T data) {
        if (data == null) {
            throw new IllegalArgumentException(NULL_DATA_MESSAGE);
        }

        final T updatedData = repository.update(data);
        final List<T> dataList = new ArrayList<>(getCachedData(false));
        updateItem(dataList, updatedData);
        cache.put(key, dataList);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void remove(final T data) {
        if (data == null) {
            throw new IllegalArgumentException(NULL_DATA_MESSAGE);
        }

        repository.remove(data);
        final List<T> dataList = new ArrayList<>(getCachedData(false));
        dataList.remove(data);
        cache.put(key, dataList);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void duplicate(final T data) {
        if (data == null) {
            throw new IllegalArgumentException(NULL_DATA_MESSAGE);
        }

        repository.add(getCopy(data));
        cache.clear();
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void moveUp(final T data) {
        move(data, true);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void moveDown(final T data) {
        move(data, false);
    }

    @Override
    public void updatePositions() {
        final List<T> data = CollectionUtils.getSortedData(getCachedData(false));
        updatePositions(data);
        final List<T> savedData = repository.updateAll(data);
        cache.put(key, savedData);
    }

    /**
     * Returns copy of data.
     *
     * @param data data
     * @return copy of data
     */
    protected abstract T getCopy(T data);

    /**
     * Updates positions.
     *
     * @param data data
     */
    protected void updatePositions(final List<T> data) {
        for (int i = 0; i < data.size(); i++) {
            final T item = data.get(i);
            item.setPosition(i);
        }
    }

    /**
     * Returns list of data.
     *
     * @param cached true if returned data from repository should be cached
     * @return list of data
     */
    @SuppressWarnings("unchecked")
    private List<T> getCachedData(final boolean cached) {
        final Cache.ValueWrapper cacheValue = cache.get(key);
        if (cacheValue == null) {
            final List<T> data = repository.getAll();
            if (cached) {
                cache.put(key, data);
            }
            return data;
        }
        return (List<T>) cacheValue.get();
    }

    /**
     * Moves data in list one position up or down.
     *
     * @param data data
     * @param up   if moving data up
     * @throws IllegalArgumentException if data is null
     */
    private void move(final T data, final boolean up) {
        if (data == null) {
            throw new IllegalArgumentException(NULL_DATA_MESSAGE);
        }

        final List<T> dataList = CollectionUtils.getSortedData(getCachedData(false));
        final int index = dataList.indexOf(data);
        final T other = dataList.get(up ? index - 1 : index + 1);
        final int position = data.getPosition();
        data.setPosition(other.getPosition());
        other.setPosition(position);
        final List<T> updatedData = repository.updateAll(List.of(data, other));
        updateItem(dataList, updatedData.get(0));
        updateItem(dataList, updatedData.get(1));
        cache.put(key, dataList);
    }

    /**
     * Updates item if list of data.
     *
     * @param data list of data
     * @param item updating item
     */
    private void updateItem(final List<T> data, final T item) {
        final int index = data.indexOf(item);
        data.remove(item);
        data.add(index, item);
    }

}
