package in.vhglobal.ecommerce.dtos.reponse;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private LocalDateTime timestamp;
    private boolean success;
    private String message;
    private T data;
    private int status;
    private String resourceKey;   // Optional: e.g., "USER_NOT_FOUND"

    public static <T> ApiResponse<T> success(String message, T data, int status) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message(message)
                .data(data)
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data, int status, String resourceKey) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message(message)
                .data(data)
                .resourceKey(resourceKey)
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, int status, String resourceKey) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .status(status)
                .message(message)
                .resourceKey(resourceKey)
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, int status) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message(message)
                .status(status)
                .build();
    }
}
