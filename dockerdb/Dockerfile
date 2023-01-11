ARG PG_VERSION=12.6-alpine

FROM postgres:${PG_VERSION}

RUN apk update && \
    apk add net-tools

ENV POSTGRES_USER=user
ENV POSTGRES_PASSWORD=pass
ENV POSTGRES_DB=test

EXPOSE 5432

HEALTHCHECK CMD pg_isready -d test -U user