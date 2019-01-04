package cz.vhromada.common.test.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Collections;

import cz.vhromada.common.Movable;
import cz.vhromada.common.service.MovableService;
import cz.vhromada.common.test.stub.AbstractMovableValidatorStub;
import cz.vhromada.common.test.stub.MovableStub;
import cz.vhromada.common.validator.AbstractMovableValidator;
import cz.vhromada.common.validator.MovableValidator;
import cz.vhromada.common.validator.ValidationType;
import cz.vhromada.validation.result.Event;
import cz.vhromada.validation.result.Result;
import cz.vhromada.validation.result.Severity;
import cz.vhromada.validation.result.Status;

import org.junit.jupiter.api.Test;

/**
 * A class represents test for class {@link AbstractMovableValidator}.
 *
 * @author Vladimir Hromada
 */
class AbstractMovableValidatorTest extends MovableValidatorTest<Movable, Movable> {

    /**
     * Event key
     */
    private static final String KEY = "key";

    /**
     * Event value
     */
    private static final String VALUE = "value";

    /**
     * Test method for {@link AbstractMovableValidator#AbstractMovableValidator(String, MovableService)} with null name.
     */
    @Test
    void constructor_NullName() {
        assertThatThrownBy(() -> new AbstractMovableValidatorStub(null, getService(), KEY, VALUE)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableValidator#AbstractMovableValidator(String, MovableService)} with null
     * service for movable data.
     */
    @Test
    void constructor_NullMovableService() {
        assertThatThrownBy(() -> new AbstractMovableValidatorStub(getName(), null, KEY, VALUE)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test method for {@link AbstractMovableValidator#validate(Movable, ValidationType...)} with {@link ValidationType#DEEP}.
     */
    @Test
    @Override
    void validate_Deep() {
        final Movable movable = getValidatingData(1);

        initDeepMock(movable);

        final Result<Void> result = getValidator().validate(movable, ValidationType.DEEP);

        assertSoftly(softly -> {
            softly.assertThat(result.getStatus()).isEqualTo(Status.WARN);
            softly.assertThat(result.getEvents()).isEqualTo(Collections.singletonList(new Event(Severity.WARN, KEY, VALUE)));
        });

        verifyDeepMock(movable);
    }

    @Override
    protected MovableValidator<Movable> getValidator() {
        return new AbstractMovableValidatorStub(getName(), getService(), KEY, VALUE);
    }

    @Override
    protected Movable getValidatingData(final Integer id) {
        return new MovableStub(id);
    }

    @Override
    protected Movable getValidatingData(final Integer id, final Integer position) {
        return new MovableStub(id, position);
    }

    @Override
    protected Movable getRepositoryData(final Movable validatingData) {
        return new MovableStub(validatingData.getId());
    }

    @Override
    protected Movable getItem1() {
        return new MovableStub(1);
    }

    @Override
    protected Movable getItem2() {
        return new MovableStub(2);
    }

    @Override
    protected String getName() {
        return "Stub";
    }

}
