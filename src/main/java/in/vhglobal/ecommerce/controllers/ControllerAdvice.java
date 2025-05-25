package in.vhglobal.ecommerce.controllers;

import in.vhglobal.ecommerce.dtos.reponse.ApiResponse;
import in.vhglobal.ecommerce.exceptions.ApiException;
import in.vhglobal.ecommerce.exceptions.BadRequestApiException;
import in.vhglobal.ecommerce.exceptions.InternalServerErrorApiException;
import in.vhglobal.ecommerce.exceptions.NotFoundApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;
import java.util.Set;

import static in.vhglobal.ecommerce.constants.ResourceKey.*;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        log.error("The following API exception was thrown: ", ex);
        String resourceKey = null;
        if (Objects.nonNull(ex.getResourceKeys()) && !ex.getResourceKeys().isEmpty()) {
            resourceKey = String.join("|", ex.getResourceKeys());
        }
        return new ResponseEntity<>(
                ApiResponse.failure(ex.getMessage(), ex.getStatus(), resourceKey),
                HttpStatus.valueOf(ex.getStatus())
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        NotFoundApiException notFoundApiException = new NotFoundApiException(ex, ex.getMessage(), Set.of(NOT_FOUND));
        return handleApiException(notFoundApiException);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BadRequestApiException badRequestApiException = new BadRequestApiException(ex, Objects.requireNonNull(ex.getFieldError()).getDefaultMessage(), Set.of(BAD_REQUEST));
        return handleApiException(badRequestApiException);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex) {
        InternalServerErrorApiException internalServerErrorApiException = new InternalServerErrorApiException(ex, "Internal Server Error Occurred", Set.of(SERVICE_ERROR));
        return handleApiException(internalServerErrorApiException);
    }
}
