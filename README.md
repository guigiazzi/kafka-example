# kafka-example

Base Kafka spring-boot project, containing a consumer and a producer.

## Kafka local setup

In order to run an instance of Kafka locally, go to the kafka-stack-docker-compose folder, located in the root of the project, and run the following command:

```bash
docker-compose -f zk-single-kafka-single.yml up -d
```

Note: you must have Docker running locally.

## Usage

To start this applicaiton, simply run:

```bash
mvn spring-boot:run
```

This application exposes a REST API that receives a message and publishes it to a local Kafka Topic. To consume this API, simply make a POST request to localhost:8080/kafka/message, with the following body

```json
{"message": "Hello world"}
```