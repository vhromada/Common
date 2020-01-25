package cz.vhromada.common.mapper

/**
 * An interface represents mapper between data.
 *
 * @param <T> type of source data
 * @param <U> type of target data
 * @author Vladimir Hromada
 */
interface Mapper<T, U> {

    /**
     * Maps movable data.
     *
     * @param source source
     * @return converted movable data
     */
    fun map(source: T): U

    /**
     * Maps movable data.
     *
     * @param source source
     * @return converted movable data
     */
    fun mapBack(source: U): T

    /**
     * Maps list of movable data.
     *
     * @param source source
     * @return converted list of movable data
     */
    @Suppress("unused")
    fun map(source: List<T>): List<U> {
        return source.map { map(it) }
    }

    /**
     * Maps list of movable data.
     *
     * @param source source
     * @return converted list of movable data
     */
    fun mapBack(source: List<U>): List<T> {
        return source.map { mapBack(it) }
    }

}
