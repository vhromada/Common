package cz.vhromada.common.test.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Collections;
import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.facade.MovableParentFacade;
import cz.vhromada.result.Event;
import cz.vhromada.result.Result;
import cz.vhromada.result.Severity;
import cz.vhromada.result.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * An abstract class represents integration test for {@link MovableParentFacade}.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension.class)
@SuppressWarnings("JUnitTestMethodInProductSource")
public abstract class MovableParentFacadeIntegrationTest<T extends Movable, U extends Movable> {

    /**
     * Test method for {@link MovableParentFacade#newData()}.
     */
    @Test
    @DirtiesContext
    void newData() {
        clearReferencedData();

        final Result<Void> result = getMovableParentFacade().newData();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertNewRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#getAll()}.
     */
    @Test
    void getAll() {
        final Result<List<T>> result = getMovableParentFacade().getAll();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            assertDataListDeepEquals(result.getData(), getDataList());
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#get(Integer)}.
     */
    @Test
    void get() {
        for (int i = 1; i <= getDefaultDataCount(); i++) {
            final Result<T> result = getMovableParentFacade().get(i);
            final int index = i;

            assertSoftly(softly -> {
                softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
                assertDataDeepEquals(result.getData(), getDomainData(index));
                softly.assertThat(result.getEvents()).isEmpty();
            });
        }

        final Result<T> result = getMovableParentFacade().get(Integer.MAX_VALUE);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#get(Integer)} with null data.
     */
    @Test
    void get_NullData() {
        final Result<T> result = getMovableParentFacade().get(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(new Event(Severity.ERROR, "ID_NULL", "ID mustn't be null.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#add(Movable)}.
     */
    @Test
    @DirtiesContext
    void add() {
        final Result<Void> result = getMovableParentFacade().add(newData(null, null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDataDomainDeepEquals(getExpectedAddData(), getRepositoryData(getDefaultDataCount() + 1));
        assertAddRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#add(Movable)} with null data.
     */
    @Test
    void add_NullData() {
        final Result<Void> result = getMovableParentFacade().add(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#add(Movable)} with data with not null ID.
     */
    @Test
    void add_NotNullId() {
        final Result<Void> result = getMovableParentFacade().add(newData(1, null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_ID_NOT_NULL", "ID must be null.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#add(Movable)} with data with not null position.
     */
    @Test
    void add_NotNullPosition() {
        final Result<Void> result = getMovableParentFacade().add(newData(null, 1));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_POSITION_NOT_NULL", "Position must be null.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)}.
     */
    @Test
    @DirtiesContext
    void update() {
        final T data = getUpdateData(1);

        final Result<Void> result = getMovableParentFacade().update(data);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDataDeepEquals(data, getRepositoryData(1));
        assertUpdateRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)} with null data.
     */
    @Test
    void update_NullData() {
        final Result<Void> result = getMovableParentFacade().update(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)} with data with null ID.
     */
    @Test
    void update_NullId() {
        final Result<Void> result = getMovableParentFacade().update(newData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)} with data with null position.
     */
    @Test
    void update_NullPosition() {
        final T data = getUpdateData(1);
        data.setPosition(null);

        final Result<Void> result = getMovableParentFacade().update(data);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_POSITION_NULL", "Position mustn't be null.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)} with data with bad ID.
     */
    @Test
    void update_BadId() {
        final Result<Void> result = getMovableParentFacade().update(newData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#remove(Movable)}.
     */
    @Test
    @DirtiesContext
    void remove() {
        clearReferencedData();

        final Result<Void> result = getMovableParentFacade().remove(newData(1));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertThat(getRepositoryData(1)).isNull();
        assertRemoveRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#remove(Movable)} with null data.
     */
    @Test
    void remove_NullData() {
        final Result<Void> result = getMovableParentFacade().remove(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#remove(Movable)} with data with null ID.
     */
    @Test
    void remove_NullId() {
        final Result<Void> result = getMovableParentFacade().remove(newData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#remove(Movable)} with data with bad ID.
     */
    @Test
    void remove_BadId() {
        final Result<Void> result = getMovableParentFacade().remove(newData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#duplicate(Movable)}.
     */
    @Test
    @DirtiesContext
    void duplicate() {
        final Result<Void> result = getMovableParentFacade().duplicate(newData(getDefaultDataCount()));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDataDomainDeepEquals(getExpectedDuplicatedData(), getRepositoryData(getDefaultDataCount() + 1));
        assertDuplicateRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#duplicate(Movable)} with null data.
     */
    @Test
    void duplicate_NullData() {
        final Result<Void> result = getMovableParentFacade().duplicate(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#duplicate(Movable)} with data with null ID.
     */
    @Test
    void duplicate_NullId() {
        final Result<Void> result = getMovableParentFacade().duplicate(newData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#duplicate(Movable)} with data with bad ID.
     */
    @Test
    void duplicate_BadId() {
        final Result<Void> result = getMovableParentFacade().duplicate(newData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)}.
     */
    @Test
    @DirtiesContext
    void moveUp() {
        final Result<Void> result = getMovableParentFacade().moveUp(newData(2));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        final U data1 = getDomainData(1);
        data1.setPosition(1);
        final U data2 = getDomainData(2);
        data2.setPosition(0);
        assertDataDomainDeepEquals(data1, getRepositoryData(1));
        assertDataDomainDeepEquals(data2, getRepositoryData(2));
        for (int i = 3; i <= getDefaultDataCount(); i++) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i));
        }
        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)} with null data.
     */
    @Test
    void moveUp_NullData() {
        final Result<Void> result = getMovableParentFacade().moveUp(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)} with data with null ID.
     */
    @Test
    void moveUp_NullId() {
        final Result<Void> result = getMovableParentFacade().moveUp(newData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)} with not movable data.
     */
    @Test
    void moveUp_NotMovableData() {
        final Result<Void> result = getMovableParentFacade().moveUp(newData(1));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_NOT_MOVABLE", getName() + " can't be moved up.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)} with data with bad ID.
     */
    @Test
    void moveUp_BadId() {
        final Result<Void> result = getMovableParentFacade().moveUp(newData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)}.
     */
    @Test
    @DirtiesContext
    void moveDown() {
        final Result<Void> result = getMovableParentFacade().moveDown(newData(1));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        final U data1 = getDomainData(1);
        data1.setPosition(1);
        final U data2 = getDomainData(2);
        data2.setPosition(0);
        assertDataDomainDeepEquals(data1, getRepositoryData(1));
        assertDataDomainDeepEquals(data2, getRepositoryData(2));
        for (int i = 3; i <= getDefaultDataCount(); i++) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i));
        }
        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)} with null data.
     */
    @Test
    void moveDown_NullData() {
        final Result<Void> result = getMovableParentFacade().moveDown(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)} with data with null ID.
     */
    @Test
    void moveDown_NullId() {
        final Result<Void> result = getMovableParentFacade().moveDown(newData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)} with not movable data.
     */
    @Test
    void moveDown_NotMovableData() {
        final Result<Void> result = getMovableParentFacade().moveDown(newData(getDefaultDataCount()));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_NOT_MOVABLE", getName() + " can't be moved down.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)} with data with bad ID.
     */
    @Test
    void moveDown_BadId() {
        final Result<Void> result = getMovableParentFacade().moveDown(newData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableParentFacade#updatePositions()}.
     */
    @Test
    @DirtiesContext
    void updatePositions() {
        final Result<Void> result = getMovableParentFacade().updatePositions();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        for (int i = 1; i <= getDefaultDataCount(); i++) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i));
        }
        assertDefaultRepositoryData();
    }

    /**
     * Returns facade for movable data for parent data.
     *
     * @return facade for movable data for parent data
     */
    protected abstract MovableParentFacade<T> getMovableParentFacade();

    /**
     * Returns default count of data.
     *
     * @return default count of data
     */
    protected abstract Integer getDefaultDataCount();

    /**
     * Returns repository count of data.
     *
     * @return repository count of data
     */
    protected abstract Integer getRepositoryDataCount();

    /**
     * Returns list of data.
     *
     * @return list of data
     */
    protected abstract List<U> getDataList();

    /**
     * Returns domain data.
     *
     * @param index index
     * @return domain data
     */
    protected abstract U getDomainData(Integer index);

    /**
     * Returns new data.
     *
     * @param id ID
     * @return new data
     */
    protected abstract T newData(Integer id);

    /**
     * Returns domain data.
     *
     * @param id ID
     * @return domain data
     */
    protected abstract U newDomainData(Integer id);

    /**
     * Returns repository data.
     *
     * @param id ID
     * @return repository data
     */
    protected abstract U getRepositoryData(Integer id);

    /**
     * Returns name of entity.
     *
     * @return name of entity
     */
    protected abstract String getName();

    /**
     * Clears referenced data.
     */
    protected abstract void clearReferencedData();

    /**
     * Asserts list of data deep equals.
     *
     * @param expected expected
     * @param actual   actual
     */
    protected abstract void assertDataListDeepEquals(List<T> expected, List<U> actual);

    /**
     * Asserts data deep equals.
     *
     * @param expected expected
     * @param actual   actual
     */
    protected abstract void assertDataDeepEquals(T expected, U actual);

    /**
     * Asserts domain data deep equals.
     *
     * @param expected expected
     * @param actual   actual
     */
    protected abstract void assertDataDomainDeepEquals(U expected, U actual);

    /**
     * Returns update data.
     *
     * @param id ID
     * @return update data
     */
    @SuppressWarnings("SameParameterValue")
    protected T getUpdateData(final Integer id) {
        return newData(id, 0);
    }

    /**
     * Returns expected add data.
     *
     * @return expected add data
     */
    protected U getExpectedAddData() {
        return newDomainData(getDefaultDataCount() + 1);
    }

    /**
     * Returns expected duplicated data.
     *
     * @return expected duplicated data
     */
    protected U getExpectedDuplicatedData() {
        final U data = getDomainData(getDefaultDataCount());
        data.setId(getDefaultDataCount() + 1);

        return data;
    }

    /**
     * Asserts default repository data.
     */
    protected void assertDefaultRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount());
    }

    /**
     * Asserts repository data for {@link MovableParentFacade#newData()}.
     */
    protected void assertNewRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(0);
    }

    /**
     * Asserts repository data for {@link MovableParentFacade#add(Movable)}.
     */
    protected void assertAddRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount() + 1);
    }

    /**
     * Asserts repository data for {@link MovableParentFacade#update(Movable)}.
     */
    protected void assertUpdateRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount());
    }

    /**
     * Asserts repository data for {@link MovableParentFacade#remove(Movable)}.
     */
    protected void assertRemoveRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount() - 1);
    }

    /**
     * Asserts repository data for {@link MovableParentFacade#duplicate(Movable)}.
     */
    protected void assertDuplicateRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount() + 1);
    }

    /**
     * Returns new data.
     *
     * @param id       ID
     * @param position position
     * @return new data
     */
    private T newData(final Integer id, final Integer position) {
        final T data = newData(id);
        data.setPosition(position);

        return data;
    }

    /**
     * Returns event for null data.
     *
     * @return event for null data
     */
    private Event getNullDataEvent() {
        return new Event(Severity.ERROR, getPrefix() + "_NULL", getName() + " mustn't be null.");
    }

    /**
     * Returns event for data with null ID.
     *
     * @return event for data with null ID
     */
    private Event getNullDataIdEvent() {
        return new Event(Severity.ERROR, getPrefix() + "_ID_NULL", "ID mustn't be null.");
    }

    /**
     * Returns event for not existing data.
     *
     * @return event for not existing data
     */
    private Event getNotExistingDataEvent() {
        return new Event(Severity.ERROR, getPrefix() + "_NOT_EXIST", getName() + " doesn't exist.");
    }

    /**
     * Returns prefix for validation keys.
     *
     * @return prefix for validation keys
     */
    private String getPrefix() {
        return getName().toUpperCase();
    }

}
