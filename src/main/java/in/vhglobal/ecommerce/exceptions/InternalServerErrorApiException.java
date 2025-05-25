package in.vhglobal.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Set;

public class InternalServerErrorApiException extends ApiException {

    public InternalServerErrorApiException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public InternalServerErrorApiException(Set<String> resourceKeys) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), resourceKeys);
    }

    public InternalServerErrorApiException(String message, Set<String> resourceKeys) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), resourceKeys);
    }

    public InternalServerErrorApiException(Throwable cause) {
        super(cause, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public InternalServerErrorApiException(Throwable cause, String message) {
        super(cause, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    public InternalServerErrorApiException(Throwable cause, Set<String> resourceKeys) {
        super(cause, HttpStatus.INTERNAL_SERVER_ERROR.value(), resourceKeys);
    }

    public InternalServerErrorApiException(Throwable cause, String message, Set<String> resourceKeys) {
        super(cause, HttpStatus.INTERNAL_SERVER_ERROR.value(), message, resourceKeys);
    }
}
