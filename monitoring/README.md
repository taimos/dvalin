## monitoring

The monitoring service allows sending statistics to different backends to collect metering data.

### Backends

Currently a logging backend and AWS CloudWatch are supported. To enable the backend put the desired 
library on the classpath. Only one backend can be on the classpath simultaneously.

### Usage

To send metrics manually inject the `MetricSender` interface and call the `sendMetric` method. 
You have to supply some coordinates for the metric and the value itself.

Dvalin also provides AspectJ annotations that send metrics automatically.

* `@ExecutionTime` - method annotation that reports the execution time of the method
