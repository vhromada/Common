package cz.vhromada.common.test.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Collections;
import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.facade.MovableChildFacade;
import cz.vhromada.result.Event;
import cz.vhromada.result.Result;
import cz.vhromada.result.Severity;
import cz.vhromada.result.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * An abstract class represents integration test for {@link MovableChildFacade}.
 *
 * @param <T> type of child entity data
 * @param <U> type of child domain data
 * @param <V> type of parent entity data
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension.class)
@SuppressWarnings("JUnitTestMethodInProductSource")
public abstract class MovableChildFacadeIntegrationTest<T extends Movable, U extends Movable, V extends Movable> {

    /**
     * Null ID message
     */
    private static final String NULL_ID_MESSAGE = "ID mustn't be null.";

    /**
     * Test method for {@link MovableChildFacade#get(Integer)}.
     */
    @Test
    void get() {
        for (int i = 1; i <= getDefaultChildDataCount(); i++) {
            final Result<T> result = getMovableChildFacade().get(i);
            final int index = i;

            assertSoftly(softly -> {
                softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
                assertDataDeepEquals(result.getData(), getDomainData(index));
                softly.assertThat(result.getEvents()).isEmpty();
            });
        }

        final Result<T> result = getMovableChildFacade().get(Integer.MAX_VALUE);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#get(Integer)} with null data.
     */
    @Test
    void get_NullData() {
        final Result<T> result = getMovableChildFacade().get(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(new Event(Severity.ERROR, "ID_NULL", NULL_ID_MESSAGE)));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#add(Movable, Movable)}.
     */
    @Test
    @DirtiesContext
    void add() {
        final U expectedData = newDomainData(getDefaultChildDataCount() + 1);
        expectedData.setPosition(Integer.MAX_VALUE);

        final Result<Void> result = getMovableChildFacade().add(newParentData(1), newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDataDomainDeepEquals(expectedData, getRepositoryData(getDefaultChildDataCount() + 1));
        assertAddRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#add(Movable, Movable)} with null parent.
     */
    @Test
    void add_NullParent() {
        final Result<Void> result = getMovableChildFacade().add(null, newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullParentDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#add(Movable, Movable)} with parent with null ID.
     */
    @Test
    void add_NullId() {
        final Result<Void> result = getMovableChildFacade().add(newParentData(null), newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullParentDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#add(Movable, Movable)} with with not existing parent.
     */
    @Test
    void add_NotExistingParent() {
        final Result<Void> result = getMovableChildFacade().add(newParentData(Integer.MAX_VALUE), newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingParentDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#add(Movable, Movable)} with null child.
     */
    @Test
    void add_NullChild() {
        final Result<Void> result = getMovableChildFacade().add(newParentData(1), null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#add(Movable, Movable)} with child with null ID.
     */
    @Test
    void add_NotNullId() {
        final Result<Void> result = getMovableChildFacade().add(newParentData(1), newChildData(1));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getChildPrefix() + "_ID_NOT_NULL", "ID must be null.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#update(Movable)}.
     */
    @Test
    @DirtiesContext
    void update() {
        final T data = newChildData(1);

        final Result<Void> result = getMovableChildFacade().update(data);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDataDeepEquals(data, getRepositoryData(1));
        assertUpdateRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#update(Movable)} with null data.
     */
    @Test
    void update_NullData() {
        final Result<Void> result = getMovableChildFacade().update(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#update(Movable)} with data with null ID.
     */
    @Test
    void update_NullId() {
        final Result<Void> result = getMovableChildFacade().update(newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#update(Movable)} with data with null position.
     */
    @Test
    void update_NullPosition() {
        final T data = newChildData(1);
        data.setPosition(null);

        final Result<Void> result = getMovableChildFacade().update(data);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getChildPrefix() + "_POSITION_NULL", "Position mustn't be null.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#update(Movable)} with data with bad ID.
     */
    @Test
    void update_BadId() {
        final Result<Void> result = getMovableChildFacade().update(newChildData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#remove(Movable)}.
     */
    @Test
    @DirtiesContext
    void remove() {
        final Result<Void> result = getMovableChildFacade().remove(newChildData(1));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertThat(getRepositoryData(1)).isNull();
        assertRemoveRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#remove(Movable)} with null data.
     */
    @Test
    void remove_NullData() {
        final Result<Void> result = getMovableChildFacade().remove(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#remove(Movable)} with data with null ID.
     */
    @Test
    void remove_NullId() {
        final Result<Void> result = getMovableChildFacade().remove(newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#remove(Movable)} with data with bad ID.
     */
    @Test
    void remove_BadId() {
        final Result<Void> result = getMovableChildFacade().remove(newChildData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#duplicate(Movable)}.
     */
    @Test
    @DirtiesContext
    void duplicate() {
        final Result<Void> result = getMovableChildFacade().duplicate(newChildData(1));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        assertDataDomainDeepEquals(getExpectedDuplicatedData(), getRepositoryData(getDefaultChildDataCount() + 1));
        assertDuplicateRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#duplicate(Movable)} with null data.
     */
    @Test
    void duplicate_NullData() {
        final Result<Void> result = getMovableChildFacade().duplicate(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#duplicate(Movable)} with data with null ID.
     */
    @Test
    void duplicate_NullId() {
        final Result<Void> result = getMovableChildFacade().duplicate(newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#duplicate(Movable)} with data with bad ID.
     */
    @Test
    void duplicate_BadId() {
        final Result<Void> result = getMovableChildFacade().duplicate(newChildData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveUp(Movable)}.
     */
    @Test
    @DirtiesContext
    void moveUp() {
        final Result<Void> result = getMovableChildFacade().moveUp(newChildData(2));

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
        for (int i = 3; i <= getDefaultChildDataCount(); i++) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i));
        }
        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveUp(Movable)} with null data.
     */
    @Test
    void moveUp_NullData() {
        final Result<Void> result = getMovableChildFacade().moveUp(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveUp(Movable)} with data with null ID.
     */
    @Test
    void moveUp_NullId() {
        final Result<Void> result = getMovableChildFacade().moveUp(newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveUp(Movable)} with not movable data.
     */
    @Test
    void moveUp_NotMovableData() {
        final Result<Void> result = getMovableChildFacade().moveUp(newChildData(1));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getChildPrefix() + "_NOT_MOVABLE", getChildName() + " can't be moved up.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveUp(Movable)} with data with bad ID.
     */
    @Test
    void moveUp_BadId() {
        final Result<Void> result = getMovableChildFacade().moveUp(newChildData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveDown(Movable)}.
     */
    @Test
    @DirtiesContext
    void moveDown() {
        final Result<Void> result = getMovableChildFacade().moveDown(newChildData(1));

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
        for (int i = 3; i <= getDefaultChildDataCount(); i++) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i));
        }
        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveDown(Movable)} with null data.
     */
    @Test
    void moveDown_NullData() {
        final Result<Void> result = getMovableChildFacade().moveDown(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveDown(Movable)} with data with null ID.
     */
    @Test
    void moveDown_NullId() {
        final Result<Void> result = getMovableChildFacade().moveDown(newChildData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullChildDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveDown(Movable)} with not movable data.
     */
    @Test
    void moveDown_NotMovableData() {
        final Result<Void> result = getMovableChildFacade().moveDown(newChildData(getDefaultChildDataCount()));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getChildPrefix() + "_NOT_MOVABLE", getChildName() + " can't be moved down.")));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#moveDown(Movable)} with data with bad ID.
     */
    @Test
    void moveDown_BadId() {
        final Result<Void> result = getMovableChildFacade().moveDown(newChildData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingChildDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#find(Movable)}.
     */
    @Test
    void find() {
        for (int i = 1; i <= getDefaultParentDataCount(); i++) {
            final Result<List<T>> result = getMovableChildFacade().find(newParentData(i));
            final int index = i;

            assertSoftly(softly -> {
                softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
                assertDataListDeepEquals(result.getData(), getDataList(index));
                softly.assertThat(result.getEvents()).isEmpty();
            });
        }

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#find(Movable)} with null parent.
     */
    @Test
    void find_NullParent() {
        final Result<List<T>> result = getMovableChildFacade().find(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullParentDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#find(Movable)} with parent with null ID.
     */
    @Test
    void find_NullId() {
        final Result<List<T>> result = getMovableChildFacade().find(newParentData(null));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNullParentDataIdEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Test method for {@link MovableChildFacade#find(Movable)} with parent with bad ID.
     */
    @Test
    void find_BadId() {
        final Result<List<T>> result = getMovableChildFacade().find(newParentData(Integer.MAX_VALUE));

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(getNotExistingParentDataEvent()));
        });

        assertDefaultRepositoryData();
    }

    /**
     * Returns facade for movable data for child data.
     *
     * @return facade for movable data for child data
     */
    protected abstract MovableChildFacade<T, V> getMovableChildFacade();

    /**
     * Returns default count of parent data.
     *
     * @return default count of parent data
     */
    protected abstract Integer getDefaultParentDataCount();

    /**
     * Returns default count of child data.
     *
     * @return default count of child data
     */
    protected abstract Integer getDefaultChildDataCount();

    /**
     * Returns repository parent count of data.
     *
     * @return repository parent count of data
     */
    protected abstract Integer getRepositoryParentDataCount();

    /**
     * Returns repository child count of data.
     *
     * @return repository child count of data
     */
    protected abstract Integer getRepositoryChildDataCount();

    /**
     * Returns list of data.
     *
     * @param parentId parent ID
     * @return list of data
     */
    protected abstract List<U> getDataList(Integer parentId);

    /**
     * Returns domain data.
     *
     * @param index index
     * @return domain data
     */
    protected abstract U getDomainData(Integer index);

    /**
     * Returns new parent data.
     *
     * @param id ID
     * @return new parent data
     */
    protected abstract V newParentData(Integer id);

    /**
     * Returns new child data.
     *
     * @param id ID
     * @return new child data
     */
    protected abstract T newChildData(Integer id);

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
     * Returns name of parent entity.
     *
     * @return name of parent entity
     */
    protected abstract String getParentName();

    /**
     * Returns name of child entity.
     *
     * @return name of child entity
     */
    protected abstract String getChildName();

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
     * Returns expected duplicated data.
     *
     * @return expected duplicated data
     */
    protected U getExpectedDuplicatedData() {
        final U data = getDomainData(1);
        data.setId(getDefaultChildDataCount() + 1);

        return data;
    }

    /**
     * Asserts default repository data.
     */
    protected void assertDefaultRepositoryData() {
        assertSoftly(softly -> {
            softly.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount());
            assertReferences();
        });
    }

    /**
     * Asserts repository data for {@link MovableChildFacade#add(Movable, Movable)}.
     */
    private void assertAddRepositoryData() {
        assertSoftly(softly -> {
            softly.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount() + 1);
            assertReferences();
        });
    }

    /**
     * Asserts repository data for {@link MovableChildFacade#update(Movable)}.
     */
    protected void assertUpdateRepositoryData() {
        assertSoftly(softly -> {
            softly.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount());
            assertReferences();
        });
    }

    /**
     * Asserts repository data for {@link MovableChildFacade#remove(Movable)}.
     */
    protected void assertRemoveRepositoryData() {
        assertSoftly(softly -> {
            softly.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount() - 1);
            assertReferences();
        });
    }

    /**
     * Asserts repository data for {@link MovableChildFacade#duplicate(Movable)}.
     */
    protected void assertDuplicateRepositoryData() {
        assertSoftly(softly -> {
            softly.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount() + 1);
            assertReferences();
        });
    }

    /**
     * Asserts references.
     */
    protected void assertReferences() {
        assertThat(getRepositoryParentDataCount()).isEqualTo(getDefaultParentDataCount());
    }

    /**
     * Returns event for null parent data.
     *
     * @return event for null parent data
     */
    private Event getNullParentDataEvent() {
        return new Event(Severity.ERROR, getParentPrefix() + "_NULL", getParentName() + " mustn't be null.");
    }

    /**
     * Returns event for parent data with null ID.
     *
     * @return event for parent data with null ID
     */
    private Event getNullParentDataIdEvent() {
        return new Event(Severity.ERROR, getParentPrefix() + "_ID_NULL", NULL_ID_MESSAGE);
    }

    /**
     * Returns event for not existing parent data.
     *
     * @return event for not existing parent data
     */
    private Event getNotExistingParentDataEvent() {
        return new Event(Severity.ERROR, getParentPrefix() + "_NOT_EXIST", getParentName() + " doesn't exist.");
    }

    /**
     * Returns event for null child data.
     *
     * @return event for null child data
     */
    private Event getNullChildDataEvent() {
        return new Event(Severity.ERROR, getChildPrefix() + "_NULL", getChildName() + " mustn't be null.");
    }

    /**
     * Returns event for child data with null ID.
     *
     * @return event for child data with null ID
     */
    private Event getNullChildDataIdEvent() {
        return new Event(Severity.ERROR, getChildPrefix() + "_ID_NULL", NULL_ID_MESSAGE);
    }

    /**
     * Returns event for not existing child data.
     *
     * @return event for not existing child data
     */
    private Event getNotExistingChildDataEvent() {
        return new Event(Severity.ERROR, getChildPrefix() + "_NOT_EXIST", getChildName() + " doesn't exist.");
    }

    /**
     * Returns parent prefix for validation keys.
     *
     * @return parent prefix for validation keys
     */
    private String getParentPrefix() {
        return getParentName().toUpperCase();
    }

    /**
     * Returns child prefix for validation keys.
     *
     * @return child prefix for validation keys
     */
    private String getChildPrefix() {
        return getChildName().toUpperCase();
    }

}
