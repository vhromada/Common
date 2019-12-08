package cz.vhromada.common.test.facade

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import cz.vhromada.common.Movable
import cz.vhromada.common.facade.AbstractMovableChildFacade
import cz.vhromada.common.facade.MovableChildFacade
import cz.vhromada.common.test.stub.AbstractMovableChildFacadeStub
import cz.vhromada.common.test.stub.MovableStub
import org.junit.jupiter.api.BeforeEach

/**
 * A class represents test for class [AbstractMovableChildFacade].
 *
 * @author Vladimir Hromada
 */
class AbstractMovableChildFacadeTest : MovableChildFacadeTest<Movable, Movable, Movable, Movable>() {

    /**
     * Instance of [Movable]
     */
    private var movable: Movable? = null

    /**
     * {@inheritDoc}
     * Cleanup movable.
     */
    @BeforeEach
    override fun setUp() {
        super.setUp()

        movable = null
    }

    override fun getFacade(): MovableChildFacade<Movable, Movable> {
        return AbstractMovableChildFacadeStub(service, mapper, parentMovableValidator, childMovableValidator)
    }

    override fun newParentEntity(id: Int): Movable {
        return getMovable(id)
    }

    override fun newParentDomain(id: Int): Movable {
        return getMovable(id)
    }

    override fun newParentDomainWithChildren(id: Int, children: List<Movable>): Movable {
        return getMovable(id)
    }

    override fun newChildEntity(id: Int?): Movable {
        return getMovable(id)
    }

    override fun newChildDomain(id: Int?): Movable {
        return getMovable(id)
    }

    override fun getParentRemovedData(parent: Movable, child: Movable): Movable {
        return parent
    }

    override fun anyParentEntity(): Movable {
        return any()
    }

    override fun anyChildEntity(): Movable {
        return any()
    }

    override fun anyChildDomain(): Movable {
        return any()
    }

    override fun argumentCaptorParentDomain(): KArgumentCaptor<Movable> {
        return argumentCaptor()
    }

    override fun assertParentDeepEquals(expected: Movable, actual: Movable) {}

    /**
     * Returns movable object.
     *
     * @param id ID
     * @return movable object
     */
    private fun getMovable(id: Int?): Movable {
        if (movable == null) {
            movable = MovableStub(id)
        } else {
            movable!!.id = id
        }

        return movable!!
    }

}
