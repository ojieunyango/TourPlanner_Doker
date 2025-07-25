package com.example.tour_backend.dto.tour;

import com.example.tour_backend.domain.tour.BudgetType;
import com.example.tour_backend.dto.tour.plan.TravelPlanDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class TourDto {
    private Long tourId;
    private Long userId;
    private String title;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private Integer travelers;
    private BudgetType budget;
    private TravelPlanDto planData;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    @Builder
    public TourDto(Long tourId, Long userId, String title, LocalDate startDate, LocalDate endDate,
                   Integer travelers, BudgetType budget, TravelPlanDto planData,
                   LocalDateTime createDate, LocalDateTime modifiedDate) {
        this.tourId = tourId;
        this.userId = userId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.travelers = travelers;
        this.budget = budget;
        this.planData = planData;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
    }
}