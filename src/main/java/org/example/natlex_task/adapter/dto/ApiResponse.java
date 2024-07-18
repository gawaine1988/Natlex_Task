package org.example.natlex_task.adapter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.HttpStatus.OK;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private int statusCode;

    private String statusMessage;

    @JsonInclude(NON_NULL)
    private T response;

    public static <T> ApiResponse<T> ok() {
        return buildResponse(OK, null);
    }

    public static <T> ApiResponse<T> ok(T responseObject) {
        return buildResponse(OK, responseObject);
    }

    public static <T> ApiResponse<T> ok(String statusMessage, T responseObject) {
        return buildResponse(OK, statusMessage, responseObject);
    }

    public static <T> ApiResponse<T> buildResponse(HttpStatus status, T responseObject) {
        return new ApiResponse<>(status.value(), status.getReasonPhrase(), responseObject);
    }

    public static <T> ApiResponse<T> buildResponse(HttpStatus status, String message, T responseObject) {
        return new ApiResponse<>(status.value(), message, responseObject);
    }
}
