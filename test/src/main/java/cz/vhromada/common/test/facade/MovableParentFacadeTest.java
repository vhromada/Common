package cz.vhromada.common.test.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.facade.MovableParentFacade;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.utils.CollectionUtils;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.common.validator.ValidationType;
import cz.vhromada.converter.Converter;
import cz.vhromada.result.Event;
import cz.vhromada.result.Result;
import cz.vhromada.result.Severity;
import cz.vhromada.result.Status;
import cz.vhromada.test.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

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
    private MovableService<U> movableService;

    /**
     * Instance of {@link Converter}
     */
    @Mock
    private Converter converter;

    /**
     * Instance of {@link MovableValidator}
     */
    @Mock
    private MovableValidator<T> movableValidator;

    /**
     * Instance of {@link MovableParentFacade}
     */
    private MovableParentFacade<T> parentCatalogFacade;

    /**
     * Initializes facade for movable data.
     */
    @BeforeEach
    void setUp() {
        parentCatalogFacade = getCatalogParentFacade();
    }

    /**
     * Test method for {@link MovableParentFacade#newData()}.
     */
    @Test
    void newData() {
        final Result<Void> result = parentCatalogFacade.newData();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).newData();
        verifyNoMoreInteractions(movableService);
        verifyZeroInteractions(converter, movableValidator);
    }

    /**
     * Test method for {@link MovableParentFacade#getAll()}.
     */
    @Test
    void getAll() {
        final List<U> domainList = CollectionUtils.newList(newDomain(1), newDomain(2));
        final List<T> entityList = CollectionUtils.newList(newEntity(1), newEntity(2));

        when(movableService.getAll()).thenReturn(domainList);
        when(converter.convertCollection(anyList(), eq(getEntityClass()))).thenReturn(entityList);

        final Result<List<T>> result = parentCatalogFacade.getAll();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isEqualTo(entityList);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).getAll();
        verify(converter).convertCollection(domainList, getEntityClass());
        verifyNoMoreInteractions(movableService, converter);
        verifyZeroInteractions(movableValidator);
    }


    /**
     * Test method for {@link MovableParentFacade#get(Integer)} with existing data.
     */
    @Test
    void get_ExistingData() {
        final U domain = newDomain(1);
        final T entity = newEntity(1);

        when(movableService.get(any(Integer.class))).thenReturn(domain);
        when(converter.convert(any(getDomainClass()), eq(getEntityClass()))).thenReturn(entity);

        final Result<T> result = parentCatalogFacade.get(1);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isEqualTo(entity);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).get(1);
        verify(converter).convert(domain, getEntityClass());
        verifyNoMoreInteractions(movableService, converter);
        verifyZeroInteractions(movableValidator);
    }

    /**
     * Test method for {@link MovableParentFacade#get(Integer)} with not existing data.
     */
    @Test
    void get_NotExistingData() {
        when(movableService.get(any(Integer.class))).thenReturn(null);

        final Result<T> result = parentCatalogFacade.get(Integer.MAX_VALUE);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).get(Integer.MAX_VALUE);
        verify(converter).convert(null, getEntityClass());
        verifyNoMoreInteractions(movableService, converter);
        verifyZeroInteractions(movableValidator);
    }

    /**
     * Test method for {@link MovableParentFacade#get(Integer)} with null data.
     */
    @Test
    void get_NullData() {
        final Result<T> result = parentCatalogFacade.get(null);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getData()).isNull();
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(new Event(Severity.ERROR, "ID_NULL", "ID mustn't be null.")));
        });

        verifyZeroInteractions(movableService, converter, movableValidator);
    }

    /**
     * Test method for {@link MovableParentFacade#add(Movable)}.
     */
    @Test
    void add() {
        final T entity = newEntity(null);
        final U domain = newDomain(null);

        when(converter.convert(any(getEntityClass()), eq(getDomainClass()))).thenReturn(domain);
        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = parentCatalogFacade.add(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).add(domain);
        verify(converter).convert(entity, getDomainClass());
        verify(movableValidator).validate(entity, ValidationType.NEW, ValidationType.DEEP);
        verifyNoMoreInteractions(movableService, converter, movableValidator);
    }

    /**
     * Test method for {@link MovableParentFacade#add(Movable)} with invalid data.
     */
    @Test
    void add_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = parentCatalogFacade.add(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(movableValidator).validate(entity, ValidationType.NEW, ValidationType.DEEP);
        verifyNoMoreInteractions(movableValidator);
        verifyZeroInteractions(movableService, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)}.
     */
    @Test
    void update() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        initUpdateMock(domain);

        final Result<Void> result = parentCatalogFacade.update(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verifyUpdateMock(entity, domain);
        verifyNoMoreInteractions(movableService, converter, movableValidator);
    }

    /**
     * Test method for {@link MovableParentFacade#update(Movable)} with invalid data.
     */
    @Test
    void update_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = parentCatalogFacade.update(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(movableValidator).validate(entity, ValidationType.EXISTS, ValidationType.DEEP);
        verifyNoMoreInteractions(movableValidator);
        verifyZeroInteractions(movableService, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#remove(Movable)}.
     */
    @Test
    void remove() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        when(movableService.get(any(Integer.class))).thenReturn(domain);
        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = parentCatalogFacade.remove(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).get(1);
        verify(movableService).remove(domain);
        verify(movableValidator).validate(entity, ValidationType.EXISTS);
        verifyNoMoreInteractions(movableService, movableValidator);
        verifyZeroInteractions(converter);
    }

    /**
     * Test method for {@link MovableParentFacade#remove(Movable)} with invalid data.
     */
    @Test
    void remove_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = parentCatalogFacade.remove(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(movableValidator).validate(entity, ValidationType.EXISTS);
        verifyNoMoreInteractions(movableValidator);
        verifyZeroInteractions(movableService, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#duplicate(Movable)}.
     */
    @Test
    void duplicate() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        when(movableService.get(any(Integer.class))).thenReturn(domain);
        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = parentCatalogFacade.duplicate(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).get(1);
        verify(movableService).duplicate(domain);
        verify(movableValidator).validate(entity, ValidationType.EXISTS);
        verifyNoMoreInteractions(movableService, movableValidator);
        verifyZeroInteractions(converter);
    }

    /**
     * Test method for {@link MovableParentFacade#duplicate(Movable)} with invalid data.
     */
    @Test
    void duplicate_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = parentCatalogFacade.duplicate(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(movableValidator).validate(entity, ValidationType.EXISTS);
        verifyNoMoreInteractions(movableValidator);
        verifyZeroInteractions(movableService, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)}.
     */
    @Test
    void moveUp() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        when(movableService.get(any(Integer.class))).thenReturn(domain);
        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = parentCatalogFacade.moveUp(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).get(1);
        verify(movableService).moveUp(domain);
        verify(movableValidator).validate(entity, ValidationType.EXISTS, ValidationType.UP);
        verifyNoMoreInteractions(movableService, movableValidator);
        verifyZeroInteractions(converter);
    }

    /**
     * Test method for {@link MovableParentFacade#moveUp(Movable)} with invalid data.
     */
    @Test
    void moveUp_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = parentCatalogFacade.moveUp(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(movableValidator).validate(entity, ValidationType.EXISTS, ValidationType.UP);
        verifyNoMoreInteractions(movableValidator);
        verifyZeroInteractions(movableService, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)}.
     */
    @Test
    void moveDown() {
        final T entity = newEntity(1);
        final U domain = newDomain(1);

        when(movableService.get(any(Integer.class))).thenReturn(domain);
        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());

        final Result<Void> result = parentCatalogFacade.moveDown(entity);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).get(1);
        verify(movableService).moveDown(domain);
        verify(movableValidator).validate(entity, ValidationType.EXISTS, ValidationType.DOWN);
        verifyNoMoreInteractions(movableService, movableValidator);
        verifyZeroInteractions(converter);
    }

    /**
     * Test method for {@link MovableParentFacade#moveDown(Movable)} with invalid data.
     */
    @Test
    void moveDown_InvalidData() {
        final T entity = newEntity(Integer.MAX_VALUE);

        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(INVALID_DATA_RESULT);

        final Result<Void> result = parentCatalogFacade.moveDown(entity);

        assertThat(result).isEqualTo(INVALID_DATA_RESULT);

        verify(movableValidator).validate(entity, ValidationType.EXISTS, ValidationType.DOWN);
        verifyNoMoreInteractions(movableValidator);
        verifyZeroInteractions(movableService, converter);
    }

    /**
     * Test method for {@link MovableParentFacade#updatePositions()}.
     */
    @Test
    void updatePositions() {
        final Result<Void> result = parentCatalogFacade.updatePositions();

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verify(movableService).updatePositions();
        verifyNoMoreInteractions(movableService);
        verifyZeroInteractions(converter, movableValidator);
    }

    /**
     * Returns service for movable data.
     *
     * @return service for movable data
     */
    protected MovableService<U> getMovableService() {
        return movableService;
    }

    /**
     * Returns converter.
     *
     * @return converter
     */
    protected Converter getConverter() {
        return converter;
    }

    /**
     * Returns validator for movable data.
     *
     * @return validator for movable data
     */
    protected MovableValidator<T> getMovableValidator() {
        return movableValidator;
    }

    /**
     * Initializes mock for update.
     *
     * @param domain domain
     */
    protected void initUpdateMock(final U domain) {
        when(converter.convert(any(getEntityClass()), eq(getDomainClass()))).thenReturn(domain);
        when(movableValidator.validate(any(getEntityClass()), any())).thenReturn(new Result<>());
    }

    /**
     * Verifies mock for update.
     *
     * @param entity entity
     * @param domain domain
     */
    protected void verifyUpdateMock(final T entity, final U domain) {
        verify(movableService).update(domain);
        verify(converter).convert(entity, getDomainClass());
        verify(movableValidator).validate(entity, ValidationType.EXISTS, ValidationType.DEEP);
    }

    /**
     * Returns facade for movable data for parent data.
     *
     * @return facade for movable data for parent data
     */
    protected abstract MovableParentFacade<T> getCatalogParentFacade();

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
