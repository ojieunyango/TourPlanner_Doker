package com.example.tour_backend.controller;

import com.example.tour_backend.dto.common.ApiResponse;
import com.example.tour_backend.dto.tour.TourDto;
import com.example.tour_backend.service.TourService;
import com.example.tour_backend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TourController {
    
    private final TourService tourService;

    /**
     * 새로운 여행 계획 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TourDto>> createTour(@RequestBody TourDto tourDto) {
        log.info("여행 계획 생성 요청: title={}, userId={}", tourDto.getTitle(), tourDto.getUserId());
        TourDto createdTour = tourService.createTour(tourDto);
        return ResponseUtil.created(createdTour, "여행 계획이 성공적으로 생성되었습니다.");
    }

    /**
     * 여행 계획 조회
     */
    @GetMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourDto>> getTour(@PathVariable Long tourId) {
        return ResponseUtil.conditionalSuccess(
                tourService.getTour(tourId),
                "여행 계획을 찾을 수 없습니다. ID: " + tourId
        );
    }

    /**
     * 여행 계획 수정
     */
    @PutMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourDto>> updateTour(@PathVariable Long tourId, @RequestBody TourDto tourDto) {
        log.info("여행 계획 수정 요청: tourId={}, title={}", tourId, tourDto.getTitle());
        TourDto updatedTour = tourService.updateTour(tourId, tourDto);
        return ResponseUtil.updated(updatedTour);
    }

    /**
     * 사용자별 여행 계획 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TourDto>>> getToursByUser(@PathVariable Long userId) {
        List<TourDto> tours = tourService.getToursByUser(userId);
        return ResponseUtil.success(tours, "사용자의 여행 계획 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 모든 여행 계획 조회 (관리자용)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TourDto>>> getAllTours() {
        List<TourDto> tours = tourService.getAllTours();
        return ResponseUtil.success(tours, "모든 여행 계획 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 여행 계획 삭제
     */
    @DeleteMapping("/{tourId}")
    public ResponseEntity<ApiResponse<Void>> deleteTour(@PathVariable Long tourId) {
        log.info("여행 계획 삭제 요청: tourId={}", tourId);
        tourService.deleteTour(tourId);
        return ResponseUtil.deleted();
    }

    /**
     * 여행 계획 검색 (제목 기반) - 추후 구현
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TourDto>>> searchTours(@RequestParam String keyword) {
        // 추후 검색 기능 구현 시 사용
        return ResponseUtil.success(List.of(), "검색 기능은 추후 구현 예정입니다.");
    }

    /**
     * 여행 계획 복사
     */
    @PostMapping("/{tourId}/copy")
    public ResponseEntity<ApiResponse<TourDto>> copyTour(@PathVariable Long tourId, @RequestParam Long newUserId) {
        log.info("여행 계획 복사 요청: originalTourId={}, newUserId={}", tourId, newUserId);
        
        // 원본 여행 계획 조회
        TourDto originalTour = tourService.getTour(tourId)
                .orElseThrow(() -> new RuntimeException("복사할 여행 계획을 찾을 수 없습니다."));
        
        // 새로운 여행 계획으로 복사
        TourDto newTour = TourDto.builder()
                .userId(newUserId)
                .title(originalTour.getTitle() + " (복사본)")
                .startDate(originalTour.getStartDate())
                .endDate(originalTour.getEndDate())
                .travelers(originalTour.getTravelers())
                .budget(originalTour.getBudget())
                .planData(originalTour.getPlanData())
                .build();
        
        TourDto copiedTour = tourService.createTour(newTour);
        return ResponseUtil.created(copiedTour, "여행 계획이 성공적으로 복사되었습니다.");
    }

    /**
     * 사용자의 여행 계획 개수 조회
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Integer>> getUserTourCount(@PathVariable Long userId) {
        List<TourDto> tours = tourService.getToursByUser(userId);
        return ResponseUtil.success(tours.size(), "사용자의 여행 계획 개수를 조회했습니다.");
    }
}
