package cz.vhromada.common.test.validator;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.utils.CollectionUtils;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.common.validator.ValidationType;
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
 * An abstract class represents test for {@link MovableValidator}.
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("JUnitTestMethodInProductSource")
public abstract class MovableValidatorTest<T extends Movable, U extends Movable> {

    /**
     * ID
     */
    private static final int ID = 5;

    /**
     * Instance of {@link MovableService}
     */
    @Mock
    private MovableService<U> movableService;

    /**
     * Instance of {@link MovableValidator}
     */
    private MovableValidator<T> movableValidator;

    /**
     * Initializes validator.
     */
    @BeforeEach
    void setUp() {
        movableValidator = getMovableValidator();
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#NEW} with correct data.
     */
    @Test
    void validate_New() {
        final Result<Void> result = movableValidator.validate(getValidatingData(null), ValidationType.NEW);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verifyZeroInteractions(movableService);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#NEW} with data with not null ID.
     */
    @Test
    void validate_New_NotNullId() {
        final Result<Void> result = movableValidator.validate(getValidatingData(Integer.MAX_VALUE), ValidationType.NEW);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_ID_NOT_NULL", "ID must be null.")));
        });

        verifyZeroInteractions(movableService);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#EXISTS} with correct data.
     */
    @Test
    void validate_Exists() {
        final T validatingData = getValidatingData(ID);

        initExistsMock(validatingData, true);

        final Result<Void> result = movableValidator.validate(validatingData, ValidationType.EXISTS);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verifyExistsMock(validatingData);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#EXISTS} with data with null ID.
     */
    @Test
    void validate_Exists_NullId() {
        final Result<Void> result = movableValidator.validate(getValidatingData(null), ValidationType.EXISTS);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_ID_NULL", "ID mustn't be null.")));
        });

        verifyZeroInteractions(movableService);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#EXISTS} with not existing data.
     */
    @Test
    void validate_Exists_NotExistingData() {
        final T validatingData = getValidatingData(ID);

        initExistsMock(validatingData, false);

        final Result<Void> result = movableValidator.validate(validatingData, ValidationType.EXISTS);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_NOT_EXIST", getName() + " doesn't exist.")));
        });

        verifyExistsMock(validatingData);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#UP} with correct data.
     */
    @Test
    void validate_Up() {
        final T validatingData = getValidatingData(ID);

        initMovingMock(validatingData, true, true);

        final Result<Void> result = movableValidator.validate(validatingData, ValidationType.UP);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verifyMovingMock(validatingData);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#UP} with invalid data.
     */
    @Test
    void validate_Up_Invalid() {
        final T validatingData = getValidatingData(Integer.MAX_VALUE);

        initMovingMock(validatingData, true, false);

        final Result<Void> result = movableValidator.validate(validatingData, ValidationType.UP);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_NOT_MOVABLE", getName() + " can't be moved up.")));
        });

        verifyMovingMock(validatingData);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#DOWN} with correct data.
     */
    @Test
    void validate_Down() {
        final T validatingData = getValidatingData(ID);

        initMovingMock(validatingData, false, true);

        final Result<Void> result = movableValidator.validate(validatingData, ValidationType.DOWN);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verifyMovingMock(validatingData);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#DOWN} with invalid data.
     */
    @Test
    void validate_Down_Invalid() {
        final T validatingData = getValidatingData(Integer.MAX_VALUE);

        initMovingMock(validatingData, false, false);

        final Result<Void> result = movableValidator.validate(validatingData, ValidationType.DOWN);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.ERROR);
            softly.assertThat(result.getEvents())
                .isEqualTo(Collections.singletonList(new Event(Severity.ERROR, getPrefix() + "_NOT_MOVABLE", getName() + " can't be moved down.")));
        });

        verifyMovingMock(validatingData);
    }

    /**
     * Test method for {@link MovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#DEEP} with correct data.
     */
    @Test
    void validate_Deep() {
        final Result<Void> result = movableValidator.validate(getValidatingData(ID), ValidationType.DEEP);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.OK);
            softly.assertThat(result.getEvents()).isEmpty();
        });

        verifyZeroInteractions(movableService);
    }

    /**
     * Returns instance of {@link MovableService}.
     *
     * @return instance of {@link MovableService}
     */
    protected MovableService<U> getMovableService() {
        return movableService;
    }

    /**
     * Initializes mock for exists.
     *
     * @param validatingData validating data
     * @param exists         true if data exists
     */
    protected void initExistsMock(final T validatingData, final boolean exists) {
        final U result = exists ? getRepositoryData(validatingData) : null;

        when(movableService.get(any(Integer.class))).thenReturn(result);
    }

    /**
     * Verifies mock for exists.
     *
     * @param validatingData validating data
     */
    protected void verifyExistsMock(final T validatingData) {
        verify(movableService).get(validatingData.getId());
        verifyNoMoreInteractions(movableService);
    }

    /**
     * Initializes mock for moving.
     *
     * @param validatingData validating data
     * @param up             true if moving up
     * @param valid          true if data should be valid
     */
    protected void initMovingMock(final T validatingData, final boolean up, final boolean valid) {
        final List<U> dataList = CollectionUtils.newList(getItem1(), getItem2());
        final U repositoryData = getRepositoryData(validatingData);
        if (up && valid || !up && !valid) {
            dataList.add(repositoryData);
        } else {
            dataList.add(0, repositoryData);
        }

        when(movableService.getAll()).thenReturn(dataList);
        when(movableService.get(any(Integer.class))).thenReturn(repositoryData);
    }

    /**
     * Verifies mock for moving.
     *
     * @param validatingData validating data
     */
    protected void verifyMovingMock(final T validatingData) {
        verify(movableService).getAll();
        verify(movableService).get(validatingData.getId());
        verifyNoMoreInteractions(movableService);
    }

    /**
     * Returns instance of {@link MovableValidator}.
     *
     * @return instance of {@link MovableValidator}
     */
    protected abstract MovableValidator<T> getMovableValidator();

    /**
     * Returns instance of {@link T}.
     *
     * @param id ID
     * @return instance of {@link T}
     */
    protected abstract T getValidatingData(Integer id);

    /**
     * Returns instance of {@link U}.
     *
     * @param validatingData validating data
     * @return instance of {@link U}
     */
    protected abstract U getRepositoryData(T validatingData);

    /**
     * Returns 1st item in data list.
     *
     * @return 1st item in data list
     */
    protected abstract U getItem1();

    /**
     * Returns 2nd item in data list.
     *
     * @return 2nd item in data list
     */
    protected abstract U getItem2();

    /**
     * Returns name of entity.
     *
     * @return name of entity
     */
    protected abstract String getName();

    /**
     * Returns prefix for validation keys.
     *
     * @return prefix for validation keys
     */
    private String getPrefix() {
        return getName().toUpperCase();
    }

}
