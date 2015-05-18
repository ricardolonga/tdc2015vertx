FROM java:8-jre

EXPOSE 8080

ADD target/tdc2015vertx-0.1-fat.jar /

ENTRYPOINT ["java", "-jar", "tdc2015vertx-0.1-fat.jar"]

