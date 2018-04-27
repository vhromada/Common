package cz.vhromada.common.test.stub;

import cz.vhromada.common.Movable;

/**
 * A class represents stub for {@link Movable}.
 *
 * @author Vladimir Hromada
 */
public class MovableStub implements Movable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Integer id;

    /**
     * Position
     */
    private Integer position;

    /**
     * Creates a new instance of MovableStub.
     *
     * @param id ID
     */
    public MovableStub(final Integer id) {
        this(id, null);
    }

    /**
     * Creates a new instance of MovableStub.
     *
     * @param id       ID
     * @param position position
     */
    public MovableStub(final Integer id, final Integer position) {
        this.id = id;
        this.position = position;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(final Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPosition() {
        return position;
    }

    @Override
    public void setPosition(final Integer position) {
        this.position = position;
    }

}
