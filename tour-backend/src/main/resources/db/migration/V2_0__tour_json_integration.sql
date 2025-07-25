-- Tour 테이블 구조 변경: JSON 통합 구조로 마이그레이션
-- 실행 전 기존 데이터 백업 필수!

-- 1단계: 새로운 컬럼 추가
ALTER TABLE tour 
ADD COLUMN travelers INT NOT NULL DEFAULT 2,
ADD COLUMN budget ENUM('LOW', 'MEDIUM', 'HIGH', 'LUXURY') NOT NULL DEFAULT 'MEDIUM',
ADD COLUMN plan_data LONGTEXT;

-- 2단계: 날짜 컬럼 타입 확인 (LocalDate 호환성)
-- MySQL에서 DATE 타입은 LocalDate와 호환됩니다
-- 별도 변경 불필요

-- 3단계: 기존 데이터를 JSON 형태로 마이그레이션 (예시)
-- 실제 운영 환경에서는 데이터 양에 따라 배치 처리 필요

UPDATE tour t SET plan_data = JSON_OBJECT(
    'schedules', JSON_ARRAY(),
    'weatherData', JSON_ARRAY(),
    'metadata', JSON_OBJECT(
        'version', '1.0',
        'lastUpdated', NOW(),
        'totalDays', DATEDIFF(t.end_date, t.start_date) + 1,
        'estimatedBudget', NULL
    )
) WHERE plan_data IS NULL;

-- 4단계: 인덱스 추가 (성능 최적화)
CREATE INDEX idx_tour_travelers ON tour(travelers);
CREATE INDEX idx_tour_budget ON tour(budget);
CREATE INDEX idx_tour_user_create_date ON tour(user_id, create_date DESC);

-- 5단계: JSON 데이터 검색을 위한 생성된 컬럼 (MySQL 5.7+)
-- ALTER TABLE tour ADD COLUMN schedule_count INT AS (JSON_LENGTH(plan_data, '$.schedules')) STORED;

-- 참고: 기존 테이블들은 데이터 검증 후 수동으로 삭제
-- DROP TABLE IF EXISTS mapentity;
-- DROP TABLE IF EXISTS traffic; 
-- DROP TABLE IF EXISTS weather;
-- DROP TABLE IF EXISTS schedule;