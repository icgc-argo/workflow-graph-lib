# workflow-graph-lib
Common libraries and utilities for Workflow-Graph.

## Technologies
 - Java 11
 - GraalVM

## Use

### Maven Dependency

#### Release

Add the release repository to your `pom.xml`
```xml
<repository>
    <id>dcc-release</id>
    <name>artifacts.oicr.on.ca-releases</name>
    <url>https://artifacts.oicr.on.ca/artifactory/dcc-release</url>
</repository>
```

Include the depencency
```xml
<dependency>
    <groupId>org.icgc_argo</groupId>
    <artifactId>workflow-graph-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Snapshot
For snapshots, use the available snapshot repository by adding the following to your `pom.xml`
```xml
<repository>
    <id>dcc-snapshot</id>
    <name>artifacts.oicr.on.ca-snapshots</name>
    <url>https://artifacts.oicr.on.ca/artifactory/dcc-snapshot</url>
</repository>
```

## Development

### Test

```bash
mvn clean test
```

### Build and Package

```bash
mvn clean package
```

### Use build in local development
Install your local build to your `~/.m2` directory with the following:

```bash
mvn clean install
```

Then include the new snapshot version as a dependency. 
