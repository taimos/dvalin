## jpa

The `jpa` library adds JPA and Hibernate support including changeset management using liquibase. By setting some 
system properties you get the full support to store and retrieve data from relational database systems. 
Supported databases are currently HSQL, PostgreSQL and MySQL. You have to add the desired jdbc driver to your classpath manually.

The following settings are possible:

* `ds.type` - {MYSQL|POSTGRESQL|HSQL} type of the database
* `ds.package` - root package of your entities (path notation with /)
* `ds.showsql` - {true|false} to log all SQL statements to the logger
* `ds.demodata` - {true|false} to insert data from the file `sql/demodata_${ds.type}.sql` on startup

For MySQL the following extra setting are possible:

* `ds.mysql.host` - the hostname of the database server
* `ds.mysql.port` - the port number of the database server
* `ds.mysql.db` - the name of the database
* `ds.mysql.user` - the user name of the database server
* `ds.mysql.password` - the password of the database server

For PostgreSQL the following extra setting are possible:

* `ds.pgsql.host` - the hostname of the database server
* `ds.pgsql.port` - the port number of the database server
* `ds.pgsql.db` - the name of the database
* `ds.pgsql.user` - the user name of the database server
* `ds.pgsql.password` - the password of the database server

The library also provides a general purpose DAO interface (`IEntityDAO`) and an abstract implementation
(`EntityDAOHibernate`) with many helper methods to ease the development of the data layer. For this to 
work your entities have to implement the `IEntity` interface.

If you use the JodaTime library you can annotate Date members with the `JodaDateTimeType` to activate 
JodaTime support for JPA.

All changesets contained or referenced in the file `liquibase/changelog.xml` are checked and applied 
on startup by the liquibase database migration library.
