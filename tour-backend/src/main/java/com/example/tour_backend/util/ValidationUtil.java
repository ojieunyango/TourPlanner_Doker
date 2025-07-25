package com.example.tour_backend.util;

import com.example.tour_backend.domain.tour.BudgetType;
import com.example.tour_backend.dto.tour.TourDto;
import com.example.tour_backend.exception.GlobalExceptionHandler.InvalidTourDataException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 데이터 유효성 검증 유틸리티
 */
@Slf4j
public class ValidationUtil {
    
    private static final int MIN_TRAVELERS = 1;
    private static final int MAX_TRAVELERS = 50;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_TRIP_DAYS = 365; // 최대 1년
    
    /**
     * TourDto 전체 검증
     */
    public static void validateTourDto(TourDto dto) {
        List<String> errors = new ArrayList<>();
        
        // 필수 필드 검증
        if (dto.getUserId() == null) {
            errors.add("사용자 ID는 필수입니다.");
        }
        
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            errors.add("제목은 필수입니다.");
        }
        
        if (dto.getStartDate() == null) {
            errors.add("시작일은 필수입니다.");
        }
        
        if (dto.getEndDate() == null) {
            errors.add("종료일은 필수입니다.");
        }
        
        // 개별 필드 검증
        if (dto.getTitle() != null) {
            validateTitle(dto.getTitle(), errors);
        }
        
        if (dto.getTravelers() != null) {
            validateTravelers(dto.getTravelers(), errors);
        }
        
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            validateDateRange(dto.getStartDate(), dto.getEndDate(), errors);
        }
        
        if (!errors.isEmpty()) {
            String message = String.join(", ", errors);
            log.warn("Tour validation failed: {}", message);
            throw new InvalidTourDataException(message);
        }
    }
    
    /**
     * 제목 검증
     */
    public static void validateTitle(String title, List<String> errors) {
        if (title == null || title.trim().isEmpty()) {
            errors.add("제목은 필수입니다.");
            return;
        }
        
        if (title.length() > MAX_TITLE_LENGTH) {
            errors.add("제목은 " + MAX_TITLE_LENGTH + "자를 초과할 수 없습니다.");
        }
        
        // 특수문자 검증 (기본적인 보안)
        if (title.contains("<") || title.contains(">") || title.contains("script")) {
            errors.add("제목에 허용되지 않는 문자가 포함되어 있습니다.");
        }
    }
    
    /**
     * 여행자 수 검증
     */
    public static void validateTravelers(Integer travelers, List<String> errors) {
        if (travelers == null) {
            errors.add("여행자 수는 필수입니다.");
            return;
        }
        
        if (travelers < MIN_TRAVELERS) {
            errors.add("여행자 수는 최소 " + MIN_TRAVELERS + "명 이상이어야 합니다.");
        }
        
        if (travelers > MAX_TRAVELERS) {
            errors.add("여행자 수는 최대 " + MAX_TRAVELERS + "명을 초과할 수 없습니다.");
        }
    }
    
    /**
     * 날짜 범위 검증 (과거 날짜 허용 - 과거 여행 기록용)
     */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate, List<String> errors) {
        if (startDate == null || endDate == null) {
            errors.add("시작일과 종료일은 필수입니다.");
            return;
        }
        
        // 종료일이 시작일보다 빠른지 검증
        if (endDate.isBefore(startDate)) {
            errors.add("종료일은 시작일과 같거나 이후 날짜여야 합니다.");
        }
        
        // 너무 긴 여행 기간 검증
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days > MAX_TRIP_DAYS) {
            errors.add("여행 기간은 최대 " + MAX_TRIP_DAYS + "일을 초과할 수 없습니다.");
        }
    }
    
    /**
     * 예산 타입 검증
     */
    public static void validateBudgetType(BudgetType budget, List<String> errors) {
        if (budget == null) {
            errors.add("예산 유형은 필수입니다.");
            return;
        }
        
        // BudgetType enum 유효성은 자동으로 검증되므로 추가 검증 불필요
        log.debug("Budget type validated: {}", budget);
    }
    
    /**
     * ID 검증 (양수인지 확인)
     */
    public static void validateId(Long id, String fieldName, List<String> errors) {
        if (id == null) {
            errors.add(fieldName + "는 필수입니다.");
            return;
        }
        
        if (id <= 0) {
            errors.add(fieldName + "는 양수여야 합니다.");
        }
    }
    
    /**
     * 간단한 검증 메소드들
     */
    public static boolean isValidTitle(String title) {
        return title != null && 
               !title.trim().isEmpty() && 
               title.length() <= MAX_TITLE_LENGTH &&
               !title.contains("<") && 
               !title.contains(">") && 
               !title.contains("script");
    }
    
    public static boolean isValidTravelers(Integer travelers) {
        return travelers != null && 
               travelers >= MIN_TRAVELERS && 
               travelers <= MAX_TRAVELERS;
    }
    
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        
        LocalDate today = LocalDate.now();
        
        return !startDate.isBefore(today) && 
               !endDate.isBefore(startDate) &&
               java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1 <= MAX_TRIP_DAYS;
    }
    
    /**
     * 문자열 정리 및 검증
     */
    public static String sanitizeString(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        
        String cleaned = input.trim()
                .replaceAll("<[^>]*>", "")  // HTML 태그 제거
                .replaceAll("script", "")   // script 단어 제거
                .replaceAll("[\\r\\n]+", " "); // 개행문자를 공백으로
        
        if (cleaned.length() > maxLength) {
            cleaned = cleaned.substring(0, maxLength);
        }
        
        return cleaned;
    }
}
