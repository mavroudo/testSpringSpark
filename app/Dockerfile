FROM ubuntu:20.04

#ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"
#RUN apk add openjdk11
RUN apt-get update && apt-get install -y openjdk-11-jdk maven
ENV PATH=$PATH:${JAVA_HOME}/bin


# Install maven
RUN apk add --no-cache maven

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
#RUN mvn dependency:resolve

# Adding source, compile and package into a fat jar
ADD src /code/src
RUN mkdir "/code/src/main/resources/lib"
RUN mvn clean compile package -f pom.xml -DskipTests
#RUN mvn dependency:copy-dependencies -DoutputDirectory=/code/src/main/resources/lib

RUN wget -O /code/src/main/resources/lib/hadoop-aws-3.2.0.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.2.0/hadoop-aws-3.2.0.jar
RUN wget -O /code/src/main/resources/lib/aws-java-sdk-bundle-1.11.375.jar https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.11.375/aws-java-sdk-bundle-1.11.375.jar

CMD ["java", "-jar", "target/app-0.0.1-SNAPSHOT.jar"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]