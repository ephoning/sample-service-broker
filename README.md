sample-service-broker
===========================

Based on a Spring MVC framework app for V2 CloudFoundry service brokers
see: https://github.com/cloudfoundry-community/spring-service-broker

# Overview

A simple service broker that uses a bound MySQL service instance to store the service broker state
and "provisions" instances of a 'foo' service'. The actual foo service instances are nothing but "valid" requests that
can be made to a FooController

The use of the "embedded" foo service instance makes the service broker self-contained enough to allow it to be used
to quickly experiment with service broker registration and making its associated plans publicly accessible as documented
in
http://docs.gopivotal.com/pivotalcf/services/managing-service-brokers.html#register-broker
and
http://docs.gopivotal.com/pivotalcf/services/access-control.html#make-plans-public

"Placeholder" implementations are provided for being able to provision MySQL service instances. Some additional work
needs to be done still to define and use an additional MySQL data source instance with DBA/admin privileges to
really have this function.
Even more work needs to be done to complete the "placeholder" Oracle service instance brokerage functionality...
