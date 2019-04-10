FROM openjdk:8-jdk-alpine
COPY target/vm-0.0.1-SNAPSHOT.jar vm.jar
#ENV GOOGLE_APPLICATION_CREDENTIALS=/root/.config/gcloud/wyown-707b1db66bde.json
ENTRYPOINT ["java","-jar","vm.jar"]

# docker build -t abryu082/vm:v1.0.6 .
# docker push abryu082/vm:v1.0.6