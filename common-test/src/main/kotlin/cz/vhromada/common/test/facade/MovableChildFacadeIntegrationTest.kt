package cz.vhromada.common.test.facade

import cz.vhromada.common.domain.Audit
import cz.vhromada.common.domain.AuditEntity
import cz.vhromada.common.entity.Movable
import cz.vhromada.common.facade.MovableChildFacade
import cz.vhromada.common.result.Event
import cz.vhromada.common.result.Severity
import cz.vhromada.common.result.Status
import cz.vhromada.common.test.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * An abstract class represents integration test for [MovableChildFacade].
 *
 * @param <T> type of child entity data
 * @param <U> type of child domain data
 * @param <V> type of parent entity data
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@Suppress("FunctionName", "Unused")
abstract class MovableChildFacadeIntegrationTest<T : Movable, U : AuditEntity, V : Movable> {

    /**
     * Test method for [MovableChildFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..getDefaultChildDataCount()) {
            val result = getFacade().get(i)

            assertSoftly {
                it.assertThat(result.status).isEqualTo(Status.OK)
                assertDataDeepEquals(result.data!!, getDomainData(i))
                it.assertThat(result.events()).isEmpty()
            }
        }

        val result = getFacade().get(Integer.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEmpty()
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedData = newDomainData(getDefaultChildDataCount() + 1)
        expectedData.position = Integer.MAX_VALUE

        val result = getFacade().add(newParentData(1), newChildData(null, null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertDataDomainDeepEquals(expectedData, getRepositoryData(getDefaultChildDataCount() + 1)!!)
        assertAddRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.add] with parent with null ID.
     */
    @Test
    fun addNullId() {
        val result = getFacade().add(newParentData(null), newChildData(null, null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullParentDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.add] with with not existing parent.
     */
    @Test
    fun addNotExistingParent() {
        val result = getFacade().add(newParentData(Integer.MAX_VALUE), newChildData(null, null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingParentDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.add] with child with not null ID.
     */
    @Test
    fun addNotNullId() {
        val result = getFacade().add(newParentData(1), newChildData(1, null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getChildPrefix() + "_ID_NOT_NULL", "ID must be null.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.add] with child with not null position.
     */
    @Test
    fun addNotNullPosition() {
        val result = getFacade().add(newParentData(1), newChildData(null, 0))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getChildPrefix() + "_POSITION_NOT_NULL", "Position must be null.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val data = newChildData(1, 0)

        val result = getFacade().update(data)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertDataDeepEquals(data, getRepositoryData(1)!!)
        assertUpdateRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.update] with data with null ID.
     */
    @Test
    fun updateNullId() {
        val result = getFacade().update(newChildData(null, 0))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullChildDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.update] with data with null position.
     */
    @Test
    fun updateNullPosition() {
        val result = getFacade().update(newChildData(1, null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getChildPrefix() + "_POSITION_NULL", "Position mustn't be null.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.update] with data with bad ID.
     */
    @Test
    fun updateBadId() {
        val result = getFacade().update(newChildData(Integer.MAX_VALUE, 0))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingChildDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.remove].
     */
    @Test
    @DirtiesContext
    fun remove() {
        val result = getFacade().remove(newChildData(1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertThat(getRepositoryData(1)).isNull()
        assertRemoveRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.remove] with data with null ID.
     */
    @Test
    fun removeNullId() {
        val result = getFacade().remove(newChildData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullChildDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.remove] with data with bad ID.
     */
    @Test
    fun removeBadId() {
        val result = getFacade().remove(newChildData(Integer.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingChildDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val result = getFacade().duplicate(newChildData(1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertDataDomainDeepEquals(getExpectedDuplicatedData(), getRepositoryData(getDefaultChildDataCount() + 1)!!)
        assertDuplicateRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.duplicate] with data with null ID.
     */
    @Test
    fun duplicateNullId() {
        val result = getFacade().duplicate(newChildData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullChildDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.duplicate] with data with bad ID.
     */
    @Test
    fun duplicateBadId() {
        val result = getFacade().duplicate(newChildData(Integer.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingChildDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.moveUp].
     */
    @Test
    @DirtiesContext
    @Suppress("DuplicatedCode")
    fun moveUp() {
        val result = getFacade().moveUp(newChildData(2))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        val data1 = getDomainData(1)
        data1.position = 1
        data1.modify(getUpdatedAudit())
        val data2 = getDomainData(2)
        data2.position = 0
        data2.modify(getUpdatedAudit())
        assertDataDomainDeepEquals(data1, getRepositoryData(1)!!)
        assertDataDomainDeepEquals(data2, getRepositoryData(2)!!)
        for (i in 3..getDefaultChildDataCount()) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i)!!)
        }
        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.moveUp] with data with null ID.
     */
    @Test
    fun moveUpNullId() {
        val result = getFacade().moveUp(newChildData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullChildDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.moveUp] with not movable data.
     */
    @Test
    fun moveUpNotMovableData() {
        val result = getFacade().moveUp(newChildData(1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getChildPrefix() + "_NOT_MOVABLE", "${getChildName()} can't be moved up.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.moveUp] with data with bad ID.
     */
    @Test
    fun moveUpBadId() {
        val result = getFacade().moveUp(newChildData(Integer.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingChildDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.moveDown].
     */
    @Test
    @DirtiesContext
    @Suppress("DuplicatedCode")
    fun moveDown() {
        val result = getFacade().moveDown(newChildData(1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        val data1 = getDomainData(1)
        data1.position = 1
        data1.modify(getUpdatedAudit())
        val data2 = getDomainData(2)
        data2.position = 0
        data2.modify(getUpdatedAudit())
        assertDataDomainDeepEquals(data1, getRepositoryData(1)!!)
        assertDataDomainDeepEquals(data2, getRepositoryData(2)!!)
        for (i in 3..getDefaultChildDataCount()) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i)!!)
        }
        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.moveDown] with data with null ID.
     */
    @Test
    fun moveDownNullId() {
        val result = getFacade().moveDown(newChildData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullChildDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.moveDown] with not movable data.
     */
    @Test
    fun moveDownNotMovableData() {
        val result = getFacade().moveDown(newChildData(getDefaultChildDataCount()))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getChildPrefix() + "_NOT_MOVABLE", "${getChildName()} can't be moved down.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.moveDown] with data with bad ID.
     */
    @Test
    fun moveDownBadId() {
        val result = getFacade().moveDown(newChildData(Integer.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingChildDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.find].
     */
    @Test
    fun find() {
        for (i in 1..getDefaultParentDataCount()) {
            val result = getFacade().find(newParentData(i))

            assertSoftly {
                it.assertThat(result.status).isEqualTo(Status.OK)
                assertDataListDeepEquals(result.data!!, getDataList(i))
                it.assertThat(result.events()).isEmpty()
            }
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.find] with parent with null ID.
     */
    @Test
    fun findNullId() {
        val result = getFacade().find(newParentData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(getNullParentDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableChildFacade.find] with parent with bad ID.
     */
    @Test
    fun findBadId() {
        val result = getFacade().find(newParentData(Integer.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingParentDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Returns facade for movable data for child data.
     *
     * @return facade for movable data for child data
     */
    protected abstract fun getFacade(): MovableChildFacade<T, V>

    /**
     * Returns default count of parent data.
     *
     * @return default count of parent data
     */
    protected abstract fun getDefaultParentDataCount(): Int

    /**
     * Returns default count of child data.
     *
     * @return default count of child data
     */
    protected abstract fun getDefaultChildDataCount(): Int

    /**
     * Returns repository parent count of data.
     *
     * @return repository parent count of data
     */
    protected abstract fun getRepositoryParentDataCount(): Int

    /**
     * Returns repository child count of data.
     *
     * @return repository child count of data
     */
    protected abstract fun getRepositoryChildDataCount(): Int

    /**
     * Returns list of data.
     *
     * @param parentId parent ID
     * @return list of data
     */
    protected abstract fun getDataList(parentId: Int): List<U>

    /**
     * Returns domain data.
     *
     * @param index index
     * @return domain data
     */
    protected abstract fun getDomainData(index: Int): U

    /**
     * Returns new parent data.
     *
     * @param id ID
     * @return new parent data
     */
    protected abstract fun newParentData(id: Int?): V

    /**
     * Returns new child data.
     *
     * @param id ID
     * @return new child data
     */
    protected abstract fun newChildData(id: Int?): T

    /**
     * Returns domain data.
     *
     * @param id ID
     * @return domain data
     */
    protected abstract fun newDomainData(id: Int): U

    /**
     * Returns repository data.
     *
     * @param id ID
     * @return repository data
     */
    protected abstract fun getRepositoryData(id: Int): U?

    /**
     * Returns name of parent entity.
     *
     * @return name of parent entity
     */
    protected abstract fun getParentName(): String

    /**
     * Returns name of child entity.
     *
     * @return name of child entity
     */
    protected abstract fun getChildName(): String

    /**
     * Asserts list of data deep equals.
     *
     * @param expected expected
     * @param actual   actual
     */
    protected abstract fun assertDataListDeepEquals(expected: List<T>, actual: List<U>)

    /**
     * Asserts data deep equals.
     *
     * @param expected expected
     * @param actual   actual
     */
    protected abstract fun assertDataDeepEquals(expected: T, actual: U)

    /**
     * Asserts domain data deep equals.
     *
     * @param expected expected
     * @param actual   actual
     */
    protected abstract fun assertDataDomainDeepEquals(expected: U, actual: U)

    /**
     * Returns expected duplicated data.
     *
     * @return expected duplicated data
     */
    protected open fun getExpectedDuplicatedData(): U {
        val data = getDomainData(1)
        data.id = getDefaultChildDataCount() + 1

        return data
    }

    /**
     * Asserts default repository data.
     */
    protected open fun assertDefaultRepositoryData() {
        assertSoftly {
            it.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount())
            assertReferences()
        }
    }

    /**
     * Asserts repository data for [MovableChildFacade.update].
     */
    protected open fun assertUpdateRepositoryData() {
        assertSoftly {
            it.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount())
            assertReferences()
        }
    }

    /**
     * Asserts repository data for [MovableChildFacade.remove].
     */
    protected open fun assertRemoveRepositoryData() {
        assertSoftly {
            it.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount() - 1)
            assertReferences()
        }
    }

    /**
     * Asserts repository data for [MovableChildFacade.duplicate].
     */
    protected open fun assertDuplicateRepositoryData() {
        assertSoftly {
            it.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount() + 1)
            assertReferences()
        }
    }

    /**
     * Asserts references.
     */
    protected open fun assertReferences() {
        assertThat(getRepositoryParentDataCount()).isEqualTo(getDefaultParentDataCount())
    }

    /**
     * Returns new child data.
     *
     * @param id       ID
     * @param position position
     * @return new child data
     */
    private fun newChildData(id: Int?, position: Int?): T {
        val childData = newChildData(id)
        childData.position = position

        return childData
    }

    /**
     * Returns audit for update.
     *
     * @return audit for update
     */
    private fun getUpdatedAudit(): Audit {
        return Audit(TestConstants.ACCOUNT_ID, TestConstants.TIME)
    }

    /**
     * Returns event for parent data with null ID.
     *
     * @return event for parent data with null ID
     */
    private fun getNullParentDataIdEvent(): Event {
        return Event(Severity.ERROR, getParentPrefix() + "_ID_NULL", "ID mustn't be null.")
    }

    /**
     * Returns event for not existing parent data.
     *
     * @return event for not existing parent data
     */
    private fun getNotExistingParentDataEvent(): Event {
        return Event(Severity.ERROR, getParentPrefix() + "_NOT_EXIST", getParentName() + " doesn't exist.")
    }

    /**
     * Returns event for child data with null ID.
     *
     * @return event for child data with null ID
     */
    private fun getNullChildDataIdEvent(): Event {
        return Event(Severity.ERROR, getChildPrefix() + "_ID_NULL", "ID mustn't be null.")
    }

    /**
     * Returns event for not existing child data.
     *
     * @return event for not existing child data
     */
    private fun getNotExistingChildDataEvent(): Event {
        return Event(Severity.ERROR, getChildPrefix() + "_NOT_EXIST", getChildName() + " doesn't exist.")
    }

    /**
     * Returns parent prefix for validation keys.
     *
     * @return parent prefix for validation keys
     */
    private fun getParentPrefix(): String {
        return getParentName().toUpperCase()
    }

    /**
     * Returns child prefix for validation keys.
     *
     * @return child prefix for validation keys
     */
    private fun getChildPrefix(): String {
        return getChildName().toUpperCase()
    }

    /**
     * Asserts repository data for [MovableChildFacade.add].
     */
    private fun assertAddRepositoryData() {
        assertSoftly {
            it.assertThat(getRepositoryChildDataCount()).isEqualTo(getDefaultChildDataCount() + 1)
            assertReferences()
        }
    }

}
