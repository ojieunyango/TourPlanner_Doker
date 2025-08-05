## 🌐 프로젝트 배포 과정 정리

### 1. MariaDB Docker로 실행

```bash
# 최신 마리아DB 이미지 다운로드
docker pull mariadb:latest

# MariaDB 컨테이너 실행
docker run -d \
  --name tourdb \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=tourdb \
  mariadb:latest

# 이미지 확인
docker image ls
```

---

### 2. Spring Boot Backend Docker 빌드 및 실행

```bash
# 프로젝트 루트에 Dockerfile 작성 후, 이미지 빌드
docker build -t tourbackend .

# 이미지 확인
docker image ls

# 컨테이너 실행 (포트 매핑 + DB 연결)
docker run -d \
  -p 8443:8443 \
  --name tourapp \
  --link tourdb:mariadb \
  tourbackend
```

---

### 3. Frontend GitHub Pages 배포

```bash
# vite.config.ts에서 base 경로 설정
base: '/[리포지토리명]/',

# package.json에 deploy 스크립트 추가
"scripts": {
  "predeploy": "npm run build",
  "deploy": "gh-pages -d dist"
}

# 배포 실행
npm run deploy
```

---

### 4. Mixed Content 오류 발생

GitHub Pages는 HTTPS를 사용하므로, HTTP로 실행된 백엔드 API에 접근 시 아래 오류 발생:

```
Mixed Content: The page was loaded over HTTPS, but requested an insecure resource...
```

---

### 5. HTTPS 서버 인증서 적용 (Spring Boot)

자체 서명 인증서 (`.p12`)를 생성하고 아래 설정을 추가:

```properties
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=123456
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=selfsigned
```

하지만 여전히 localhost 기반이므로 외부 접속 불가.

---

### 6. Cloudflare Tunnel로 HTTPS 도메인 생성

```bash
# WSL Ubuntu 진입
wsl -d Ubuntu-22.04

# 인증 없는 임시 HTTPS 터널 생성
cloudflared tunnel --url https://localhost:8443 --no-tls-verify
```

> 예시 생성 주소:
> https://motherboard-registration-labels-runtime.trycloudflare.com

---

### 7. Frontend에서 API 주소 변경

```env
VITE_API_BASE_URL=https://motherboard-registration-labels-runtime.trycloudflare.com/api
```

---

### 8. CORS 오류 발생 및 해결

- 인증서 없이 cloudflared를 실행하면 브라우저에서 HTTPS 통신은 가능하지만,
  다른 출처(Cross-Origin)로 요청하므로 **CORS 오류 발생**.
- 백엔드(Spring Boot)에서 CORS 설정을 추가하여 해결.

```java
@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return registry -> registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*");
    }
}
```

---

### ✅ 최종 구조

| 구성 요소     | 설명                                           |
|---------------|------------------------------------------------|
| MariaDB       | Docker에서 `tourdb`로 실행                     |
| Backend       | HTTPS (8443), Docker로 실행, Cloudflare에 연결 |
| Frontend      | GitHub Pages 배포                              |
| Cloudflare    | 인증서 없이 HTTPS 터널 제공 (`--no-tls-verify`) |

---

### 🔧 유용한 명령어 요약

```bash
# 모든 컨테이너 확인
docker ps -a

# 로그 확인
docker logs tourapp

# 컨테이너 삭제
docker rm -f tourapp tourdb

# Ubuntu 진입
wsl -d Ubuntu-22.04
```
