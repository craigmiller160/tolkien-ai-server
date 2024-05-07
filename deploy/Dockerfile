FROM openjdk:21-slim

RUN mkdir /app
COPY build/tolkien-ai-server-*.jar /app/tolkien-ai-server.jar

WORKDIR /app
RUN jar -xf tolkien-ai-server.jar && \
    rm tolkien-ai-server.jar

ENTRYPOINT ["java", "-cp", "./", "org.springframework.boot.loader.launch.JarLauncher"]