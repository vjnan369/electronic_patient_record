FROM ubuntu:16.04

# Install Java.

RUN apt-get update

RUN apt-get install -y wget


RUN wget --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u191-b12/2787e4a523244c269598db4e85c51e0c/jdk-8u191-linux-x64.tar.gz

RUN mkdir /opt/jdk

RUN tar -zxf jdk-8u191-linux-x64.tar.gz -C /opt/jdk

RUN update-alternatives --install /usr/bin/java java /opt/jdk/jdk1.8.0_191/bin/java 100

RUN update-alternatives --install /usr/bin/javac javac /opt/jdk/jdk1.8.0_191/bin/javac 100

# set path
ENV JAVA_HOME /opt/jdk/jdk1.8.0_191

ENV PATH $PATH:$JAVA_HOME/bin

RUN java -version

EXPOSE 8080

WORKDIR ~/patientdata

COPY target/patient-data-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
