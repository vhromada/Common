package cz.vhromada.common.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.utils.CollectionUtils;
import cz.vhromada.test.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.jpa.repository.JpaRepository;

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
    private MovableService<T> movableService;

    /**
     * Instance of {@link JpaRepository}
     */
    private JpaRepository<T, Integer> repository;

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
        movableService = getMovableService();
        dataList = CollectionUtils.newList(getItem1(), getItem2());

        when(repository.findAll()).thenReturn(dataList);
    }

    /**
     * Test method for {@link MovableService#newData()}.
     */
    @Test
    void newData_CachedData() {
        movableService.newData();

        verify(repository).deleteAll();
        verify(cache).clear();
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#getAll()} with cached data.
     */
    @Test
    void getAll_CachedData() {
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        final List<T> data = movableService.getAll();

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
        when(cache.get(any(String.class))).thenReturn(null);

        final List<T> data = movableService.getAll();

        assertThat(data).isEqualTo(dataList);

        verify(repository).findAll();
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

        final T data = movableService.get(dataList.get(0).getId());

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

        final T data = movableService.get(Integer.MAX_VALUE);

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
        when(cache.get(any(String.class))).thenReturn(null);

        final T data = movableService.get(dataList.get(0).getId());

        assertThat(data).isEqualTo(dataList.get(0));

        verify(repository).findAll();
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#get(Integer)} with not cached not existing data.
     */
    @Test
    void get_NotCachedNotExistingData() {
        when(cache.get(any(String.class))).thenReturn(null);

        final T data = movableService.get(Integer.MAX_VALUE);

        assertThat(data).isNull();

        verify(repository).findAll();
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#get(Integer)} with null data.
     */
    @Test
    void get_NullData() {
        assertThatThrownBy(() -> movableService.get(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#add(T)} with cached data.
     */
    @Test
    void add_CachedData() {
        final T data = getAddItem();

        when(repository.save(any(getItemClass()))).thenAnswer(setIdAndPosition());
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        movableService.add(data);

        assertAddResult(data);

        verify(repository).save(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#add(T)} with not cached data.
     */
    @Test
    void add_NotCachedData() {
        final T data = getAddItem();

        when(repository.save(any(getItemClass()))).thenAnswer(setIdAndPosition());
        when(cache.get(any(String.class))).thenReturn(null);

        movableService.add(data);

        assertAddResult(data);

        verify(repository).findAll();
        verify(repository).save(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#add(T)} with null data.
     */
    @Test
    void add_NullData() {
        assertThatThrownBy(() -> movableService.add(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#update(T)} with cached data.
     */
    @Test
    void update_CachedData() {
        final T data = dataList.get(0);
        data.setPosition(10);

        when(repository.save(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        movableService.update(data);

        assertSoftly(softly -> {
            softly.assertThat(dataList.size()).isEqualTo(2);
            softly.assertThat(dataList.get(0)).isEqualTo(data);
        });

        verify(repository).save(data);
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

        when(repository.save(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        movableService.update(data);

        assertSoftly(softly -> {
            softly.assertThat(dataList.size()).isEqualTo(2);
            softly.assertThat(dataList.get(0)).isEqualTo(data);
        });

        verify(repository).findAll();
        verify(repository).save(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#update(T)} with null data.
     */
    @Test
    void update_NullData() {
        assertThatThrownBy(() -> movableService.update(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#remove(T)} with cached data.
     */
    @Test
    void remove_CachedData() {
        final T data = dataList.get(0);

        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        movableService.remove(data);

        assertSoftly(softly -> {
            softly.assertThat(dataList.size()).isEqualTo(1);
            softly.assertThat(dataList.contains(data)).isFalse();
        });

        verify(repository).delete(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#remove(T)} with not cached data.
     */
    @Test
    void remove_NotCachedData() {
        final T data = dataList.get(0);

        when(cache.get(any(String.class))).thenReturn(null);

        movableService.remove(data);

        assertSoftly(softly -> {
            softly.assertThat(dataList.size()).isEqualTo(1);
            softly.assertThat(dataList.contains(data)).isFalse();
        });

        verify(repository).findAll();
        verify(repository).delete(data);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#remove(T)} with null data.
     */
    @Test
    void remove_NullData() {
        assertThatThrownBy(() -> movableService.remove(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#duplicate(T)} with cached data.
     */
    @Test
    void duplicate_CachedData() {
        final T copy = getCopyItem();
        final ArgumentCaptor<T> copyArgumentCaptor = ArgumentCaptor.forClass(getItemClass());

        when(repository.save(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        movableService.duplicate(dataList.get(0));

        assertSoftly(softly -> {
            softly.assertThat(dataList.size()).isEqualTo(3);
            assertDataDeepEquals(copy, dataList.get(2));
        });

        verify(repository).save(copyArgumentCaptor.capture());
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);

        final T copyArgument = copyArgumentCaptor.getValue();
        assertDataDeepEquals(copy, copyArgument);
    }

    /**
     * Test method for {@link MovableService#duplicate(T)} with not cached data.
     */
    @Test
    void duplicate_NotCachedData() {
        final T copy = getCopyItem();
        final ArgumentCaptor<T> copyArgumentCaptor = ArgumentCaptor.forClass(getItemClass());

        when(repository.save(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        movableService.duplicate(dataList.get(0));

        assertSoftly(softly -> {
            softly.assertThat(dataList.size()).isEqualTo(3);
            assertDataDeepEquals(copy, dataList.get(2));
        });

        verify(repository).findAll();
        verify(repository).save(copyArgumentCaptor.capture());
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);

        final T copyArgument = copyArgumentCaptor.getValue();
        assertDataDeepEquals(copy, copyArgument);
    }

    /**
     * Test method for {@link MovableService#duplicate(T)} with null data.
     */
    @Test
    void duplicate_NullData() {
        assertThatThrownBy(() -> movableService.duplicate(null)).isInstanceOf(IllegalArgumentException.class);
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

        when(repository.save(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        movableService.moveUp(data2);

        assertSoftly(softly -> {
            softly.assertThat(data1.getPosition()).isEqualTo(position2);
            softly.assertThat(data2.getPosition()).isEqualTo(position1);
        });

        verify(repository).save(data1);
        verify(repository).save(data2);
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

        when(repository.save(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        movableService.moveUp(data2);

        assertSoftly(softly -> {
            softly.assertThat(data1.getPosition()).isEqualTo(position2);
            softly.assertThat(data2.getPosition()).isEqualTo(position1);
        });

        verify(repository).findAll();
        verify(repository).save(data1);
        verify(repository).save(data2);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#moveUp(T)} with null data.
     */
    @Test
    void moveUp_NullData() {
        assertThatThrownBy(() -> movableService.moveUp(null)).isInstanceOf(IllegalArgumentException.class);
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

        when(repository.save(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        movableService.moveDown(data1);

        assertSoftly(softly -> {
            softly.assertThat(data1.getPosition()).isEqualTo(position2);
            softly.assertThat(data2.getPosition()).isEqualTo(position1);
        });

        verify(repository).save(data1);
        verify(repository).save(data2);
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

        when(repository.save(any(getItemClass()))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        movableService.moveDown(data1);

        assertSoftly(softly -> {
            softly.assertThat(data1.getPosition()).isEqualTo(position2);
            softly.assertThat(data2.getPosition()).isEqualTo(position1);
        });

        verify(repository).findAll();
        verify(repository).save(data1);
        verify(repository).save(data2);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#moveDown(T)} with null data.
     */
    @Test
    void moveDown_NullData() {
        assertThatThrownBy(() -> movableService.moveDown(null)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link MovableService#updatePositions()} with cached data.
     */
    @Test
    void updatePositions_CachedData() {
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(new SimpleValueWrapper(dataList));

        movableService.updatePositions();

        for (int i = 0; i < dataList.size(); i++) {
            final T data = dataList.get(i);
            assertThat(data.getPosition()).isEqualTo(i);
        }

        verify(repository).saveAll(dataList);
        verify(cache).get(getCacheKey());
        verify(cache).put(getCacheKey(), dataList);
        verifyNoMoreInteractions(repository, cache);
    }

    /**
     * Test method for {@link MovableService#updatePositions()} with not cached data.
     */
    @Test
    void updatePositions_NotCachedData() {
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(cache.get(any(String.class))).thenReturn(null);

        movableService.updatePositions();

        for (int i = 0; i < dataList.size(); i++) {
            final T data = dataList.get(i);
            assertThat(data.getPosition()).isEqualTo(i);
        }

        verify(repository).findAll();
        verify(repository).saveAll(dataList);
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
     * Returns instance of {@link JpaRepository}.
     *
     * @return instance of {@link JpaRepository}
     */
    protected abstract JpaRepository<T, Integer> getRepository();

    /**
     * Returns instance of {@link MovableService}.
     *
     * @return instance of {@link MovableService}
     */
    protected abstract MovableService<T> getMovableService();

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
            softly.assertThat(dataList.size()).isEqualTo(3);
            softly.assertThat(dataList.get(2)).isEqualTo(data);
        });
    }

}
