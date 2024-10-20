# Bazowy obraz z Javy
FROM openjdk:17-jdk-alpine

# Ustawienie katalogu roboczego
WORKDIR /app

# Skopiowanie plików JAR do obrazu
COPY target/projektZajavka2.jar app.jar

# Otwarcie portu aplikacji
EXPOSE 8085

# Komenda uruchamiająca aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]
