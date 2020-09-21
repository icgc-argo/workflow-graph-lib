# workflow-graph-lib
Common libraries and utilities for Workflow-Graph.

## Technologies
 - Java 11
 - GraalVM

## Use

### Exceptions

All exceptions inherit from 4 possible abstract base classes that inform how we are to handle any inheriting exception
within our pipeline stream context. This forms a matrix of error states that can be expressed as a boolean vector (voluntary t/f, retryable t/f) .
By inheriting from these 4 possible states we can be specific as to where the error has occurred within our code while not having to reason about 
the handling logic within the pipeline context on a per exception basic as that is baked into the hierarchy and so our pipelines need only
handle these four base classes while our components extend and specify as needed.

`RequeueableException` - exceptions that are involuntary and retryable

`DeadLetterQueueableException` - exceptions that are involuntary and not retryable

`NotAcknowledgeableException` - exceptions that are voluntary and retryable (ie. filter fail)

`CommittableException` - exceptions that are voluntary and not retryable (ie. activation function returns false)

Below is an example of where the `CommittableException` falls in this matrix:

```
 ______________________________________________
 |                | Voluntary  | Involuntary  |
 |============================================|
 | Retryable      |            |              |
 |--------------------------------------------|
 | Non-Retryable  |     X      |              |
 |____________________________________________|
```
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
