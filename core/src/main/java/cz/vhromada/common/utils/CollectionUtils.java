package cz.vhromada.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.vhromada.common.Movable;

/**
 * A class represents utility class for working with collections.
 *
 * @author Vladimir Hromada
 */
public final class CollectionUtils {

    /**
     * Creates a new instance of CollectionUtils.
     */
    private CollectionUtils() {
    }

    /**
     * Creates a new list with data.
     *
     * @param data data
     * @param <T>  type of data
     * @return list with data
     */
    @SafeVarargs
    public static <T> List<T> newList(final T... data) {
        return new ArrayList<>(Arrays.asList(data));
    }

    /**
     * Returns sorted data.
     *
     * @param data data for sorting
     * @param <T>  type of data
     * @return sorted data
     */
    public static <T extends Movable> List<T> getSortedData(final List<T> data) {
        if (org.springframework.util.CollectionUtils.isEmpty(data)) {
            return Collections.emptyList();
        }

        final List<T> sortedData = new ArrayList<>(data);
        sortedData.sort((o1, o2) -> {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            final int result = Integer.compare(o1.getPosition(), o2.getPosition());
            if (result == 0) {
                return Long.compare(o1.getId(), o2.getId());
            }

            return result;
        });

        return sortedData;
    }

}
