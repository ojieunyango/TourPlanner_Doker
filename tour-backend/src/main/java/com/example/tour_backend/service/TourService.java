package com.example.tour_backend.service;

import com.example.tour_backend.domain.tour.Tour;
import com.example.tour_backend.domain.tour.TourRepository;
import com.example.tour_backend.domain.tour.BudgetType;
import com.example.tour_backend.domain.user.User;
import com.example.tour_backend.domain.user.UserRepository;
import com.example.tour_backend.dto.tour.TourDto;
import com.example.tour_backend.dto.tour.plan.TravelPlanDto;
import com.example.tour_backend.dto.tour.plan.PlanMetadataDto;
import com.example.tour_backend.util.JsonUtil;
import com.example.tour_backend.util.tour.TourDefaults;
import com.example.tour_backend.util.ValidationUtil;
import com.example.tour_backend.exception.GlobalExceptionHandler.TourNotFoundException;
import com.example.tour_backend.exception.GlobalExceptionHandler.InvalidTourDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourService {
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 여행 계획 생성
     */
    @Transactional
    public TourDto createTour(TourDto dto) {
        // 1. 기본값 적용 (검증 전)
        dto = TourDefaults.applyDefaults(dto);
        
        // 2. 입력값 검증 (기본값 적용 후)
        ValidationUtil.validateTourDto(dto);
        
        final Long userId = dto.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTourDataException("사용자가 존재하지 않습니다. ID: " + userId));

        // 계획 데이터 처리
        TravelPlanDto planData = dto.getPlanData();
        if (planData == null) {
            planData = TourDefaults.createDefaultPlan();
        }
        
        // 메타데이터 업데이트
        TourDefaults.updateMetadata(planData, dto.getStartDate(), dto.getEndDate());

        Tour tour = Tour.builder()
                .user(user)
                .title(TourDefaults.sanitizeTitle(dto.getTitle()))
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .travelers(TourDefaults.validateTravelers(dto.getTravelers()))
                .budget(dto.getBudget())
                .planData(JsonUtil.toJson(planData))
                .build();

        tourRepository.save(tour);
        log.info("새로운 여행 계획 생성: tourId={}, title={}", tour.getTourId(), tour.getTitle());

        return convertToDto(tour);
    }

    /**
     * 여행 계획 조회
     */
    public Optional<TourDto> getTour(Long tourId) {
        return tourRepository.findById(tourId)
                .map(this::convertToDto);
    }

    /**
     * 여행 계획 수정
     */
    @Transactional
    public TourDto updateTour(Long tourId, TourDto dto) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException(tourId));

        // 기본 정보 업데이트
        if (dto.getTitle() != null) {
            tour.setTitle(TourDefaults.sanitizeTitle(dto.getTitle()));
        }
        if (dto.getStartDate() != null) {
            tour.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            tour.setEndDate(dto.getEndDate());
        }
        if (dto.getTravelers() != null) {
            tour.setTravelers(TourDefaults.validateTravelers(dto.getTravelers()));
        }
        if (dto.getBudget() != null) {
            tour.setBudget(dto.getBudget());
        }

        // 계획 데이터 업데이트
        if (dto.getPlanData() != null) {
            TourDefaults.updateMetadata(dto.getPlanData(), tour.getStartDate(), tour.getEndDate());
            tour.setPlanData(JsonUtil.toJson(dto.getPlanData()));
        }

        tourRepository.save(tour);
        log.info("여행 계획 업데이트: tourId={}, title={}", tour.getTourId(), tour.getTitle());

        return convertToDto(tour);
    }

    /**
     * 사용자별 여행 계획 목록 조회
     */
    public List<TourDto> getToursByUser(Long userId) {
        return tourRepository.findByUserUserIdOrderByCreateDateDesc(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 모든 여행 계획 조회 (관리자용)
     */
    public List<TourDto> getAllTours() {
        return tourRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 여행 계획 삭제
     */
    @Transactional
    public void deleteTour(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException(tourId));
        
        tourRepository.delete(tour);
        log.info("여행 계획 삭제: tourId={}, title={}", tour.getTourId(), tour.getTitle());
    }

    /**
     * Entity -> DTO 변환
     */
    private TourDto convertToDto(Tour tour) {
        TravelPlanDto planData = JsonUtil.fromJson(tour.getPlanData());
        
        return TourDto.builder()
                .tourId(tour.getTourId())
                .userId(tour.getUser().getUserId())
                .title(tour.getTitle())
                .startDate(tour.getStartDate())
                .endDate(tour.getEndDate())
                .travelers(tour.getTravelers())
                .budget(tour.getBudget())
                .planData(planData)
                .createDate(tour.getCreateDate())
                .modifiedDate(tour.getModifiedDate())
                .build();
    }

    /**
     * 여행 기간 계산 (더이상 사용하지 않음 - TourDefaults로 이동)
     */
    @Deprecated
    private Integer calculateTotalDays(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return TourDefaults.calculateTotalDays(startDate, endDate);
    }
}