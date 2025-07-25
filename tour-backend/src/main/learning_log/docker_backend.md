# Docker 배포 시작
1. 삭제
- window 키 + 프로그램 추가 / 제거
- docker 제거
- chrome에서 docker 검색
- https://www.docker.com에서
- download for windows AMD64 버전으로 설치
- 다운로드 받는 동안에
- https://github.com/maybeags/web_devleopment_deploy_backend.git
- 여기 들어가서 git clone 말고 .zip파일을 받습니다.
- .zip 압축 풀기
- intellij에서 실행
- window + intellij
- C드라이브 -> 여러분영어 이름에
    - car_database 폴더 생성
    - backend 폴더 생성
    - 프로젝트의 src 폴더 있는 부분 전부 복사해서
    - 이상의 위치에 전부 붙여넣기
    - application.properties 들어가서 첫 번 째 줄
      3310 -> 3306
    - heidiSQL을 켜신 다음에 cardb 데이터베이스 이름을
      cardb_test로 교체하겠습니다 충돌 방지를 위해서.
    - window + docker 검색
    - docker desktop을 켜시고, intellij 상에서 터미널을 켭니다.
    - docker --version
```bash
PS C:\ahngeunsu\car_database\backend> docker --version
Docker version 28.3.2, build 578ccf6
```
- 이상은 docker가 켜져있을 때 됩니다. 혹시 안되시는 분은 intellij를 껐다 키세요, docker 적용 전에
  켠 거라서 오류 발생한겁니다.


2. MariaDB 데이터베이스용 컨테이너 생성 과정
- cardb를 생성(application.properties에 있는 이름을 기준으로)
- 명령어
    - docker pull mariadb:latest
- 확인 명령어
    - docker image ls
- 컨테이너 실행 명령어
- docker run --name cardb -e MYSQL_ROOT_PASSWORD=1234 -e MYSQL_DATABASE=cardb mariadb
- 현재까지의 범위가 뭐냐면 DB와 관련된 컨테이너 실행 및 실행한 겁니다
- run -> 실행 명령어
- 이상까지가 DB의 실행과 관련이 있습니다.
3. SpringBoot 애플리케이션을 위한 컨테이너를 만들겁니다. 즉 여러분들이 배포할 때도 db 배포가
   선행적으로 이루어지고 나서 백엔드 배포를 해야할겁니다.

application.properties에서 저희의 설정을 확인할 수 있습니다.

- 우측에 gradle 아이콘(코끼리 클릭)
- Tasks
- build를 더블클릭
- .gradle / build라는 폴더가 생깁니다.
- backend 폴더 우클릭 -> New -> file -> Dockerfile (확장자 없습니다)
- 저희가 gradle에서 빌드를 더블클릭함으로써 explorer에 build라는 폴더가 생성
  됐습니다 -> build/libs에 .jar라고 하는 파일이 생성됐습니다.
- 저희는 저 .jar 파일을 이용하여 Dockerfile에 적용하는 과정을 거칠 예정입니다.
```Dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
EXPOSE 8080
COPY build/libs/cardatabase-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

1. FROM : jdk 버전 의미 / .jar 파일을 빌드할 때 이용한 것과 동일한 버전을 사용해야 함. 저희는 springboot
   때부터 17을 이용했습니다.
2. VOLUME : 도커 컨테이너에서 생성하고 이용하는 영구 데이터에 이용됨(사실 /tmp는 temp의 약어입니다)
3. EXPOSE : 컨테이너 외부에서 열어놔야 하는 포트 넘버(스프링부트라서 기본 포트인 8080을 사용했습니다)
4. COPY : JAR 파일을 컨테이너의 파일 시스템에 복사하고 이름을 app.jar로 바꾼다는 의미.
    - 즉 도커에서 실행할 때는 app.jar를 실행하게 되겠네요
5. ENTRYPOINT : 도커 컨테이너가 실행하는 명령 라인 argument를 의미합니다.

이제 도커를 빌드하는 과정을 거칠겁니다.
docker build -t carbackend .

docker build : Docker 이미지를 빌드하는 명령어
-t carbackend : 빌드된 이미지에 carbackend라는 이름을 명시합니다.
(공백) . : 현재 디렉토리를 빌드 컨텍스트로 지정합니다. Docker는 이 경로에서 Dockerfile을 찾게됩니다.
그래서 루트 프로젝트 폴더에 Dockerfile을 생성합니다.

그 다음 체크를 하기 위한 명령어
docker image ls

```
REPOSITORY   TAG       IMAGE ID       CREATED          SIZE
carbackend   latest    eecd1783fc66   55 seconds ago   616MB
mariadb      latest    2bcbaec92bd9   5 weeks ago      455MB
```
이상의 결과값이 나왔다면 springboot backend가 배포가 되었다는 의미입니다.
이제 스프링부트 컨테이너를 실행하고, 그 후에 MariaDB와 연결하는 명령어를 입력할겁니다.

이후 application.properties로 이동합니다.

오류 발생시
docker stop carapp
docker rm carapp
docker start cardb
docker build -t carbackend .    
docker run -d -p 8080:8080 --name carapp --link cardb:mariadb carbackend

test 과정에서 오류나서
.\gradlew.bat build -x test

docker build -t carbackend .


# 고해성사
제 잘못입니다.
같이 처음부터 하겠습니다...완전 처음은 아니고

.jar를 삭제하는 것부터 시작하겠습니다.
좌측 explorer 내에서
build 폴더 내에 libs 내에 .jar 파일 두 개를 삭제합니다.
tmp 폴더를 째로 삭제합니다.
application.properties로 이동해서
```json
#spring.datasource.url=jdbc:mariadb://localhost:3306/cardb
spring.datasource.url=jdbc:mariadb://mariadb:3306/cardb
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.data.rest.basePath=/api
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

그래서 재빌드를 실행해봤을 때 오류가 발생한다면 정상입니다.
근데 오류 로그를 확인했을 때 다른 부분은 문제가 없는데
test 단계에서 작성한 부분이 오류가 발생합니다.

그래서 test를 수행하지 않고 빌드를 하는 명령어
.\gradlew.bat build -x test

이상의 명령어를 통해 .jar 파일이 만들어졌기 때문에
이를 기준으로 빌드합니다
docker build -t carbackend .

docker image ls
```
PS C:\ahngeunsu\car_database\backend> docker image ls
REPOSITORY   TAG       IMAGE ID       CREATED         SIZE
carbackend   latest    7fd703155a37   6 minutes ago   616MB
mariadb      latest    2bcbaec92bd9   5 weeks ago     455MB
PS C:\ahngeunsu\car_database\backend>
```
기존 앱의 컨테이너 중지 및 삭제하겠습니다
docker stop carapp
docker rm carapp

1. 중지된 DB 컨테이너 실행
   docker start cardb
2. 실패한 앱 컨테이너 삭제
   docker rm carapp
3. 앱 컨테이너 재실행
   docker run -d -p 8080:8080 --name carapp --link cardb:mariadb carbackend
4. 최종 확인
   docker ps
5. http://localhost:8080
6. postman agent에서 POST localhost:8080/login
```json
{
  "username": "user",
  "password": "user"
}
```
으로 200OK가 뜨는가까지가 기본 전제입니다.

Docker 백엔드 배포를 위한 순서
저희는 MariaDB를 사용하기 때문에 Docker에서 관련 자료를 pull 해왔습니다.
docker pull mariadb:latest
cardb를 MariaDB에서 실행시키는 과정을 거쳤습니다.
docker run --name -e cardb MYSQL_PASSWORD=1234 -e MYSQL_DATABASE=cardb mariadb
이상의 명령어의 맨마지막 mariadb는 application.properties의 localhost 대신으로 쓰입니다.
그래서 application.properties를 수정해야했습니다. 3번 라인 참조
그 다음에 코끼리 눌러서 빌드를 시켜 .jar 파일을 생성했습니다.
-> 근데 build failed가 발생한다면
test를 수행하지 않는 리눅스 명령어를 통해 실행했습니다.
.\gradlew.bat -x test -> 이걸로 빌드했습니다 클릭클릭이 아니라.
두 가지 중에 하나라도 build success가 떴다면
루트 프로젝트 폴더에 new -> file -> Dockerfile을 생성하고
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
EXPOSE 8080
COPY build/libs/cardatabase-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
이렇게 적었습니다.

그리고 나서 전체 도커 파일이 있는 이미지를 빌드합니다. 그 명령어는
docker build -t carbackend .
이었습니다.
그렇게 했을 때
docker image ls를 통해서 저희 프로젝트를 기준으로
carbackend와
mariadb가 둘 다 실행된다면

이제 MariaDB와 springboot 프로젝트를 연결합니다.
docker run -p 8080:8080 --name carapp --link cardb:mariadb -d carbackend
그렇게 하고나서
docker logs carapp 명령어를 실행했을 때
오류 로그없이 나온다면, 백엔드 실행이 가능합니다.
혹은 docker desktop에서 확인 가능
그러면 이제
http://localhost:8080을 통해서 접속했을 때
저희는 authentication 설정을 걸어놨기 때문에 error 화면이 뜬다면 정상실행이 된겁니다.

SOOP 스트리머
알매니저
알집
알툴즈 업데이트
판서펜

삭제 처리 부탁드립니다.

금일 컴퓨터 끄지 말고 다시시작으로 하겠습니다 !!