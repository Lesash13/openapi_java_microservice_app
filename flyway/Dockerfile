FROM flyway/flyway:6.3.3-alpine


USER root

VOLUME /tmp

COPY src/main/resources/db/migration/*.sql /flyway/sql/
RUN chmod +x /flyway/flyway
RUN apk add --no-cache openssh

ENTRYPOINT [""]
