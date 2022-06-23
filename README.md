# nifi-flowfile-utility
Simple utilities for working with Apache NiFi FlowFile bundles

1. Let's you fabricate FlowFile bundles/packages via config file for use with testing etc.

2. Todo: Cmd line utility to extract attributes & payloads of flow files based on some simple criteria like an id etc.
   (maybe use JEXL for context evaluation on the attribute map)

### Instructions
#### Build
`mvn clean package` will build a jar with dependencies

#### Run
Print out usage/help message
```
java -jar target/flowfile-utility-1.0-SNAPSHOT-jar-with-dependencies.jar --help
```

Based on your config file (see `reference.json` for an example) a flow file bundle will be built
with the configured attributes and specified payload.

##### Example
This will use `reference.json` and create an output `test.flowfile.pkg` file
```
java -jar target/flowfile-utility-1.0-SNAPSHOT-jar-with-dependencies.jar
```