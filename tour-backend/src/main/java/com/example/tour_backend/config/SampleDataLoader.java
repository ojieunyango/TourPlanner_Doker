package com.example.tour_backend.config;

import com.example.tour_backend.domain.tour.Tour;
import com.example.tour_backend.domain.tour.TourRepository;
import com.example.tour_backend.domain.tour.BudgetType;
import com.example.tour_backend.domain.user.User;
import com.example.tour_backend.domain.user.UserRepository;
import com.example.tour_backend.dto.tour.plan.*;
import com.example.tour_backend.util.tour.TourDefaults;
import com.example.tour_backend.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Profile("dev") // 개발 환경에서만 실행
@RequiredArgsConstructor
public class SampleDataLoader implements CommandLineRunner {
    
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (tourRepository.count() == 0) {
            createSampleTours();
            log.info("샘플 여행 데이터가 생성되었습니다.");
        }
    }

    private void createSampleTours() {
        // 샘플 사용자 조회 또는 생성
        List<User> users = userRepository.findAll();
        User sampleUser;
        
        if (users.isEmpty()) {
            // 테스트용 사용자 생성
            sampleUser = User.builder()
                    .password("test123")
                    .name("테스트사용자")
                    .email("test@test.com")
                    .phone("010-1234-5678")
                    .nickname("테스트")
                    .build();
            
            // username 설정 (필수 필드이지만 Builder에 없음)
            sampleUser.setUsername("test_user");
            
            userRepository.save(sampleUser);
            log.info("테스트용 사용자가 생성되었습니다. ID: {}", sampleUser.getUserId());
            
            // 추가로 ID 10번 사용자도 생성 (프론트엔드 로그인 사용자용)
            User loginUser = User.builder()
                    .password("123")
                    .name("로그인사용자")
                    .email("login@test.com")
                    .phone("010-9876-5432")
                    .nickname("로그인")
                    .build();
            
            loginUser.setUsername("123"); // 프론트엔드에서 로그인한 username과 일치
            userRepository.save(loginUser);
            log.info("로그인 사용자가 생성되었습니다. ID: {}", loginUser.getUserId());
        } else {
            sampleUser = users.get(0);
        }

        // 샘플 여행 계획 1: 서울 2박 3일
        TravelPlanDto seoulPlan = createSeoulTravelPlan();
        Tour seoulTour = Tour.builder()
                .user(sampleUser)
                .title("서울 2박 3일 여행")
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusDays(9))
                .travelers(2)
                .budget(BudgetType.MEDIUM)
                .planData(JsonUtil.toJson(seoulPlan))
                .build();
        
        tourRepository.save(seoulTour);

        // 샘플 여행 계획 2: 부산 당일치기
        TravelPlanDto busanPlan = createBusanTravelPlan();
        Tour busanTour = Tour.builder()
                .user(sampleUser)
                .title("부산 당일치기")
                .startDate(LocalDate.now().plusDays(14))
                .endDate(LocalDate.now().plusDays(14))
                .travelers(4)
                .budget(BudgetType.LOW)
                .planData(JsonUtil.toJson(busanPlan))
                .build();
        
        tourRepository.save(busanTour);
    }

    private TravelPlanDto createSeoulTravelPlan() {
        // 일정 데이터
        List<ScheduleItemDto> schedules = Arrays.asList(
            createLocationSchedule("schedule_1", 1L,"2025-07-15", "09:00", "11:00",
                "경복궁 관람", "조선 왕조의 정궁 관람","꼭 가고 싶었던 곳!",
                "경복궁", "서울특별시 종로구 사직로 161", 37.5796, 126.9770, 4.3),
            
            createTrafficSchedule("schedule_2", 2L,"2025-07-15", "11:15", "11:20",
                "지하철 3호선 이용", "경복궁역 → 안국역","꼭 가고 싶었던 곳!",
                "경복궁역", "안국역", 1500, "5분", 0),
            
            createLocationSchedule("schedule_3", 3L, "2025-07-15", "11:30", "13:00",
                "북촌한옥마을 산책", "전통 한옥의 아름다움 감상", "꼭 가고 싶었던 곳!",
                "북촌한옥마을", "서울특별시 종로구 계동길 37", 37.5816, 126.9839, 4.1)
        );

        // 날씨 데이터
        List<WeatherItemDto> weatherData = Arrays.asList(
            new WeatherItemDto("2025-07-15", 28.0, "맑음", "01d"),
            new WeatherItemDto("2025-07-16", 30.0, "구름 조금", "02d"),
            new WeatherItemDto("2025-07-17", 26.0, "소나기", "10d")
        );

        // 메타데이터
        PlanMetadataDto metadata = new PlanMetadataDto("1.0", LocalDateTime.now(), 
                TourDefaults.calculateTotalDays(
                    LocalDate.now().plusDays(7), 
                    LocalDate.now().plusDays(9)
                ), 200000);

        return new TravelPlanDto(schedules, weatherData, metadata);
    }

    private TravelPlanDto createBusanTravelPlan() {
        List<ScheduleItemDto> schedules = Arrays.asList(
            createLocationSchedule("schedule_1", 4L, "2025-07-22", "10:00", "12:00",
                "해운대 해수욕장", "부산 대표 해변 휴양", "꼭 가고 싶었던 곳!",
                "해운대해수욕장", "부산광역시 해운대구 해운대해변로 264", 35.1587, 129.1603, 4.2),
            
            createLocationSchedule("schedule_2", 5L,"2025-07-22", "14:00", "16:00",
                "감천문화마을", "부산의 마추픽추", "꼭 가고 싶었던 곳!",
                "감천문화마을", "부산광역시 사하구 감내2로 203", 35.0975, 129.0107, 4.1)
        );

        List<WeatherItemDto> weatherData = Arrays.asList(
            new WeatherItemDto("2025-07-22", 32.0, "맑음", "01d")
        );

        PlanMetadataDto metadata = new PlanMetadataDto("1.0", LocalDateTime.now(), 
                TourDefaults.calculateTotalDays(
                    LocalDate.now().plusDays(14), 
                    LocalDate.now().plusDays(14)
                ), 80000);

        return new TravelPlanDto(schedules, weatherData, metadata);
    }

    private ScheduleItemDto createLocationSchedule(String id, Long tourId, String date, String startTime, String endTime,
                                                   String title, String content,String memo, String name, String address,
                                                   double lat, double lng, double rating) {
        LocationDataDto locationData = new LocationDataDto();
        locationData.setName(name);
        locationData.setAddress(address);
        locationData.setCoordinates(new CoordinatesDto(lat, lng));
        locationData.setRating(rating);
        locationData.setGoogleMapLink("https://maps.google.com/?q=" + lat + "," + lng);

        ScheduleItemDto schedule = new ScheduleItemDto();
        schedule.setScheduleId(id);
        schedule.setTourId(tourId);
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setTitle(title);
        schedule.setContent(content);
        schedule.setMemo(memo); // 메모 추가
        //schedule.setType("location");
        schedule.setTypes(Arrays.asList("location"));  // 여기 수정
        schedule.setLocationData(locationData);

        return schedule;
    }

    private ScheduleItemDto createTrafficSchedule(String id, Long tourId,  String date, String startTime, String endTime,
                                                  String title, String content,String memo, String departure, String destination,
                                                  int price, String duration, int transfers) {
        TrafficDataDto trafficData = new TrafficDataDto();
        trafficData.setMode("TRANSIT");
        trafficData.setDeparture(departure);
        trafficData.setDestination(destination);
        trafficData.setPrice(price);
        trafficData.setTotalDuration(duration);
        trafficData.setTransfers(transfers);

        ScheduleItemDto schedule = new ScheduleItemDto();
        schedule.setScheduleId(id);
        schedule.setTourId(tourId);
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setTitle(title);
        schedule.setContent(content);
        schedule.setMemo(memo); // 메모 추가
        //schedule.setType("traffic");
        schedule.setTypes(Arrays.asList("traffic"));  // 여기 수정
        schedule.setTrafficData(trafficData);

        return schedule;
    }
}