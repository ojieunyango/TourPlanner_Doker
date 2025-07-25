package com.example.tour_backend.dto.tour.plan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficDataDto {
    private String mode;
    private String departure;
    private String destination;
    private Integer price;
    private List<RouteStepDto> route;
    private String totalDuration;
    private Integer transfers;
}