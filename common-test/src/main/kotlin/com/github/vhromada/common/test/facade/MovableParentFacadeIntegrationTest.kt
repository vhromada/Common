package com.github.vhromada.common.test.facade

import com.github.vhromada.common.domain.Audit
import com.github.vhromada.common.domain.AuditEntity
import com.github.vhromada.common.entity.Movable
import com.github.vhromada.common.facade.MovableParentFacade
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.result.Status
import com.github.vhromada.common.test.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * An abstract class represents integration test for [MovableParentFacade].
 *
 * @param <T> type of entity data
 * @param <U> type of domain data
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@Suppress("FunctionName", "Unused")
abstract class MovableParentFacadeIntegrationTest<T : Movable, U : AuditEntity> {

    /**
     * Test method for [MovableParentFacade.newData].
     */
    @Test
    @DirtiesContext
    fun newData() {
        clearReferencedData()

        val result = getFacade().newData()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertNewRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.getAll].
     */
    @Test
    fun getAll() {
        val result = getFacade().getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            assertDataListDeepEquals(result.data!!, getDataList())
            it.assertThat(result.events()).isEmpty()
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..getDefaultDataCount()) {
            val result = getFacade().get(i)

            assertSoftly {
                it.assertThat(result.status).isEqualTo(Status.OK)
                assertDataDeepEquals(result.data!!, getDomainData(i))
                it.assertThat(result.events()).isEmpty()
            }
        }

        val result = getFacade().get(Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEmpty()
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val result = getFacade().add(newData(null, null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertDataDomainDeepEquals(getExpectedAddData(), getRepositoryData(getDefaultDataCount() + 1)!!)
        assertAddRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.add] with data with not null ID.
     */
    @Test
    fun addNotNullId() {
        val result = getFacade().add(newData(1, null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_ID_NOT_NULL", "ID must be null.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.add] with data with not null position.
     */
    @Test
    fun addNotNullPosition() {
        val result = getFacade().add(newData(null, 1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_POSITION_NOT_NULL", "Position must be null.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val data = getUpdateData(1)

        val result = getFacade().update(data)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertDataDeepEquals(data, getRepositoryData(1)!!)
        assertUpdateRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.update] with data with null ID.
     */
    @Test
    fun updateNullId() {
        val result = getFacade().update(getUpdateData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.update] with data with null position.
     */
    @Test
    fun updateNullPosition() {
        val data = getUpdateData(1)
        data.position = null

        val result = getFacade().update(data)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_POSITION_NULL", "Position mustn't be null.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.update] with data with bad ID.
     */
    @Test
    fun updateBadId() {
        val result = getFacade().update(getUpdateData(Int.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.remove].
     */
    @Test
    @DirtiesContext
    fun remove() {
        clearReferencedData()

        val result = getFacade().remove(newData(1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertThat(getRepositoryData(1)).isNull()
        assertRemoveRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.remove] with data with null ID.
     */
    @Test
    fun removeNullId() {
        val result = getFacade().remove(newData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.remove] with data with bad ID.
     */
    @Test
    fun removeBadId() {
        val result = getFacade().remove(newData(Int.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val result = getFacade().duplicate(newData(getDefaultDataCount()))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        assertDataDomainDeepEquals(getExpectedDuplicatedData(), getRepositoryData(getDefaultDataCount() + 1)!!)
        assertDuplicateRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.duplicate] with data with null ID.
     */
    @Test
    fun duplicateNullId() {
        val result = getFacade().duplicate(newData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.duplicate] with data with bad ID.
     */
    @Test
    fun duplicateBadId() {
        val result = getFacade().duplicate(newData(Int.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.moveUp].
     */
    @Test
    @DirtiesContext
    @Suppress("DuplicatedCode")
    fun moveUp() {
        val result = getFacade().moveUp(newData(2))

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
        for (i in 3..getDefaultDataCount()) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i)!!)
        }
        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.moveUp] with data with null ID.
     */
    @Test
    fun moveUpNullId() {
        val result = getFacade().moveUp(newData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.moveUp] with not movable data.
     */
    @Test
    fun moveUpNotMovableData() {
        val result = getFacade().moveUp(newData(1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_NOT_MOVABLE", "${getName()} can't be moved up.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.moveUp] with data with bad ID.
     */
    @Test
    fun moveUpBadId() {
        val result = getFacade().moveUp(newData(Int.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.moveDown].
     */
    @Test
    @DirtiesContext
    @Suppress("DuplicatedCode")
    fun moveDown() {
        val result = getFacade().moveDown(newData(1))

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
        for (i in 3..getDefaultDataCount()) {
            assertDataDomainDeepEquals(getDomainData(i), getRepositoryData(i)!!)
        }
        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.moveDown] with data with null ID.
     */
    @Test
    fun moveDownNullId() {
        val result = getFacade().moveDown(newData(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNullDataIdEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.moveDown] with not movable data.
     */
    @Test
    fun moveDownNotMovableData() {
        val result = getFacade().moveDown(newData(getDefaultDataCount()))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, getPrefix() + "_NOT_MOVABLE", "${getName()} can't be moved down.")))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.moveDown] with data with bad ID.
     */
    @Test
    fun moveDownBadId() {
        val result = getFacade().moveDown(newData(Int.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(getNotExistingDataEvent()))
        }

        assertDefaultRepositoryData()
    }

    /**
     * Test method for [MovableParentFacade.updatePositions].
     */
    @Test
    @DirtiesContext
    fun updatePositions() {
        val result = getFacade().updatePositions()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        for (i in 1..getDefaultDataCount()) {
            assertDataDomainDeepEquals(getExpectedUpdatePositionData(i), getRepositoryData(i)!!)
        }
        assertDefaultRepositoryData()
    }

    /**
     * Returns facade for movable data for parent data.
     *
     * @return facade for movable data for parent data
     */
    protected abstract fun getFacade(): MovableParentFacade<T>

    /**
     * Returns default count of data.
     *
     * @return default count of data
     */
    protected abstract fun getDefaultDataCount(): Int

    /**
     * Returns repository count of data.
     *
     * @return repository count of data
     */
    protected abstract fun getRepositoryDataCount(): Int

    /**
     * Returns list of data.
     *
     * @return list of data
     */
    protected abstract fun getDataList(): List<U>

    /**
     * Returns domain data.
     *
     * @param index index
     * @return domain data
     */
    protected abstract fun getDomainData(index: Int): U

    /**
     * Returns new data.
     *
     * @param id ID
     * @return new data
     */
    protected abstract fun newData(id: Int?): T

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
     * Returns name of entity.
     *
     * @return name of entity
     */
    protected abstract fun getName(): String

    /**
     * Clears referenced data.
     */
    protected abstract fun clearReferencedData()

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
     * Returns update data.
     *
     * @param id ID
     * @return update data
     */
    protected open fun getUpdateData(id: Int?): T {
        return newData(id, 0)
    }

    /**
     * Returns expected add data.
     *
     * @return expected add data
     */
    protected open fun getExpectedAddData(): U {
        return newDomainData(getDefaultDataCount() + 1)
    }

    /**
     * Returns expected duplicated data.
     *
     * @return expected duplicated data
     */
    protected open fun getExpectedDuplicatedData(): U {
        val data = getDomainData(getDefaultDataCount())
        data.id = getDefaultDataCount() + 1
        data.audit = getUpdatedAudit()

        return data
    }

    /**
     * Returns expected update position data.
     *
     * @param index index of data
     * @return expected update position data
     */
    protected open fun getExpectedUpdatePositionData(index: Int): U {
        val data = getDomainData(index)
        data.modify(getUpdatedAudit())

        return data
    }

    /**
     * Returns audit for update.
     *
     * @return audit for update
     */
    protected open fun getUpdatedAudit(): Audit {
        return Audit(TestConstants.ACCOUNT_UUID, TestConstants.TIME)
    }

    /**
     * Returns prefix for validation keys.
     *
     * @return prefix for validation keys
     */
    protected open fun getPrefix(): String {
        return getName().toUpperCase()
    }

    /**
     * Asserts default repository data.
     */
    protected open fun assertDefaultRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount())
    }

    /**
     * Asserts repository data for [MovableParentFacade.newData].
     */
    protected open fun assertNewRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(0)
    }

    /**
     * Asserts repository data for [MovableParentFacade.add].
     */
    protected open fun assertAddRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount() + 1)
    }

    /**
     * Asserts repository data for [MovableParentFacade.update].
     */
    protected open fun assertUpdateRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount())
    }

    /**
     * Asserts repository data for [MovableParentFacade.remove].
     */
    protected open fun assertRemoveRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount() - 1)
    }

    /**
     * Asserts repository data for [MovableParentFacade.duplicate].
     */
    protected open fun assertDuplicateRepositoryData() {
        assertThat(getRepositoryDataCount()).isEqualTo(getDefaultDataCount() + 1)
    }

    /**
     * Returns new data.
     *
     * @param id       ID
     * @param position position
     * @return new data
     */
    private fun newData(id: Int?, position: Int?): T {
        val data = newData(id)
        data.position = position

        return data
    }

    /**
     * Returns event for data with null ID.
     *
     * @return event for data with null ID
     */
    private fun getNullDataIdEvent(): Event {
        return Event(Severity.ERROR, getPrefix() + "_ID_NULL", "ID mustn't be null.")
    }

    /**
     * Returns event for not existing data.
     *
     * @return event for not existing data
     */
    private fun getNotExistingDataEvent(): Event {
        return Event(Severity.ERROR, getPrefix() + "_NOT_EXIST", getName() + " doesn't exist.")
    }

}
