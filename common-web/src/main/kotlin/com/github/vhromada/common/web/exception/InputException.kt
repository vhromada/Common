package com.github.vhromada.common.web.exception

import com.github.vhromada.common.result.Result
import org.springframework.http.HttpStatus

/**
 * A class represents input exception.
 *
 * @author Vladimir Hromada
 */
class InputException(val result: Result<*>, val httpStatus: HttpStatus = HttpStatus.UNPROCESSABLE_ENTITY) : RuntimeException(result.toString()) {

    @Suppress("unused")
    constructor(key: String, message: String, httpStatus: HttpStatus = HttpStatus.UNPROCESSABLE_ENTITY) : this(Result.error<Unit>(key = key, message = message), httpStatus)

}
