## cluster

The `cluster` libraries provide tools to form a cluster of services. Currently only Hazelcast is available 
under `cluster-hazelcast` and can be added using maven. It adds hazelcast to the classpath and auto configuration for clusters.
To form a cluster set the system property `hazelcast.cluster` to `true` and implement `ClusterInfoProvider` as a component.
To connect to a cluster implement the same interface and set the property `hazelcast.client` to the name 
of a cluster that the provider can resolve.

If running a cluster you can optionally specify the following properties:

* `hazelcast.port` - the port for Hazelcast to use (default: 5701) 
* `hazelcast.portcount` - the number of ports for Hazelcast to try with auto increment (default: only given port) 

If you create beans of type `QueueConfig`, `TopicConfig` or `MapConfig` they will automatically be added to the created Hazelcast configuration.
Should this not be enough you can create beans implementing `ConfigProvider` and do custom configuration there.
