package com.example.tour_backend.dto.tour.plan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStepDto {
    private String mode;
    private String line;
    private String departure;
    private String arrival;
    private String departureTime;
    private String arrivalTime;
}