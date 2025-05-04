FROM eclipse-temurin:21-jdk
ENV SPRING_DATA_MONGODB_URI=${MONGODB_URI}

WORKDIR /app

COPY target/Social_ELib_Project-0.0.1-SNAPSHOT.jar app.jar

# Запускаємо застосунок
ENTRYPOINT ["java", "-jar", "app.jar"]
