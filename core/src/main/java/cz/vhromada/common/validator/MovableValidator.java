package cz.vhromada.common.validator;

import cz.vhromada.common.Movable;
import cz.vhromada.result.Result;

/**
 * An interface represents validator for movable data.
 *
 * @param <T> type of data
 * @author Vladimir Hromada
 */
public interface MovableValidator<T extends Movable> {

    /**
     * Validates data.
     *
     * @param data            validating data
     * @param validationTypes types of validation
     * @return result with validation errors
     */
    Result<Void> validate(T data, ValidationType... validationTypes);

}
