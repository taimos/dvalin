## orchestration

The `orchestration` libraries provide tools for orchestration like service discovery and global configuration.
Currently only etcd is available under `orchestration-etcd` and can be added using maven. 
To use it set the system property `orchestration.etcd.peers` to a comma separated list of peer URIs.
You can then autowire instances of `ServiceDiscovery` and `GlobalConfiguration`.

Other bindings are planned for the future.
