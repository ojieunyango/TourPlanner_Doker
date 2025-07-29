
package com.example.tour_backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Tour 관련 예외 처리
     */
    @ExceptionHandler(TourNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTourNotFound(TourNotFoundException ex) {
        log.warn("Tour not found: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, "TOUR_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InvalidTourDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTourData(InvalidTourDataException ex) {
        log.warn("Invalid tour data: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_TOUR_DATA", ex.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleJsonProcessing(JsonProcessingException ex) {
        log.error("JSON processing error: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "JSON_PROCESSING_ERROR", 
                "여행 계획 데이터 처리 중 오류가 발생했습니다.");
    }

    /**
     * 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.BAD_REQUEST, "RUNTIME_ERROR", ex.getMessage());
    }

    /**
     * 예상하지 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", 
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }

    /**
     * 에러 응답 생성 헬퍼 메소드
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String errorCode, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 커스텀 예외 클래스들
     */
    public static class TourNotFoundException extends RuntimeException {
        public TourNotFoundException(String message) {
            super(message);
        }
        
        public TourNotFoundException(Long tourId) {
            super("여행 계획을 찾을 수 없습니다. ID: " + tourId);
        }
    }

    public static class InvalidTourDataException extends RuntimeException {
        public InvalidTourDataException(String message) {
            super(message);
        }
    }

    public static class JsonProcessingException extends RuntimeException {
        public JsonProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
