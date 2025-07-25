package com.example.tour_backend.dto.tour.plan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanMetadataDto {
    private String version;
    private LocalDateTime lastUpdated;
    private Integer totalDays;
    private Integer estimatedBudget;
    
    // 기본값 설정을 위한 생성자
    public PlanMetadataDto(String version, LocalDateTime lastUpdated) {
        this.version = version != null ? version : "1.0";
        this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDateTime.now();
    }
}