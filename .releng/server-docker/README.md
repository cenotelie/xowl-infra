# xOWL Server - Docker image #

This is a Docker image for the xOWL server.

The data for the xOWL server are exposed on the volume `/xowl-data`.
The image exposes the TCP port 3443 for HTTP connections.

## Use ##

```
$ docker pull xowl/xowl-server:latest
$ docker run -d -p 3443:3443/tcp --name my-xowl-instance -v /path/to/host/data:/xowl-data xowl-server:latest
```

Replace the `/path/to/host/data` to a path where to store the databases on your system.
With a web-browser, go to [https://localhost:3443/web/](https://localhost:3443/web/).
The default administrator login and password are `admin` and `admin`.