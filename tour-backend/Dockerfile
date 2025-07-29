FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
# SSL 포트 변경 (8443)
EXPOSE 8443
# jar 파일 복사
COPY build/libs/tour-backend-0.0.1-SNAPSHOT.jar app.jar
# SSL 키스토어 파일 복사 (springboot.p12 경로에 맞게 수정)
#COPY src/main/resources/springboot.p12 springboot.p12
ENTRYPOINT ["java", "-jar", "/app.jar"]
