package in.vhglobal.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Set;

public class BadRequestApiException extends ApiException {

    public BadRequestApiException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }

    public BadRequestApiException(Set<String> resourceKeys) {
        super(HttpStatus.BAD_REQUEST.value(), resourceKeys);
    }

    public BadRequestApiException(String message, Set<String> resourceKeys) {
        super(message, HttpStatus.BAD_REQUEST.value(), resourceKeys);
    }

    public BadRequestApiException(Throwable cause, Set<String> resourceKeys) {
        super(cause, HttpStatus.BAD_REQUEST.value(), resourceKeys);
    }

    public BadRequestApiException(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST.value());
    }

    public BadRequestApiException(Throwable cause, String message) {
        super(cause, HttpStatus.BAD_REQUEST.value(), message);
    }

    public BadRequestApiException(Throwable cause, String message, Set<String> resourceKeys) {
        super(cause, HttpStatus.BAD_REQUEST.value(), message, resourceKeys);
    }

}
