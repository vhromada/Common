package cz.vhromada.common.test.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.converter.MovableConverter;
import cz.vhromada.common.facade.MovableChildFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.common.validator.ValidationType;
import cz.vhromada.validation.result.Event;
import cz.vhromada.validation.result.Result;
import cz.vhromada.validation.result.Severity;
import cz.vhromada.validation.result.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * An abstract class represents test for {@link MovableChildFacade}.
 *
 * @param <S> type of child entity data
 * @param <T> type of child domain data
 * @param <U> type of parent entity data
 * @param <V> type of parent domain data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("JUnitTestMethodInProductSource")
public abstract class MovableChildFacadeTest<S extends Movable, T extends Movable, U extends Movable, V extends Movable> {

    /**
     * Result for invalid data
     */
    private static final Result<Void> INVALID_DATA_RESULT = Result.error("DATA_INVALID", "Data must be valid.");

    /**
     * Instance of {@link MovableService}
     */
    @Mock
    private MovableService<V> service;

    /**
     * Instance of {@link MovableConverter}
     */
    @Mock
    private MovableConverter<S, T> converter;

    /**
     * Instance of {@link MovableValidator}
     */
    @Mock
    private MovableValidator<U> parentValidator;

    /**
     * Instance of {@link MovableValidator}
     */
    @Mock
    private MovableValidator<S> childValidator;

    /**
     * Instance of {@link MovableChildFacade}
     */
    private MovableChildFacade<S, U> facade;

    /**
     * Initializes facade for movable data.
     */
    @BeforeEach
    public void setUp() {
        facade = getFacade();
    }

    /**
     * Test method for {@link MovableChildFacade#get(Integer)} with existing data.
     */
    @Test
    void get_ExistingData() {
        final S childEntity = newChildEntity(1);

        when(service.getAll()).thenReturn(List.of(newParentDomain(1)));
        when(converter.convertBack(any(getChildDomainClass()))).thenReturn(childEntity);

        final Result<S> result = facade.get(1);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isEqualTo(childEntity);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).getAll();
        verify(converter).convertBack(newChildDomain(1));
        verifyNoMoreInteractions(service, converter);
        verifyZeroInteractions(parentValidator, childValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#get(Integer)} with not existing data.
     */
    @Test
    void get_NotExistingData() {
        when(service.getAll()).thenReturn(List.of(newParentDomain(1)));

        final Result<S> result = facade.get(Integer.MAX_VALUE);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).getAll();
        verify(converter).convertBack((T) null);
        verifyNoMoreInteractions(service, converter);
        verifyZeroInteractions(parentValidator, childValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#get(Integer)} with null data.
     */
    @Test
    void get_NullData() {
        final Result<S> result = facade.get(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(new Event(Severity.ERROR, "ID_NULL", "ID mustn't be null.")));
        });

        verifyZeroInteractions(service, converter, parentValidator, childValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#add(Movable, Movable)}.
     */
    @Test
    void add() {
        final U parentEntity = newParentEntity(1);
        final S childEntity = newChildEntity(null);
        final T childDomain = newChildDomain(null);
        final ArgumentCaptor<V> argumentCaptor = ArgumentCaptor.forClass(getParentDomainClass());

        if (isFirstChild()) {
            when(service.get(any(Integer.class))).thenReturn(newParentDomain(1));
        } else {
            when(service.getAll()).thenReturn(List.of(newParentDomain(1)));
        }
        when(converter.convert(any(getChildEntityClass()))).thenReturn(childDomain);
        when(parentValidator.validate(any(getParentEntityClass()), any())).thenReturn(new Result<>());
        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.add(parentEntity, childEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        if (isFirstChild()) {
            verify(service).get(parentEntity.getId());
        } else {
            verify(service).getAll();
        }
        verify(service).update(argumentCaptor.capture());
        verify(parentValidator).validate(parentEntity, ValidationType.EXISTS);
        verify(childValidator).validate(childEntity, ValidationType.NEW, ValidationType.DEEP);
        verify(converter).convert(childEntity);
        verifyNoMoreInteractions(service, converter, parentValidator, childValidator);

        assertParentDeepEquals(newParentDomainWithChildren(1, List.of(newChildDomain(1), childDomain)), argumentCaptor.getValue());
    }

    /**
     * Test method for {@link MovableChildFacade#add(Movable, Movable)} with invalid data.
     */
    @Test
    void add_InvalidData() {
        final U parentEntity = newParentEntity(Integer.MAX_VALUE);
        final S childEntity = newChildEntity(null);
        final Result<Void> invalidParentResult = Result.error("PARENT_INVALID", "Parent must be valid.");
        final Result<Void> invalidChildResult = Result.error("CHILD_INVALID", "Child must be valid.");

        when(parentValidator.validate(any(getParentEntityClass()), any())).thenReturn(invalidParentResult);
        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(invalidChildResult);

        final Result<Void> result = facade.add(parentEntity, childEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents()).isEqualTo(Arrays.asList(invalidParentResult.getEvents().get(0), invalidChildResult.getEvents().get(0)));
        });

        verify(parentValidator).validate(parentEntity, ValidationType.EXISTS);
        verify(childValidator).validate(childEntity, ValidationType.NEW, ValidationType.DEEP);
        verifyNoMoreInteractions(parentValidator, childValidator);
        verifyZeroInteractions(service, converter);
    }

    /**
     * Test method for {@link MovableChildFacade#update(Movable)}.
     */
    @Test
    void update() {
        final S childEntity = newChildEntity(1);
        final T childDomain = newChildDomain(1);
        final V parentDomain = newParentDomain(1);
        final ArgumentCaptor<V> argumentCaptor = ArgumentCaptor.forClass(getParentDomainClass());

        when(service.getAll()).thenReturn(List.of(parentDomain));
        when(converter.convert(any(getChildEntityClass()))).thenReturn(childDomain);
        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.update(childEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).getAll();
        verify(service).update(argumentCaptor.capture());
        verify(converter).convert(childEntity);
        verify(childValidator).validate(childEntity, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP);
        verifyNoMoreInteractions(service, converter, childValidator);
        verifyZeroInteractions(parentValidator);

        assertParentDeepEquals(parentDomain, argumentCaptor.getValue());
    }

    /**
     * Test method for {@link MovableChildFacade#update(Movable)} with invalid data.
     */
    @Test
    void update_InvalidData() {
        final S childEntity = newChildEntity(Integer.MAX_VALUE);

        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.update(childEntity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(childValidator).validate(childEntity, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP);
        verifyNoMoreInteractions(childValidator);
        verifyZeroInteractions(service, converter, parentValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#remove(Movable)}.
     */
    @Test
    void remove() {
        final S childEntity = newChildEntity(1);
        final V parentDomain = newParentDomain(1);
        final ArgumentCaptor<V> argumentCaptor = ArgumentCaptor.forClass(getParentDomainClass());

        when(service.getAll()).thenReturn(List.of(parentDomain));
        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.remove(childEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).getAll();
        verify(service).update(argumentCaptor.capture());
        verify(childValidator).validate(childEntity, ValidationType.EXISTS);
        verifyNoMoreInteractions(service, childValidator);
        verifyZeroInteractions(converter, parentValidator);

        assertParentDeepEquals(parentDomain, argumentCaptor.getValue());
    }

    /**
     * Test method for {@link MovableChildFacade#remove(Movable)} with invalid data.
     */
    @Test
    void remove_InvalidData() {
        final S childEntity = newChildEntity(Integer.MAX_VALUE);

        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.remove(childEntity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(childValidator).validate(childEntity, ValidationType.EXISTS);
        verifyNoMoreInteractions(childValidator);
        verifyZeroInteractions(service, converter, parentValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#duplicate(Movable)}.
     */
    @Test
    void duplicate() {
        final S childEntity = newChildEntity(1);
        final T childDomain = newChildDomain(null);
        childDomain.setPosition(0);
        final ArgumentCaptor<V> argumentCaptor = ArgumentCaptor.forClass(getParentDomainClass());

        when(service.getAll()).thenReturn(List.of(newParentDomain(1)));
        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.duplicate(childEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).getAll();
        verify(service).update(argumentCaptor.capture());
        verify(childValidator).validate(childEntity, ValidationType.EXISTS);
        verifyNoMoreInteractions(service, childValidator);
        verifyZeroInteractions(converter, parentValidator);

        assertParentDeepEquals(newParentDomainWithChildren(1, List.of(newChildDomain(1), childDomain)), argumentCaptor.getValue());
    }

    /**
     * Test method for {@link MovableChildFacade#duplicate(Movable)} with invalid data.
     */
    @Test
    void duplicate_InvalidData() {
        final S childEntity = newChildEntity(Integer.MAX_VALUE);

        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.duplicate(childEntity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(childValidator).validate(childEntity, ValidationType.EXISTS);
        verifyNoMoreInteractions(childValidator);
        verifyZeroInteractions(service, converter, parentValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#moveUp(Movable)}.
     */
    @Test
    void moveUp() {
        final S childEntity = newChildEntity(2);
        final T childDomain1 = newChildDomain(1);
        childDomain1.setPosition(1);
        final T childDomain2 = newChildDomain(2);
        childDomain2.setPosition(0);
        final ArgumentCaptor<V> argumentCaptor = ArgumentCaptor.forClass(getParentDomainClass());

        when(service.getAll()).thenReturn(List.of(newParentDomainWithChildren(1, List.of(newChildDomain(1),
            newChildDomain(2)))));
        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.moveUp(childEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).getAll();
        verify(service).update(argumentCaptor.capture());
        verify(childValidator).validate(childEntity, ValidationType.EXISTS, ValidationType.UP);
        verifyNoMoreInteractions(service, childValidator);
        verifyZeroInteractions(converter, parentValidator);

        assertParentDeepEquals(newParentDomainWithChildren(1, List.of(childDomain1, childDomain2)), argumentCaptor.getValue());
    }

    /**
     * Test method for {@link MovableChildFacade#moveUp(Movable)} with invalid data.
     */
    @Test
    void moveUp_InvalidData() {
        final S childEntity = newChildEntity(Integer.MAX_VALUE);

        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.moveUp(childEntity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(childValidator).validate(childEntity, ValidationType.EXISTS, ValidationType.UP);
        verifyNoMoreInteractions(childValidator);
        verifyZeroInteractions(service, converter, parentValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#moveDown(Movable)}.
     */
    @Test
    void moveDown() {
        final S childEntity = newChildEntity(1);
        final T childDomain1 = newChildDomain(1);
        childDomain1.setPosition(1);
        final T childDomain2 = newChildDomain(2);
        childDomain2.setPosition(0);
        final ArgumentCaptor<V> argumentCaptor = ArgumentCaptor.forClass(getParentDomainClass());

        when(service.getAll()).thenReturn(List.of(newParentDomainWithChildren(1, List.of(newChildDomain(1),
            newChildDomain(2)))));
        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.moveDown(childEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).getAll();
        verify(service).update(argumentCaptor.capture());
        verify(childValidator).validate(childEntity, ValidationType.EXISTS, ValidationType.DOWN);
        verifyNoMoreInteractions(service, childValidator);
        verifyZeroInteractions(converter, parentValidator);

        assertParentDeepEquals(newParentDomainWithChildren(1, List.of(childDomain1, childDomain2)), argumentCaptor.getValue());
    }

    /**
     * Test method for {@link MovableChildFacade#moveDown(Movable)} with invalid data.
     */
    @Test
    void moveDown_InvalidData() {
        final S childEntity = newChildEntity(Integer.MAX_VALUE);

        when(childValidator.validate(any(getChildEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.moveDown(childEntity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(childValidator).validate(childEntity, ValidationType.EXISTS, ValidationType.DOWN);
        verifyNoMoreInteractions(childValidator);
        verifyZeroInteractions(service, converter, parentValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#find(Movable)}.
     */
    @Test
    void find() {
        final U parentEntity = newParentEntity(1);
        final List<S> expectedData = List.of(newChildEntity(1));

        if (isFirstChild()) {
            when(service.get(any(Integer.class))).thenReturn(newParentDomain(1));
        } else {
            when(service.getAll()).thenReturn(List.of(newParentDomain(1)));
        }
        when(converter.convertBack(anyList())).thenReturn(expectedData);
        when(parentValidator.validate(any(getParentEntityClass()), any())).thenReturn(new Result<>());

        final Result<List<S>> result = facade.find(parentEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        if (isFirstChild()) {
            verify(service).get(parentEntity.getId());
        } else {
            verify(service).getAll();
        }
        verify(converter).convertBack(List.of(newChildDomain(1)));
        verify(parentValidator).validate(parentEntity, ValidationType.EXISTS);
        verifyNoMoreInteractions(service, converter, parentValidator);
        verifyZeroInteractions(childValidator);
    }

    /**
     * Test method for {@link MovableChildFacade#find(Movable)} with invalid data.
     */
    @Test
    void find_InvalidData() {
        final U parentEntity = newParentEntity(1);

        when(parentValidator.validate(any(getParentEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<List<S>> result = facade.find(parentEntity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(INVALID_DATA_RESULT.getEvents());
        });

        verify(parentValidator).validate(parentEntity, ValidationType.EXISTS);
        verifyNoMoreInteractions(parentValidator);
        verifyZeroInteractions(service, converter, childValidator);
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
     * Returns converter for movable data.
     *
     * @return converter for movable data
     */
    protected MovableConverter<S, T> getConverter() {
        return converter;
    }

    /**
     * Returns validator for movable data for parent data.
     *
     * @return validator for movable data for parent data
     */
    protected MovableValidator<U> getParentMovableValidator() {
        return parentValidator;
    }

    /**
     * Returns validator for movable data for child data.
     *
     * @return validator for movable data for child data
     */
    protected MovableValidator<S> getChildMovableValidator() {
        return childValidator;
    }

    /**
     * Returns true if child if 1st parent child.
     *
     * @return true if child if 1st parent child
     */
    @SuppressWarnings("MethodMayBeStatic")
    protected boolean isFirstChild() {
        return true;
    }

    /**
     * Returns facade for movable data for child data.
     *
     * @return facade for movable data for child data
     */
    protected abstract MovableChildFacade<S, U> getFacade();

    /**
     * Returns parent entity.
     *
     * @param id ID
     * @return parent entity
     */
    protected abstract U newParentEntity(Integer id);

    /**
     * Returns parent domain.
     *
     * @param id ID
     * @return parent domain
     */
    @SuppressWarnings("SameParameterValue")
    protected abstract V newParentDomain(Integer id);

    /**
     * Returns parent domain with children.
     *
     * @param id       ID
     * @param children children
     * @return parent domain with children
     */
    @SuppressWarnings("SameParameterValue")
    protected abstract V newParentDomainWithChildren(Integer id, List<T> children);

    /**
     * Returns child entity.
     *
     * @param id ID
     * @return child entity
     */
    protected abstract S newChildEntity(Integer id);

    /**
     * Returns child domain.
     *
     * @param id ID
     * @return child domain
     */
    protected abstract T newChildDomain(Integer id);

    /**
     * Returns parent entity class.
     *
     * @return parent entity class.
     */
    protected abstract Class<U> getParentEntityClass();

    /**
     * Returns parent domain class.
     *
     * @return parent domain class.
     */
    protected abstract Class<V> getParentDomainClass();

    /**
     * Returns child entity class.
     *
     * @return child entity class.
     */
    protected abstract Class<S> getChildEntityClass();

    /**
     * Returns child domain class.
     *
     * @return child domain class.
     */
    protected abstract Class<T> getChildDomainClass();

    /**
     * Assert parent deep equals.
     *
     * @param expected expected
     * @param actual   actual
     */
    protected abstract void assertParentDeepEquals(V expected, V actual);

}
