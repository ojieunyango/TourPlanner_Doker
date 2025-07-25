package com.example.tour_backend.util.tour;

import com.example.tour_backend.domain.tour.BudgetType;
import com.example.tour_backend.dto.tour.TourDto;
import com.example.tour_backend.dto.tour.plan.PlanMetadataDto;
import com.example.tour_backend.dto.tour.plan.TravelPlanDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Tour 관련 기본값 및 정책 관리
 */
public class TourDefaults {
    
    // 기본값 상수
    public static final int DEFAULT_TRAVELERS = 2;
    public static final BudgetType DEFAULT_BUDGET = BudgetType.MEDIUM;
    public static final String DEFAULT_PLAN_VERSION = "1.0";
    
    /**
     * TourDto에 기본값 적용
     */
    public static TourDto applyDefaults(TourDto dto) {
        if (dto == null) {
            return createDefaultTour();
        }
        
        // 기본값 적용
        if (dto.getTravelers() == null || dto.getTravelers() < 1) {
            dto.setTravelers(DEFAULT_TRAVELERS);
        }
        
        if (dto.getBudget() == null) {
            dto.setBudget(DEFAULT_BUDGET);
        }
        
        // 여행자 수 유효성 검사 (1-50명)
        if (dto.getTravelers() > 50) {
            dto.setTravelers(50);
        }
        
        // 제목이 비어있으면 기본 제목 설정
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            dto.setTitle("나의 여행 계획");
        }
        
        return dto;
    }
    
    /**
     * 기본 Tour 객체 생성
     */
    public static TourDto createDefaultTour() {
        TourDto tour = new TourDto();
        tour.setTitle("나의 여행 계획");
        tour.setTravelers(DEFAULT_TRAVELERS);
        tour.setBudget(DEFAULT_BUDGET);
        return tour;
    }
    
    /**
     * 기본 TravelPlan 객체 생성
     */
    public static TravelPlanDto createDefaultPlan() {
        TravelPlanDto plan = new TravelPlanDto();
        plan.setSchedules(new ArrayList<>());
        plan.setWeatherData(new ArrayList<>());
        
        PlanMetadataDto metadata = new PlanMetadataDto();
        metadata.setVersion(DEFAULT_PLAN_VERSION);
        metadata.setLastUpdated(LocalDateTime.now());
        plan.setMetadata(metadata);
        
        return plan;
    }
    
    /**
     * 여행 기간 계산
     */
    public static Integer calculateTotalDays(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    /**
     * PlanMetadata 업데이트
     */
    public static void updateMetadata(TravelPlanDto plan, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (plan == null) {
            return;
        }
        
        if (plan.getMetadata() == null) {
            plan.setMetadata(new PlanMetadataDto());
        }
        
        PlanMetadataDto metadata = plan.getMetadata();
        metadata.setLastUpdated(LocalDateTime.now());
        metadata.setVersion(DEFAULT_PLAN_VERSION);
        metadata.setTotalDays(calculateTotalDays(startDate, endDate));
        
        // 일정 개수 기반 예상 예산 계산 (간단한 로직)
        if (plan.getSchedules() != null && metadata.getEstimatedBudget() == null) {
            int scheduleCount = plan.getSchedules().size();
            metadata.setEstimatedBudget(scheduleCount * 50000); // 일정당 5만원 기준
        }
    }
    
    /**
     * 입력값 검증 및 정리
     */
    public static String sanitizeTitle(String title) {
        if (title == null) {
            return "나의 여행 계획";
        }
        
        String cleaned = title.trim();
        if (cleaned.isEmpty()) {
            return "나의 여행 계획";
        }
        
        // 제목 길이 제한 (100자)
        if (cleaned.length() > 100) {
            cleaned = cleaned.substring(0, 100);
        }
        
        return cleaned;
    }
    
    /**
     * 여행자 수 유효성 검사
     */
    public static Integer validateTravelers(Integer travelers) {
        if (travelers == null || travelers < 1) {
            return DEFAULT_TRAVELERS;
        }
        
        if (travelers > 50) {
            return 50;
        }
        
        return travelers;
    }
}