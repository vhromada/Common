package cz.vhromada.common.test.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.converter.MovableConverter;
import cz.vhromada.common.facade.MovableParentFacade;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * An abstract class represents test for {@link MovableParentFacade}.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("JUnitTestMethodInProductSource")
public abstract class MovableParentFacadeTest<T extends Movable, U extends Movable> {

    /**
     * Result for invalid data
     */
    private static final Result<Void> INVALID_DATA_RESULT = Result.error("DATA_INVALID", "Data must be valid.");

    /**
     * Instance of {@link MovableService}
     */
    @Mock
    private MovableService<U> service;

    /**
     * Instance of {@link MovableConverter}
     */
    @Mock
    private MovableConverter<T, U> converter;

    /**
     * Instance of {@link MovableValidator}
     */
    @Mock
    private MovableValidator<T> validator;

    /**
     * Instance of {@link MovableParentFacade}
     */
    private MovableParentFacade<T> facade;

    /**
     * Initializes facade for movable data.
     */
    @BeforeEach
    public void setUp() {
        facade = getFacade();
    }

    /**
     * Test method for {@link MovableParentFacade#newData()}.
     */
    @Test
    void newData() {
        final Result<Void> result = facade.newData();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).newData();
        verifyNoMoreInteractions(service);
        verifyZeroInteractions(converter, validator);
    }

    /**
     * Test method for {@link MovableParentFacade#getAll()}.
     */
    @Test
    void getAll() {
        final List<U> domainList = List.of(newDomain(1), newDomain(2));
        final List<T> entityList = List.of(newEntity(1), newEntity(2));

        when(service.getAll()).thenReturn(domainList);
        when(converter.convertBack(anyList())).thenReturn(entityList);

        final Result<List<T>> result = facade.getAll();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isEqualTo(entityList);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).getAll();
        verify(converter).convertBack(domainList);
        verifyNoMoreInteractions(service, converter);
        verifyZeroInteractions(validator);
    }


    /**
     * Test method for {@link MovableParentFacade#get(Integer)} with existing data.
     */
    @Test
    void get_ExistingData() {
        final U domain = newDomain(1);
        final T entity = newEntity(1);

        when(service.get(any(Integer.class))).thenReturn(domain);
        when(converter.convertBack(any(getDomainClass()))).thenReturn(entity);

        final Result<T> result = facade.get(1);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isEqualTo(entity);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).get(1);
        verify(converter).convertBack(domain);
        verifyNoMoreInteractions(service, converter);
        verifyZeroInteractions(validator);
    }

    /**
     * Test method for {@link MovableParentFacade#get(Integer)} with not existing data.
     */
    @Test
    void get_NotExistingData() {
        when(service.get(any(Integer.class))).thenReturn(null);

        final Result<T> result = facade.get(Integer.MAX_VALUE);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).get(Integer.MAX_VALUE);
        verify(converter).convertBack((U) null);
        verifyNoMoreInteractions(service, converter);
        verifyZeroInteractions(validator);
    }

    /**
     * Test method for {@link MovableParentFacade#get(Integer)} with null data.
     */
    @Test
    void get_NullData() {
        final Result<T> result = facade.get(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(new Event(Severity.ERROR, "ID_NULL", "ID mustn't be null.")));
        });

        verifyZeroInteractions(service, converter, validator);
    }

    /**
     * Test method for {@link MovableParentFacade#add(Movable)}.
     */
    @Test
    void add() {
        final T entity = newEntity(null);
        final U domain = newDomain(null);

        when(converter.convert(any(getEntityClass()))).thenReturn(domain);
        when(validator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.add(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).add(domain);
        verify(converter).convert(entity);
        verify(validator).validate(entity, ValidationType.NEW, ValidationType.DEEP);
        verifyNoMoreInteractions(service, converter, validator);
    }

    /**
     * Test method for {@link MovableParentFacade#add(Movable)} with invalid data.
     */
    @Test
    void add_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(validator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.add(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(validator).validate(entity, ValidationType.NEW, ValidationType.DEEP);
        verifyNoMoreInteractions(validator);
        verifyZeroInteractions(service, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)}.
     */
    @Test
    void update() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        initUpdateMock(domain);

        final Result<Void> result = facade.update(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verifyUpdateMock(entity, domain);
        verifyNoMoreInteractions(service, converter, validator);
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)} with invalid data.
     */
    @Test
    void update_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(validator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.update(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(validator).validate(entity, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP);
        verifyNoMoreInteractions(validator);
        verifyZeroInteractions(service, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#remove(Movable)}.
     */
    @Test
    void remove() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        when(service.get(any(Integer.class))).thenReturn(domain);
        when(validator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.remove(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).get(1);
        verify(service).remove(domain);
        verify(validator).validate(entity, ValidationType.EXISTS);
        verifyNoMoreInteractions(service, validator);
        verifyZeroInteractions(converter);
    }

    /**
     * Test method for {@link MovableParentFacade#remove(Movable)} with invalid data.
     */
    @Test
    void remove_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(validator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.remove(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(validator).validate(entity, ValidationType.EXISTS);
        verifyNoMoreInteractions(validator);
        verifyZeroInteractions(service, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#duplicate(Movable)}.
     */
    @Test
    void duplicate() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        when(service.get(any(Integer.class))).thenReturn(domain);
        when(validator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.duplicate(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).get(1);
        verify(service).duplicate(domain);
        verify(validator).validate(entity, ValidationType.EXISTS);
        verifyNoMoreInteractions(service, validator);
        verifyZeroInteractions(converter);
    }

    /**
     * Test method for {@link MovableParentFacade#duplicate(Movable)} with invalid data.
     */
    @Test
    void duplicate_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(validator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.duplicate(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(validator).validate(entity, ValidationType.EXISTS);
        verifyNoMoreInteractions(validator);
        verifyZeroInteractions(service, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)}.
     */
    @Test
    void moveUp() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        when(service.get(any(Integer.class))).thenReturn(domain);
        when(validator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.moveUp(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).get(1);
        verify(service).moveUp(domain);
        verify(validator).validate(entity, ValidationType.EXISTS, ValidationType.UP);
        verifyNoMoreInteractions(service, validator);
        verifyZeroInteractions(converter);
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)} with invalid data.
     */
    @Test
    void moveUp_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(validator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.moveUp(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(validator).validate(entity, ValidationType.EXISTS, ValidationType.UP);
        verifyNoMoreInteractions(validator);
        verifyZeroInteractions(service, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)}.
     */
    @Test
    void moveDown() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        when(service.get(any(Integer.class))).thenReturn(domain);
        when(validator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = facade.moveDown(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).get(1);
        verify(service).moveDown(domain);
        verify(validator).validate(entity, ValidationType.EXISTS, ValidationType.DOWN);
        verifyNoMoreInteractions(service, validator);
        verifyZeroInteractions(converter);
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)} with invalid data.
     */
    @Test
    void moveDown_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(validator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = facade.moveDown(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(validator).validate(entity, ValidationType.EXISTS, ValidationType.DOWN);
        verifyNoMoreInteractions(validator);
        verifyZeroInteractions(service, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#updatePositions()}.
     */
    @Test
    void updatePositions() {
        final Result<Void> result = facade.updatePositions();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(service).updatePositions();
        verifyNoMoreInteractions(service);
        verifyZeroInteractions(converter, validator);
    }

    /**
     * Returns service for movable data.
     *
     * @return service for movable data
     */
    protected MovableService<U> getService() {
        return service;
    }

    /**
     * Returns converter for movable data.
     *
     * @return converter for movable data
     */
    protected MovableConverter<T, U> getConverter() {
        return converter;
    }

    /**
     * Returns validator for movable data.
     *
     * @return validator for movable data
     */
    protected MovableValidator<T> getValidator() {
        return validator;
    }

    /**
     * Initializes mock for update.
     *
     * @param domain domain
     */
    protected void initUpdateMock(final U domain) {
        when(converter.convert(any(getEntityClass()))).thenReturn(domain);
        when(validator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());
    }

    /**
     * Verifies mock for update.
     *
     * @param entity entity
     * @param domain domain
     */
    protected void verifyUpdateMock(final T entity, final U domain) {
        verify(service).update(domain);
        verify(converter).convert(entity);
        verify(validator).validate(entity, ValidationType.UPDATE, ValidationType.EXISTS, ValidationType.DEEP);
    }

    /**
     * Returns facade for movable data for parent data.
     *
     * @return facade for movable data for parent data
     */
    protected abstract MovableParentFacade<T> getFacade();

    /**
     * Returns entity.
     *
     * @param id ID
     * @return entity
     */
    protected abstract T newEntity(Integer id);

    /**
     * Returns domain.
     *
     * @param id ID
     * @return domain
     */
    protected abstract U newDomain(Integer id);

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
