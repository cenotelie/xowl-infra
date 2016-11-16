FROM openjdk:8u92-jre-alpine
MAINTAINER Laurent Wouters <lwouters@xowl.org>
LABEL description "Docker image for the xOWL server."
EXPOSE 3443
ENTRYPOINT ["sh", "do-run.sh", "/xowl-data"]
VOLUME ["/xowl-data"]
COPY xowl-server.jar xowl-server.jar
COPY xowl-server.manifest xowl-server.manifest
COPY xowl-server.ini xowl-server.ini
COPY do-run.sh do-run.sh