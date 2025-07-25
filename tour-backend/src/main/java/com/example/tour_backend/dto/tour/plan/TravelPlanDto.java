package com.example.tour_backend.dto.tour.plan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanDto {
    private List<ScheduleItemDto> schedules;
    private List<WeatherItemDto> weatherData;
    private PlanMetadataDto metadata;
}