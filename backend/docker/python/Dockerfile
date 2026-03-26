FROM python:3.10-alpine

RUN adduser -D runner

WORKDIR /app

COPY run.sh /run.sh
RUN chmod +x /run.sh

USER runner

ENTRYPOINT ["/bin/sh", "/run.sh"]