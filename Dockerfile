FROM anapsix/alpine-java
MAINTAINER omarcovelho
WORKDIR /
COPY target/streams-starter-project-1.0-jar-with-dependencies.jar streams-starter-project-1.0-jar-with-dependencies.jar
CMD ["java","-jar","/streams-starter-project-1.0-jar-with-dependencies.jar"]