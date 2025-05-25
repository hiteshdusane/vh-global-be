package in.vhglobal.ecommerce.exceptions;

import lombok.Getter;

import java.util.Set;

@Getter
public abstract class ApiException extends RuntimeException {
    int status;
    Set<String> resourceKeys;

    protected ApiException(int status, Set<String> resourceKeys) {
        this.status = status;
        this.resourceKeys = resourceKeys;
    }

    protected ApiException(String message, int status) {
        super(message);
        this.status = status;
    }

    protected ApiException(String message, int status, Set<String> resourceKeys) {
        super(message);
        this.status = status;
        this.resourceKeys = resourceKeys;
    }

    protected ApiException(Throwable cause, int status, Set<String> resourceKeys) {
        super(cause);
        this.resourceKeys = resourceKeys;
        this.status = status;
    }

    protected ApiException(Throwable cause, int status) {
        super(cause);
        this.status = status;
    }

    protected ApiException(Throwable cause, int status, String message) {
        super(message, cause);
        this.status = status;
    }

    protected ApiException(Throwable cause, int status, String message, Set<String> resourceKeys) {
        super(message, cause);
        this.resourceKeys = resourceKeys;
        this.status = status;
    }
}