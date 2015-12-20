FROM java:8
MAINTAINER Laurent Wouters <lwouters@xowl.org>
LABEL description="Docker image for the xOWL server."
EXPOSE 3400
EXPOSE 3443
ENTRYPOINT ["java", "-jar", "xowl-server.jar", "/xowl-data"]
VOLUME ["/xowl-data"]
COPY xowl-server.jar xowl-server.jar

