package com.example.tour_backend.dto.tour.plan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDto {
    private Double lat;
    private Double lng;
}