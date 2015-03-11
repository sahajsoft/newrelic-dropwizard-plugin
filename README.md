# newrelic-dropwizard-plugin
A [New Relic (NR)](http://newrelic.com/) plugin for monitoring the health of [Dropwizard](http://dropwizard.io/)
applications over http using the application [Health Check](http://dropwizard.io/manual/core.html#health-checks) exposed 
on the admin port at `/healthcheck`.

*It does not collect Dropwizard `/metrics` data or otherwise provide instrumentation of the JVM or server.*  It provides 
a mechanism to monitor and alert on the health of Dropwizard applications.

For collecting general JVM metrics and application transactions, consider installing 
[New Relic's Java Agent](https://docs.newrelic.com/docs/agents/java-agent/getting-started/new-relic-java). For collecting 
Dropwizard-specific metrics into New Relic, consider using the [metrics-new-relic]
(https://github.com/palominolabs/metrics-new-relic) library.

## Health Check Monitoring

This plugin provides the ability to send NR alerts based on the Dropwizard application's `/healthcheck` response and 
records historical data within NR on which health check(s) failed.  The overall status of the `/healthcheck` response, 
the status code returned, and the states of the individual health checks are recorded.

Since NR does not support any text values or annotation of metric values, it's impossible to capture health check
messages within NR.

Multiple Dropwizard applications can be monitored from the same plugin agent.  The plugin polls all configured Dropwizard 
applications every minute and publishes the appropriate data into NR.  The plugin handles failures to connect to or 
receive a response from a Dropwizard application; only applications that provide a valid 200 status code response are
considered healthy.

## New Relic Dashboard UI

![dashboard ui](http://cafepressdev.github.io/newrelic-dropwizard-plugin/images/nr-dashboard-ui.png)

## Installation

This plugin is NPI-compliant and can be [installed using NPI]
(https://docs.newrelic.com/docs/plugins/plugins-new-relic/installing-plugins/installing-npi-compatible-plugin).

`npi install com.cafepress.newrelic.plugin.dropwizard`

Additionally, New Relic has [documentation for doing it the Chef or Puppet way]
(https://docs.newrelic.com/docs/plugins/plugins-new-relic/installing-plugins/plugin-installation-chef-and-puppet).

## Implementation Fine Print

The New Relic Platform does not natively support boolean metric values and this plugin represents boolean values using 
0 (false) and 1 (true).  Conceptually, [this is somewhat problematic]
(https://discuss.newrelic.com/t/how-to-report-yes-no-boolean-values-via-a-custom-plugin/4990/5) but, in practice, it 
mostly works.  Because the boolean values are treated as numbers and subject to aggregation, the NR dashboard UI can 
occasionally report unexpected "boolean" values that are fractional.  We have not seen this lead to false alerting but 
it can be confusing if you're not aware of it.

Additionally, although NR appears to be in the the [process of revamping their alerting system]
(https://docs.newrelic.com/docs/alerts/new-relic-alerts-beta/getting-started/alerting-new-relic), NR's 
current support for alerting on Plugin data is limited.  Specifically, it only supports sending notifications when the 
value of some metric exceeds a specified threshold.  Alerting on values of 0 (or on the absence of data) is not possible.

The approach this plug takes is to publish an "unhealthiness" metric as a boolean value into NR and to then alert when 
the threshold of the "unhealthiness" metric is greater than some very small value (eg 0.001).  In practice, this 
delivers what you want: NR sends an alert whenever the application is unhealthy -- as with most NR alerts, there's a
several minute lag after the application recovers before NR recovers to prevent excessive alerting in the case of a
flagging metric.

In addition to the overall "unhealthiness" metric, the plugin publishes individual "healthiness" metrics and a http status 
code metric for visualization within the UI.

The final caveat (which applies to all NR plugins) is that NR doesn't allow alerting on the *absence* of data so if, for 
example, the server that runs the plugin agent fails, you wouldn't receive an alert if one of your Dropwizard applications 
became unhealthy nor would you receive an alert that the plugin was no longer sending data!  If this were to happen, 
it would be visually obvious within the UI that there is no data but there's no automated way that I've discovered to 
force NR to generate an alert when that happens.
