package com.example.tour_backend.dto.tour.plan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherItemDto {
    private String date;
    private Double temperature;
    private String description;
    private String icon;
}