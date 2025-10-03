package service.ordermservice.exception

import org.apache.hc.core5.http.HttpStatus

enum class ErrorCode(status: Int, message: String) {
    QUANTITY_LACK(HttpStatus.SC_BAD_REQUEST, "Not Enough Quantity."),
    PRODUCT_NOT_FOUND(HttpStatus.SC_NOT_FOUND, "Check ProductID Again."),
    ORDER_NOT_FOUND(HttpStatus.SC_NOT_FOUND, "Check OrderID Again."),
    INVALID_VALUE(HttpStatus.SC_NOT_ACCEPTABLE, "Impossible Accept."),
    REQUEST_FAILURE(HttpStatus.SC_METHOD_FAILURE, "Update Request Failed"),
    OPEN_FEIGN_FAILURE(HttpStatus.SC_CLIENT_ERROR, "Open Feign Failed")
}