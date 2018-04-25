package cz.vhromada.common.validator;

/**
 * An enum represents type of validation.
 *
 * @author Vladimir Hromada
 */
public enum ValidationType {

    /**
     * New entity validation
     */
    NEW,

    /**
     * Existing entity validation
     */
    EXISTS,

    /**
     * Deep validation
     */
    DEEP,

    /**
     * Moving up entity validation
     */
    UP,

    /**
     * Moving down entity validation
     */
    DOWN

}
