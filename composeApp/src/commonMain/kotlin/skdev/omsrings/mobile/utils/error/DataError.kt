package skdev.omsrings.mobile.utils.error

sealed interface DataError : Error {
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        NO_INTERNET,
        UNAUTHORIZED,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
    }

    enum class Local : DataError {
        READ_ERROR,
        NO_DATA,
        WRITE_ERROR,
        USER_NOT_FOUND,
        USER_NOT_LOGGINED_IN
    }
}
