FROM openjdk:8u131-jre-alpine
LABEL maintainer="Laurent Wouters <lwouters@cenotelie.fr>" vendor="Association Cénotélie" description="xOWL Triple Store Server"
EXPOSE 3443
ENTRYPOINT ["sh", "do-run.sh", "/xowl-data"]
VOLUME ["/xowl-data"]
COPY xowl-server.jar xowl-server.jar
COPY xowl-server.manifest xowl-server.manifest
COPY xowl-server.ini /xowl-data/xowl-server.ini
COPY do-run.sh do-run.sh