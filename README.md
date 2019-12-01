# cc-producer-consumer

Application contains 3 modules:
- cc-commons
- cc-producer-service
- cc-consumer-service

**cc-commons** - shared library (dto, kafka and redis pojo)

**cc-producer-service** - service runs web server and posts user input requests to the kafka

**cc-consumer-service** - service listens kafka and posts incoming messages to the redis's list    


## Build 

#### Prerequisites
To build applications you have to install *JDK 11* and *Maven* 

#### Build & test
`mvn clean install`
   

## Run
 
#### Prerequisites
To create docker base environment, run kafka with

`git pull git@github.com:wurstmeister/kafka-docker.git`

And start kafka & zookeeper with

`docker-compose -f kafka-docker\docker-compose-single-broker.yml up -d` 

Start redis with

`docker run -d -p 6379:6379 redis:latest`


#### Run producer

`java -Dproducer.config.file=cc-producer-service\config\producer.yml.example -jar cc-producer-service\target\producer.jar`

#### Run consumer

`java -Dconsumer.config.file=cc-consumer-service\config\consumer.yml.example -jar cc-consumer-service\target\consumer.jar`

#### Send message to producer

`curl -X POST -H "Content-Type: application/json" -d '{"firstName":"Steve","lastName":"Jobs"}' http://localhost:8080/user/input`

#### Read messages from redis

First of all we need to get into redis container with following command 

`docker exec -it <CONTAINER_ID> redis-cli` 

after that, we can pop last message from the list

`lpop "UserInput:cube-producer"`