package in.vhglobal.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Set;

public class NotFoundApiException extends ApiException {

    public NotFoundApiException(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }

    public NotFoundApiException(Set<String> resourceKeys) {
        super(HttpStatus.NOT_FOUND.value(), resourceKeys);
    }

    public NotFoundApiException(String message, Set<String> resourceKeys) {
        super(message, HttpStatus.NOT_FOUND.value(), resourceKeys);
    }

    public NotFoundApiException(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND.value());
    }

    public NotFoundApiException(Throwable cause, String message) {
        super(cause, HttpStatus.NOT_FOUND.value(), message);
    }

    public NotFoundApiException(Throwable cause, Set<String> resourceKeys) {
        super(cause, HttpStatus.NOT_FOUND.value(), resourceKeys);
    }

    public NotFoundApiException(Throwable cause, String message, Set<String> resourceKeys) {
        super(cause, HttpStatus.NOT_FOUND.value(), message, resourceKeys);
    }
}
