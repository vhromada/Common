package cz.vhromada.common.service;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.utils.CollectionUtils;

import org.springframework.cache.Cache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
    private final JpaRepository<T, Integer> repository;

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
    public AbstractMovableService(final JpaRepository<T, Integer> repository, final Cache cache, final String key) {
        Assert.notNull(repository, "Repository mustn't be null.");
        Assert.notNull(cache, "Cache mustn't be null.");
        Assert.notNull(key, "Cache key mustn't be null.");

        this.repository = repository;
        this.cache = cache;
        this.key = key;
    }

    @Override
    public void newData() {
        repository.deleteAll();

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
        Assert.notNull(id, "ID mustn't be null.");

        final List<T> data = getCachedData(true);
        for (final T item : data) {
            if (id.equals(item.getId())) {
                return item;
            }
        }

        return null;
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void add(final T data) {
        Assert.notNull(data, NULL_DATA_MESSAGE);

        data.setPosition(0);
        final T savedData = repository.save(data);
        savedData.setPosition(savedData.getId() - 1);
        repository.save(savedData);

        final List<T> dataList = getCachedData(false);
        addItem(dataList, savedData);
        cache.put(key, dataList);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void update(final T data) {
        Assert.notNull(data, NULL_DATA_MESSAGE);

        final T savedData = repository.save(data);

        final List<T> dataList = getCachedData(false);
        updateItem(dataList, savedData);
        cache.put(key, dataList);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void remove(final T data) {
        Assert.notNull(data, NULL_DATA_MESSAGE);

        repository.delete(data);

        final List<T> dataList = getCachedData(false);
        dataList.remove(data);
        cache.put(key, dataList);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void duplicate(final T data) {
        Assert.notNull(data, NULL_DATA_MESSAGE);

        final T savedDataCopy = repository.save(getCopy(data));

        final List<T> dataList = getCachedData(false);
        addItem(dataList, savedDataCopy);
        cache.put(key, dataList);
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

        final List<T> savedData = repository.saveAll(data);

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
    private List<T> getCachedData(final boolean cached) {
        final Cache.ValueWrapper cacheValue = cache.get(key);
        if (cacheValue == null) {
            final List<T> data = repository.findAll();
            if (cached) {
                cache.put(key, data);
            }

            return data;
        }

        @SuppressWarnings("unchecked") final List<T> data = (List<T>) cacheValue.get();
        return data;
    }

    /**
     * Moves data in list one position up or down.
     *
     * @param data data
     * @param up   if moving data up
     * @throws IllegalArgumentException if data is null
     */
    private void move(final T data, final boolean up) {
        Assert.notNull(data, NULL_DATA_MESSAGE);

        final List<T> dataList = CollectionUtils.getSortedData(getCachedData(false));
        final int index = dataList.indexOf(data);
        final T other = dataList.get(up ? index - 1 : index + 1);
        final int position = data.getPosition();
        data.setPosition(other.getPosition());
        other.setPosition(position);

        final T savedData = repository.save(data);
        final T savedOther = repository.save(other);

        updateItem(dataList, savedData);
        updateItem(dataList, savedOther);
        cache.put(key, dataList);
    }

    /**
     * Adds item if list of data.
     *
     * @param data list of data
     * @param item adding item
     */
    private void addItem(final List<T> data, final T item) {
        if (!data.contains(item)) {
            data.add(item);
        }
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
