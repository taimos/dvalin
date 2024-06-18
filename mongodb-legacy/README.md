## mongodb-legacy (Deprecated)

The mongodb-legacy library adds support for the MongoDB document store. By adding the dependency you get the 
full support to interact with MongoDB databases including an in-memory database for tests.

This library uses the mongodb-driver-legacy mongo driver for java.

### Connection properties

The following settings are possible:

* `mongodb.type` - {fake|real} connect to real MongoDB database or in-memory version using `Mongo Java Server`
* `mongodb.name` - the name of the database to use for data storage
* `mongobee.enabled` - {true|false} use mongobee for database migration
* `mongobee.basepackage` - the base package of the Mongobee changesets
* `mongodb.demodata` - {true|false} load demodata on startup from ND-JSON files

For connections to real MongoDB databases, these extra properties can be set:

* `mongodb.host` - the host of the MongoDB instance (default: localhost)
* `mongodb.port` - the port of the MongoDB instance (default: 27017)
* `mongodb.uri` - instead of host and port you can specify the complete connection string
* `mongodb.sockettimeout` - the socket timeout of the connection (default: 10 seconds)
* `mongodb.connecttimeout` - the connection timeout of the connection attempt (default: 10 seconds)

### Access to database

To get access to the database inject the `MongoDBDataAccess<T>` for your entity class into your bean.
You can then call several methods to query and write data.

### Abstract entity and DAO interface

The library provides a general purpose DAO interface (`ICrudDAO`) and an abstract implementation
(`AbstractMongoDAO`) with many helper methods to ease the development of the data layer. For this to 
work your entities have to extend the `AEntity` superclass. The DAOs created have integrated support 
for JodaTime classes. If you want to use polymorphic types in your entities make sure to implement 
`@IMappedSupertype` on the super class. This advises the Jackson mapper to include type information 
into the created JSON for deserialization. 

### Changesets

For database migration purpose the mongobee library is included and is configured as denoted above 
using system properties. See the mongobee documentation on how to implement changesets. 
For Index creation take a look at the `ChangelogUtil` helper class.

### MongoDBInit

To prefill the database with startup data or test data for integration tests put file on your classpath 
into the package `mongodb` and name them using the following pattern: `<CollectionName>.ndjson`
If you set the system property `mongodb.demodata`to `true` dvalin will populate the given collections 
with the objects contained in this new-line delimited files. Just put one JSON object per line. 

### DocumentLinks

Another feature of dvalin's MongoDB support are DocumentLinks. These allow for references between your 
documents. To include a reference in one of your entities just add a field of the generic type 
`DocumentLink` and let your referenced entity implement `AReferenceableEntity`. 
Dvalin will then include a reference to the given document in your JSON which you can resolve 
by injecting the `IDlinkDAO` wherever you want.
