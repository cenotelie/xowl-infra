FROM buildpack-deps:18.04-curl
LABEL maintainer="Laurent Wouters <lwouters@cenotelie.fr>" vendor="Association Cénotélie" description="Cénotélie Java Build Environment"

# Install required system packages
RUN apt-get update && apt-get dist-upgrade -y && apt-get install -y --no-install-recommends \
		zip unzip mercurial openjdk-11-jdk maven \
		&& apt-get clean && apt-get autoclean

# Setup the user environment
RUN mkdir "/home/builder" && mkdir "/home/builder/.m2" && mkdir "/home/builder/.gnupg" && \
	echo "allow-loopback-pinentry" >> "/home/builder/.gnupg/gpg-agent.conf"
COPY cenotelie.public "/home/builder/.gnupg/cenotelie.public"
RUN (HOME="/home/builder" gpg --batch --no-tty --import "/home/builder/.gnupg/cenotelie.public")
RUN chmod -R a+rwX "/home/builder"
ENV HOME=/home/builder
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

VOLUME ["/home/builder/.gnupg/S.gpg-agent", "/home/builder/.m2"]
