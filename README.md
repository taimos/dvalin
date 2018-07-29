[![Build Status](https://travis-ci.org/taimos/dvalin.svg)](https://travis-ci.org/taimos/dvalin)
[![sonarcloud.io](https://sonarcloud.io/api/project_badges/measure?project=de.taimos%3Advalin-parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.taimos%3Advalin-parent)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.taimos/dvalin-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.taimos/dvalin-parent)


# dvalin - Taimos Microservice Framework

Dvalin is a Java micro service framework based on several open-source frameworks to combine the best tools into one quick start suite for fast, reliable and scaling micro services.
The core technology is the Spring framework and dvalin uses our Daemon Framework as the lifecycle management for the service process.

To use dvalin in your project add the maven dependencies as shown below. 
It is recommended to set the dvalin version as property to make sure all modules you use have the same version.

```
<dependency>
    <groupId>de.taimos</groupId>
    <artifactId>dvalin-<MODULENAME></artifactId>
    <version>${dvalin.version}</version>
</dependency>
```

Then add dvalin as a BOM to the maven dependency management to ensure compatibly versions of used third-party libraries.

```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>de.taimos</groupId>
            <artifactId>dvalin-parent</artifactId>
            <version>${dvalin.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```
 
# Parts of dvalin
 
Dvalin provides several independent but combineable libraries serving different purposes around the runtime and 
interaction of micro services. By adding a library as a Maven dependency, it gets activated automatically and 
all the needed Spring components are started.

These libraries are:

* [`daemon`](./daemon) - the core library for lifecycle and basic Spring enhancements
* [`jaxrs`](./jaxrs) - implement JAX-RS based REST services using the Apache CXF framework
* [`jaxrs-jwtauth`](./jaxrs-jwtauth) - JSON Web Token support for the `jaxrs` module
* [`jpa`](./jpa) - connect to SQL databases using the popular Hibernate framework
* [`mongodb`](./mongodb) - connect to MongoDB document store
* [`dynamodb`](./dynamodb) - connect to AWS DynamoDB data storage
* [`cloud`](./cloud) - basic tools to communicate with Cloud providers
* [`cluster`](./cluster) - basic tools to form a cluster
* [`template`](./template) - templating functionality
* [`notification`](./notification) - notification service to send e-mails and use template engines
* [`monitoring`](./monitoring) - monitoring service to report statistics of your service
* [`interconnect`](./interconnect) - communication framework to connect micro services with each other
* [`orchestration`](./orchestration) - orchestration tools like service discovery and global configuration
* [`test`](./test) - utilities for writing tests
* [`i18n`](./i18n) - internationalization and localization support

# Contributing

## How to contribute to dvalin

#### **Did you find a bug?**

* **Ensure the bug was not already reported** by searching on GitHub under [Issues](https://github.com/taimos/dvalin/issues).

* If you're unable to find an open issue addressing the problem, [open a new one](https://github.com/taimos/dvalin/issues/new). Be sure to include a **title and clear description**, as much relevant information as possible, and a **code sample** or an **executable test case** demonstrating the expected behavior that is not occurring.

#### **Did you write a patch that fixes a bug?**

* Open a new GitHub pull request with the patch.

* Ensure the PR description clearly describes the problem and solution. Include the relevant issue number if applicable.

#### **Did you fix whitespace, format code, or make a purely cosmetic patch?**

Changes that are cosmetic in nature and do not add anything substantial to the stability, functionality, or testability will normally not be accepted.

#### **Do you intend to add a new feature or change an existing one?**

* Suggest your change under [Issues](https://github.com/taimos/dvalin/issues).

* Do not open a pull request on GitHub until you have collected positive feedback about the change.

#### **Do you want to contribute to the dvalin documentation?**

* Just file a PR with your recommended changes
