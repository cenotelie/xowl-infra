# xOWL Infrastructure - Server #

This component implements facilities for serving datasets over HTTP.
This component is made separate from store so that applications that only want to embed a data store do not pull additional depedencies.
This component can also be used as a standalone servlet to serve a dataset.

## Dependencies

This component has the following external dependencies.

### JBCrypt

* Sources available at [https://github.com/svenkubiak/jBCrypt](https://github.com/svenkubiak/jBCrypt)
* Licenced under [ISC/BSD](https://github.com/svenkubiak/jBCrypt/blob/master/LICENSE)