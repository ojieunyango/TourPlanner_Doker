package com.example.tour_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatisticsResponseDto {
    private long userCount;
    private long threadCount;
    private long reportCount;
}