package lt.ltrp.dao

/**
 * Created by Bebras on 2016-10-26.
 * A base class for all DAO related exceptions
 */
class DaoException: Exception {

    constructor(): super() { }
    constructor(message: String): super(message) { }
    constructor(message: String, cause: Throwable): super(message, cause) { }

}