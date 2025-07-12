FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 호스트에서 빌드된 fat-jar 복사
# GitHub Action 빌드 이용
COPY build/libs/*.jar app.jar

# 애플리케이션 포트
EXPOSE 8080

# 컨테이너 가동
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
