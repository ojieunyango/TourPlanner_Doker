package com.example.tour_backend.util;

import com.example.tour_backend.dto.tour.plan.TravelPlanDto;
import com.example.tour_backend.exception.GlobalExceptionHandler.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        // null 값 직렬화 설정
        objectMapper.getSerializationConfig().getDefaultPropertyInclusion();
        // 알 수 없는 속성 무시
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * TravelPlanDto를 JSON 문자열로 변환
     */
    public static String toJson(TravelPlanDto planData) {
        try {
            if (planData == null) {
                return null;
            }
            return objectMapper.writeValueAsString(planData);
        } catch (Exception e) {
            log.error("Error converting TravelPlanDto to JSON", e);
            throw new JsonProcessingException("JSON 변환 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * TravelPlanDto를 JSON 문자열로 안전하게 변환 (예외 발생 시 빈 JSON 반환)
     */
    public static String toJsonSafe(TravelPlanDto planData) {
        try {
            if (planData == null) {
                return "{}";
            }
            return objectMapper.writeValueAsString(planData);
        } catch (Exception e) {
            log.warn("JSON 변환 실패, 빈 JSON 반환: {}", e.getMessage());
            return "{}"; // 빈 JSON 객체 반환
        }
    }
    
    /**
     * JSON 문자열을 TravelPlanDto로 변환
     */
    public static TravelPlanDto fromJson(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, TravelPlanDto.class);
        } catch (Exception e) {
            log.error("Error converting JSON to TravelPlanDto: {}", json, e);
            throw new JsonProcessingException("JSON 파싱 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * JSON 문자열을 TravelPlanDto로 안전하게 변환 (예외 발생 시 기본값 반환)
     */
    public static TravelPlanDto fromJsonSafe(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return createEmptyPlan();
            }
            return objectMapper.readValue(json, TravelPlanDto.class);
        } catch (Exception e) {
            log.warn("JSON 파싱 실패, 기본 Plan 반환. JSON: {}, Error: {}", json, e.getMessage());
            return createEmptyPlan();
        }
    }
    
    /**
     * JSON 유효성 검사
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 일반적인 객체를 JSON 문자열로 변환
     */
    public static String objectToJson(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error converting object to JSON", e);
            throw new JsonProcessingException("JSON 변환 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * JSON 문자열을 지정된 클래스로 변환
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Error converting JSON to object: {}", json, e);
            throw new JsonProcessingException("JSON 파싱 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 기본값과 함께 JSON을 객체로 변환
     */
    public static <T> T jsonToObjectWithDefault(String json, Class<T> clazz, T defaultValue) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return defaultValue;
            }
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("JSON 파싱 실패, 기본값 반환. JSON: {}, Error: {}", json, e.getMessage());
            return defaultValue;
        }
    }
    
    /**
     * 빈 TravelPlan 생성
     */
    private static TravelPlanDto createEmptyPlan() {
        TravelPlanDto plan = new TravelPlanDto();
        plan.setSchedules(java.util.Collections.emptyList());
        plan.setWeatherData(java.util.Collections.emptyList());
        return plan;
    }
}