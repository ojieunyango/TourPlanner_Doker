package com.example.tour_backend.domain.tour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {
    
    /**
     * 사용자별 여행 계획 조회 (최신순)
     */
    List<Tour> findByUserUserIdOrderByCreateDateDesc(Long userId);
    
    /**
     * 제목으로 여행 계획 검색
     */
    List<Tour> findByTitleContainingIgnoreCase(String title);
    
    /**
     * 예산 타입별 여행 계획 조회
     */
    List<Tour> findByBudget(BudgetType budget);
    
    /**
     * 여행자 수별 여행 계획 조회
     */
    List<Tour> findByTravelers(Integer travelers);
    
    /**
     * JSON 데이터에서 특정 위치 검색 (H2 호환)
     */
    @Query("SELECT t FROM Tour t WHERE t.planData LIKE %:locationName%")
    List<Tour> findByLocationName(@Param("locationName") String locationName);
    
    /**
     * 특정 날짜에 일정이 있는 여행 계획 조회 (H2 호환)
     */
    @Query("SELECT t FROM Tour t WHERE t.planData LIKE %:date%")
    List<Tour> findByScheduleDate(@Param("date") String date);
}