package com.example.tour_backend.dto.tour.plan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDataDto {
    private String name;
    private String address;
    private CoordinatesDto coordinates;
    private String placeId;
    private String googleMapLink;
    private Double rating;
    private String photoUrl;
}