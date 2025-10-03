package service.ordermservice.exception

class BaseException(errorCode: ErrorCode) : RuntimeException() {
    val errorCode = errorCode
}