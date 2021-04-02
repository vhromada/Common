package com.github.vhromada.common.web.controller

import com.github.vhromada.common.result.Result
import com.github.vhromada.common.web.exception.InputException
import org.springframework.http.HttpStatus

/**
 * An abstract class represents controller.
 *
 * @author Vladimir Hromada
 */
@Suppress("unused")
abstract class AbstractController {

    /**
     * Process result.
     *
     * @param result result
     * @param <T>    type of data
     * @return result data
     */
    protected fun <T> processResult(result: Result<T>): T? {
        if (result.isOk()) {
            return result.data
        }
        if (result.events().stream().anyMatch { it.key.contains("NOT_EXIST") }) {
            throw InputException(result = result, httpStatus = HttpStatus.NOT_FOUND)
        }
        throw InputException(result)
    }

}
