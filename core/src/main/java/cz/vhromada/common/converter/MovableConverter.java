package cz.vhromada.common.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cz.vhromada.common.Movable;

import org.springframework.util.CollectionUtils;

/**
 * An interface represents converter between movable data.
 *
 * @param <T> type of source data
 * @param <U> type of target data
 * @author Vladimir Hromada
 */
public interface MovableConverter<T extends Movable, U extends Movable> {

    /**
     * Converts movable data.
     *
     * @param source source
     * @return converted movable data
     */
    U convert(T source);

    /**
     * Converts movable data.
     *
     * @param source source
     * @return converted movable data
     */
    T convertBack(U source);

    /**
     * Converts list of movable data.
     *
     * @param source source
     * @return converted list of movable data
     */
    default List<U> convert(final List<T> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    /**
     * Converts list of movable data.
     *
     * @param source source
     * @return converted list of movable data
     */
    default List<T> convertBack(final List<U> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
            .map(this::convertBack)
            .collect(Collectors.toList());
    }

}
