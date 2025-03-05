FROM amazoncorretto:21-alpine
LABEL maintainer="Kumar Sambhav sambhav26k@gmail.com" \
      description="Pill Scheduler Application"
COPY build/libs/pillscheduler-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]