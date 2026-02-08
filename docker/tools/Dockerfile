FROM golang:1.22-alpine
WORKDIR /src
RUN apk add --no-cache git curl
RUN git clone https://github.com/hpxro7/wwiseutil.git /src/wwiseutil
WORKDIR /src/wwiseutil
RUN go build -o /opt/tools/wwiseutil ./cmd/wwiseutil
CMD ["/bin/sh", "-c", "echo 'wwiseutil installed to /opt/tools/wwiseutil' && ls -l /opt/tools"]
