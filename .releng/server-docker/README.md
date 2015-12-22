# xOWL Server - Docker image #

This is a Docker image for the xOWL server.

## Use ##

```
$ docker run -d -P --name my-xowl-instance -v /path/to/host/data:/xowl-data xowl-server:latest
```

The data for the xOWL server are exposed on the volume `/xowl-data`.
The image also exposes 2 TCP ports:

* 3443 for HTTP connections
* 3400 for XSP connections (text-based protocol)