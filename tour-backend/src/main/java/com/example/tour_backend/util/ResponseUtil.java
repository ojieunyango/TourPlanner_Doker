package com.example.tour_backend.util;

import com.example.tour_backend.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * API 응답 생성 유틸리티
 */
public class ResponseUtil {

    /**
     * 성공 응답 생성
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(data, "요청이 성공적으로 처리되었습니다.");
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 생성 성공 응답 (201 Created)
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return created(data, "리소스가 성공적으로 생성되었습니다.");
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 데이터 없는 성공 응답 (삭제 등)
     */
    public static ResponseEntity<ApiResponse<Void>> success() {
        return success("요청이 성공적으로 처리되었습니다.");
    }

    public static ResponseEntity<ApiResponse<Void>> success(String message) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 에러 응답 생성
     */
    public static ResponseEntity<ApiResponse<Object>> error(HttpStatus status, String message) {
        return error(status, null, message);
    }

    public static ResponseEntity<ApiResponse<Object>> error(HttpStatus status, String errorCode, String message) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorCode", errorCode);
        errorData.put("details", "상세한 오류 정보는 로그를 확인하세요.");

        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(message)
                .data(errorData)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 유효성 검증 실패 응답 (400 Bad Request)
     */
    public static ResponseEntity<ApiResponse<Object>> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    /**
     * 찾을 수 없음 응답 (404 Not Found)
     */
    public static ResponseEntity<ApiResponse<Object>> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", message);
    }

    /**
     * 권한 없음 응답 (403 Forbidden)
     */
    public static ResponseEntity<ApiResponse<Object>> forbidden(String message) {
        return error(HttpStatus.FORBIDDEN, "ACCESS_DENIED", message);
    }

    /**
     * 인증 실패 응답 (401 Unauthorized)
     */
    public static ResponseEntity<ApiResponse<Object>> unauthorized(String message) {
        return error(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED", message);
    }

    /**
     * 서버 내부 오류 응답 (500 Internal Server Error)
     */
    public static ResponseEntity<ApiResponse<Object>> internalError(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", message);
    }

    /**
     * 페이징된 데이터 응답
     */
    public static <T> ResponseEntity<ApiResponse<Map<String, Object>>> pagedSuccess(
            java.util.List<T> content, 
            int page, 
            int size, 
            long totalElements) {
        
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("content", content);
        pageData.put("page", page);
        pageData.put("size", size);
        pageData.put("totalElements", totalElements);
        pageData.put("totalPages", (int) Math.ceil((double) totalElements / size));
        pageData.put("hasNext", (page + 1) * size < totalElements);
        pageData.put("hasPrevious", page > 0);

        return success(pageData, "페이징된 데이터를 성공적으로 조회했습니다.");
    }

    /**
     * 조건부 응답 생성 (Optional 처리)
     */
    @SuppressWarnings("unchecked")
    public static <T> ResponseEntity<ApiResponse<T>> conditionalSuccess(
            java.util.Optional<T> optional, 
            String notFoundMessage) {
        
        if (optional.isPresent()) {
            return success(optional.get());
        } else {
            return (ResponseEntity<ApiResponse<T>>) (ResponseEntity<?>) notFound(notFoundMessage);
        }
    }

    /**
     * 업데이트 성공 응답
     */
    public static <T> ResponseEntity<ApiResponse<T>> updated(T data) {
        return success(data, "리소스가 성공적으로 업데이트되었습니다.");
    }

    /**
     * 삭제 성공 응답
     */
    public static ResponseEntity<ApiResponse<Void>> deleted() {
        return success("리소스가 성공적으로 삭제되었습니다.");
    }

    /**
     * 중복 리소스 응답 (409 Conflict)
     */
    public static ResponseEntity<ApiResponse<Object>> conflict(String message) {
        return error(HttpStatus.CONFLICT, "RESOURCE_CONFLICT", message);
    }

    /**
     * 처리할 수 없는 요청 (422 Unprocessable Entity)
     */
    public static ResponseEntity<ApiResponse<Object>> unprocessableEntity(String message) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", message);
    }
}
