FROM golang:1.22-alpine
ENV GOPATH=/go
WORKDIR /go/src/github.com/hpxro7/wwiseutil
RUN apk add --no-cache git curl
RUN git clone https://github.com/hpxro7/wwiseutil.git /go/src/github.com/hpxro7/wwiseutil
RUN GO111MODULE=off go build -o /opt/tools/wwiseutil ./cmd
CMD ["/bin/sh", "-c", "echo 'wwiseutil installed to /opt/tools/wwiseutil' && ls -l /opt/tools"]
