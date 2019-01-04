package cz.vhromada.common.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.repository.MovableRepository;
import cz.vhromada.common.service.MovableService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * An abstract class represents test for {@link MovableService}.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("JUnitTestMethodInProductSource")
public abstract class MovableServiceTest<T extends Movable> {

    /**
     * ID
     */
    private static final int ID = 5;

    /**
     * Instance of {@link Cache}
     */
    @Mock
    private Cache cache;

    /**
     * Instance of {@link MovableService}
     */
    private MovableService<T> service;

    /**
     * Instance of {@link MovableRepository}
     */
    private MovableRepository<T> repository;

    /**
     * Data list
     */
    private List<T> dataList;

    /**
     * Initializes data.
     */
    @BeforeEach
    public void setUp() {
        repository = getRepository();
        service = getService();
        dataList = List.of(getItem1(), getItem2());
    }

    /**
     * Test method for {@link MovableService#newData()}.
     */
    @Test
    void newData() {
        service.newData();

        verify(repository).removeAll();
        verify(cache).clear();
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#getAll()} with cached data.
     */
    @Test
    void getAll_CachedData() {
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        final List<T> data = service.getAll();

        assertThat(data).isEqualTo(dataList);

        verify(cache).get(getCacheKey());
        verifyNoMoreInteractions(cache);
        verifyZeroInteractions(repository);
    }

    /**
     * Test method for {@link MovableService#getAll()} with not cached data.
     */
    @Test
    void getAll_NotCachedData() {
        when(repository.getAll()).thenReturn(dataList);
        when(cache.get(any(String.class))).thenReturn(null);

        final List<T> data = service.getAll();

        assertThat(data).isEqualTo(dataList);

        verify(repository).getAll();
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#get(Integer)} with cached existing data.
     */
    @Test
    void get_CachedExistingData() {
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        final T data = service.get(dataList.get(0).getId());

        assertThat(data).isEqualTo(dataList.get(0));

        verify(cache).get(getCacheKey());
        verifyNoMoreInteractions(cache);
        verifyZeroInteractions(repository);
    }

    /**
     * Test method for {@link MovableService#get(Integer)} with cached not existing data.
     */
    @Test
    void get_CachedNotExistingData() {
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        final T data = service.get(Integer.MAX_VALUE);

        assertThat(data).isNull();

        verify(cache).get(getCacheKey());
        verifyNoMoreInteractions(cache);
        verifyZeroInteractions(repository);
    }

    /**
     * Test method for {@link MovableService#get(Integer)} with not cached existing data.
     */
    @Test
    void get_NotCachedExistingData() {
        when(repository.getAll()).thenReturn(dataList);
        when(cache.get(any(String.class))).thenReturn(null);

        final T data = service.get(dataList.get(0).getId());

        assertThat(data).isEqualTo(dataList.get(0));

        verify(repository).getAll();
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#get(Integer)} with not cached not existing data.
     */
    @Test
    void get_NotCachedNotExistingData() {
        when(repository.getAll()).thenReturn(dataList);
        when(cache.get(any(String.class))).thenReturn(null);

        final T data = service.get(Integer.MAX_VALUE);

        assertThat(data).isNull();

        verify(repository).getAll();
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#get(Integer)} with null data.
     */
    @Test
    void get_NullData() {
        assertThatThrownBy(() -> service.get(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#add(T)} with cached data.
     */
    @Test
    void add() {
        final T data = getAddItem();

        when(repository.add(any(getItemClass()))).thenAnswer(setIdAndPosition());
        when(repository.update(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);

        service.add(data);

        assertAddResult(data);

        verify(repository).add(data);
        verify(repository).update(data);
        verify(cache).clear();
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#add(T)} with null data.
     */
    @Test
    void add_NullData() {
        assertThatThrownBy(() -> service.add(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#update(T)} with cached data.
     */
    @Test
    void update_CachedData() {
        final T data = dataList.get(0);
        data.setPosition(10);

        when(repository.update(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        service.update(data);

        assertSoftly(softly -> {
            softly.assertThat(dataList.size()).isEqualTo(2);
            softly.assertThat(dataList.get(0)).isEqualTo(data);
        });

        verify(repository).update(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#update(T)} with not cached data.
     */
    @Test
    void update_NotCachedData() {
        final T data = dataList.get(0);
        data.setPosition(10);

        when(repository.getAll()).thenReturn(dataList);
        when(repository.update(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        service.update(data);

        assertSoftly(softly -> {
            softly.assertThat(dataList.size()).isEqualTo(2);
            softly.assertThat(dataList.get(0)).isEqualTo(data);
        });

        verify(repository).getAll();
        verify(repository).update(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#update(T)} with null data.
     */
    @Test
    void update_NullData() {
        assertThatThrownBy(() -> service.update(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#remove(T)} with cached data.
     */
    @Test
    @SuppressWarnings("unchecked")
    void remove_CachedData() {
        final T data = dataList.get(0);
        final ArgumentCaptor<List<T>> cacheArgumentCaptor = ArgumentCaptor.forClass(List.class);

        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        service.remove(data);

        verify(repository).remove(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(eq(getCacheKey()), cacheArgumentCaptor.capture());
        verifyNoMoreInteractions(repository, cache);

        final List<T> cacheData = cacheArgumentCaptor.getValue();
        assertSoftly(softly -> {
            softly.assertThat(cacheData.size()).isEqualTo(dataList.size() - 1);
            softly.assertThat(cacheData.contains(data)).isFalse();
        });
    }

    /**
     * Test method for {@link MovableService#remove(T)} with not cached data.
     */
    @Test
    @SuppressWarnings("unchecked")
    void remove_NotCachedData() {
        final T data = dataList.get(0);
        final ArgumentCaptor<List<T>> cacheArgumentCaptor = ArgumentCaptor.forClass(List.class);

        when(repository.getAll()).thenReturn(dataList);
        when(cache.get(any(String.class))).thenReturn(null);

        service.remove(data);

        verify(repository).getAll();
        verify(repository).remove(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(eq(getCacheKey()), cacheArgumentCaptor.capture());
        verifyNoMoreInteractions(repository, cache);

        final List<T> cacheData = cacheArgumentCaptor.getValue();
        assertSoftly(softly -> {
            softly.assertThat(cacheData.size()).isEqualTo(dataList.size() - 1);
            softly.assertThat(cacheData.contains(data)).isFalse();
        });
    }

    /**
     * Test method for {@link MovableService#remove(T)} with null data.
     */
    @Test
    void remove_NullData() {
        assertThatThrownBy(() -> service.remove(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#duplicate(T)} with cached data.
     */
    @Test
    void duplicate() {
        final T copy = getCopyItem();
        final ArgumentCaptor<T> copyArgumentCaptor = ArgumentCaptor.forClass(getItemClass());

        when(repository.add(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);

        service.duplicate(dataList.get(0));

        verify(repository).add(copyArgumentCaptor.capture());
        verify(cache).clear();
        verifyNoMoreInteractions(repository, cache);

        final T copyArgument = copyArgumentCaptor.getValue();
        assertDataDeepEquals(copy, copyArgument);
    }

    /**
     * Test method for {@link MovableService#duplicate(T)} with null data.
     */
    @Test
    void duplicate_NullData() {
        assertThatThrownBy(() -> service.duplicate(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#moveUp(T)} with cached data.
     */
    @Test
    void moveUp_CachedData() {
        final T data1 = dataList.get(0);
        final int position1 = data1.getPosition();
        final T data2 = dataList.get(1);
        final int position2 = data2.getPosition();

        when(repository.updateAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        service.moveUp(data2);

        assertSoftly(softly -> {
            softly.assertThat(data1.getPosition()).isEqualTo(position2);
            softly.assertThat(data2.getPosition()).isEqualTo(position1);
        });

        verify(repository).updateAll(List.of(data2, data1));
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#moveUp(T)} with not cached data.
     */
    @Test
    void moveUp_NotCachedData() {
        final T data1 = dataList.get(0);
        final int position1 = data1.getPosition();
        final T data2 = dataList.get(1);
        final int position2 = data2.getPosition();

        when(repository.getAll()).thenReturn(dataList);
        when(repository.updateAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        service.moveUp(data2);

        assertSoftly(softly -> {
            softly.assertThat(data1.getPosition()).isEqualTo(position2);
            softly.assertThat(data2.getPosition()).isEqualTo(position1);
        });

        verify(repository).getAll();
        verify(repository).updateAll(List.of(data2, data1));
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#moveUp(T)} with null data.
     */
    @Test
    void moveUp_NullData() {
        assertThatThrownBy(() -> service.moveUp(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#moveDown(T)} with cached data.
     */
    @Test
    void moveDown_CachedData() {
        final T data1 = dataList.get(0);
        final int position1 = data1.getPosition();
        final T data2 = dataList.get(1);
        final int position2 = data2.getPosition();

        when(repository.updateAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        service.moveDown(data1);

        assertSoftly(softly -> {
            softly.assertThat(data1.getPosition()).isEqualTo(position2);
            softly.assertThat(data2.getPosition()).isEqualTo(position1);
        });

        verify(repository).updateAll(List.of(data1, data2));
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#moveDown(T)} with not cached data.
     */
    @Test
    void moveDown_NotCachedData() {
        final T data1 = dataList.get(0);
        final int position1 = data1.getPosition();
        final T data2 = dataList.get(1);
        final int position2 = data2.getPosition();

        when(repository.getAll()).thenReturn(dataList);
        when(repository.updateAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        service.moveDown(data1);

        assertSoftly(softly -> {
            softly.assertThat(data1.getPosition()).isEqualTo(position2);
            softly.assertThat(data2.getPosition()).isEqualTo(position1);
        });

        verify(repository).getAll();
        verify(repository).updateAll(List.of(data1, data2));
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#moveDown(T)} with null data.
     */
    @Test
    void moveDown_NullData() {
        assertThatThrownBy(() -> service.moveDown(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#updatePositions()} with cached data.
     */
    @Test
    void updatePositions_CachedData() {
        when(repository.updateAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        service.updatePositions();

        for (int i = 0; i < dataList.size(); i++) {
            final T data = dataList.get(i);
            assertThat(data.getPosition()).isEqualTo(i);
        }

        verify(repository).updateAll(dataList);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#updatePositions()} with not cached data.
     */
    @Test
    void updatePositions_NotCachedData() {
        when(repository.getAll()).thenReturn(dataList);
        when(repository.updateAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        service.updatePositions();

        for (int i = 0; i < dataList.size(); i++) {
            final T data = dataList.get(i);
            assertThat(data.getPosition()).isEqualTo(i);
        }

        verify(repository).getAll();
        verify(repository).updateAll(dataList);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Returns instance of {@link Cache}.
     *
     * @return instance of {@link Cache}
     */
    protected Cache getCache() {
        return cache;
    }

    /**
     * Returns instance of {@link MovableRepository}.
     *
     * @return instance of {@link MovableRepository}
     */
    protected abstract MovableRepository<T> getRepository();

    /**
     * Returns instance of {@link MovableService}.
     *
     * @return instance of {@link MovableService}
     */
    protected abstract MovableService<T> getService();

    /**
     * Returns cache key.
     *
     * @return cache key
     */
    protected abstract String getCacheKey();

    /**
     * Returns 1st item in data list.
     *
     * @return 1st item in data list
     */
    protected abstract T getItem1();

    /**
     * Returns 2nd item in data list.
     *
     * @return 2nd item in data list
     */
    protected abstract T getItem2();

    /**
     * Returns add item
     *
     * @return add item
     */
    protected abstract T getAddItem();

    /**
     * Returns copy item.
     *
     * @return copy item
     */
    protected abstract T getCopyItem();

    /**
     * Returns item class.
     *
     * @return item class
     */
    protected abstract Class<T> getItemClass();

    /**
     * Asserts data deep equals.
     *
     * @param expected expected data
     * @param actual   actual data
     */
    protected abstract void assertDataDeepEquals(T expected, T actual);

    /**
     * Sets ID and position.
     *
     * @return mocked answer
     */
    private Answer<T> setIdAndPosition() {
        return invocation -> {
            final T movable = invocation.getArgument(0);
            movable.setId(ID);
            movable.setPosition(ID - 1);

            return movable;
        };
    }

    /**
     * Asserts result of {@link MovableService#add(T)}
     *
     * @param data add item
     */
    private void assertAddResult(final T data) {
        assertSoftly(softly -> {
            softly.assertThat(data.getId()).isEqualTo(ID);
            softly.assertThat(data.getPosition()).isEqualTo(ID - 1);
        });
    }

}
