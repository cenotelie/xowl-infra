FROM openjdk:8u131-jre-alpine
LABEL maintainer="Laurent Wouters <lwouters@cenotelie.fr>" vendor="Association Cénotélie" description="xOWL Triple Store Server"

ENTRYPOINT ["sh", "do-run.sh", "/xowl-data"]
VOLUME ["/xowl-data"]

COPY do-run.sh do-run.sh
COPY src/main/resources/org/xowl/infra/server/config/docker.ini /xowl-data/xowl-server.ini
COPY xowl-server.jar xowl-server.jar
COPY xowl-server.manifest xowl-server.manifest