#build in docker
FROM maven:3.6.3-jdk-8 as maven_build
#VOLUME "$USER_HOME_DIR/.m2"
COPY . /source
WORKDIR /source/admin
RUN mvn clean install

FROM openjdk:8
COPY --from=maven_build /source/admin/target/admin-0.0.1-SNAPSHOT.jar /usr/local/lib/works-spores-admin.jar
EXPOSE 8082:8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/works-spores-admin.jar"]
