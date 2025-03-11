FROM alpine:latest

ARG JDK_VERSION=8

RUN apk add --no-cache \
    git \
    curl \
    openjdk${JDK_VERSION}-jdk

WORKDIR /build

COPY build.sh /build/build.sh
RUN chmod +x /build/build.sh

ENTRYPOINT ["/bin/sh", "-c", "/build/build.sh"]