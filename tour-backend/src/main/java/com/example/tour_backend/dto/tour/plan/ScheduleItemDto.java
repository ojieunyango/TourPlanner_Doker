package com.example.tour_backend.dto.tour.plan;



import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItemDto {

    private String scheduleId;
    private Long tourId;
    private String date;
    private String startTime;
    private String endTime;
    private String title;
    private String content;
    @Nullable
    private String memo; // 메모 추가
    // 타입을 배열로 변경 (여러 타입 지정 가능)
    @Nullable
    private List<String> types;
    // private String type; // "location" or "traffic"
    @Nullable
    private LocationDataDto locationData;
    @Nullable
    private TrafficDataDto trafficData;
}