FROM adoptopenjdk/openjdk11-openj9:latest
ARG JAR_FILE=build/libs/todo-list.jar
COPY ${JAR_FILE} todo-list.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod","-jar","/todo-list.jar"]