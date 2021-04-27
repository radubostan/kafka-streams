# Kafka Streams

This project is a an examples of KStream processing with Avro schemas

## Requirements

Problem Statement:

- Build Kafka Streams applications based on Spring that ingests data from the Customer Topic, Balance Topic, and produces a CustomerBalance topic with the joined streams.

Customer Topic

```
	{
	    "customerId",
	    "name",
	    "phoneNumber":
	    "accountId",
	}
```

Balance Topic:

```
	{
	    "balanceId",
	    "accountId",
	    "balance",
	
	}
```

CustomerBalance Topic:

```
	{
	    "accountId"
	    "customerId"
	    "phoneNumber"
	    "balance"
	}

```

Sample Input

	Customer Topic: customer { "a", "Robert Hofman", "888-888-8888", "b"}  
	Balance Topic: balance  { "j", "b", 20.23}
	

Expected Output 

	CustomerBalance topic: { "b", "a", "888-888-8888", 2.23}


## Software Installation, Configuration and Data Preparation

The project uses Confluent Platform, which is mainly a data streaming platform consisting of most of the Kafka features plus additional functionality 
(Confluent Control Center is a GUI-based system for managing and monitoring Kafka, Schema Registry, APIs to generate data etc).

First we need to install and launch locally the Confluent services (i.e. Schema Registry, Broker, ZooKeeper, Control Center).
One option is to use Docker for local installation using [Confluent Docker Installation](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html#ce-docker-quickstart) 

The Confluent Platform Docker installation was verified by running:  

    docker-compose ps

    Name                    Command               State                       Ports
    ---------------------------------------------------------------------------------------------------------
    broker            /etc/confluent/docker/run        Up      0.0.0.0:9092->9092/tcp, 0.0.0.0:9101->9101/tcp
    connect           /etc/confluent/docker/run        Up      0.0.0.0:8083->8083/tcp, 9092/tcp
    control-center    /etc/confluent/docker/run        Up      0.0.0.0:9021->9021/tcp
    ksql-datagen      bash -c echo Waiting for K ...   Up
    ksqldb-cli        /bin/sh                          Up
    ksqldb-server     /etc/confluent/docker/run        Up      0.0.0.0:8088->8088/tcp
    rest-proxy        /etc/confluent/docker/run        Up      0.0.0.0:8082->8082/tcp
    schema-registry   /etc/confluent/docker/run        Up      0.0.0.0:8081->8081/tcp
    zookeeper         /etc/confluent/docker/run        Up      0.0.0.0:2181->2181/tcp, 2888/tcp, 3888/tcp

 
Next step is to create the input topics (Customer and Balance), the output topic (Customer Balance) and create Avro schemas for all topics. 

All these tasks were completed using Confluent Control Center:

	http://localhost:9021/clusters	
  
Test data can be created by calling Confluent rest-proxy APIs. Here is an example of the utput for the local run to create a Customer record:
	
	{"offsets":[{"partition":1,"offset":0,"error_code":null,"error":null}],"key_schema_id":4,"value_schema_id":1}

## Solution (Java)
The Java solution was implemented using Spring Boot, Apache Kafka Streams, Confluent, Avro libraries and Maven for dependencies and build management.

This project assumes and requires the following:

* Java 8+
* Maven 3.6.0+
* Confluent Platform
* Docker 19+ (optional - if the installation of Confluent is via Docker)

The application is using an Avro Maven plugin do generate the Avro files based on the provided schemas. 
All Kakfa related configuration (urls, application config id, topic names etc) are stored in application.properties.

The application has a main processor (KafkaStreamsCustomerProcessor), which has the following responsibilities

- Configure Kafka 
- Create Serde instances
- Allows performing continuous computation on the input topics  
- Build the processor topology to perform the computation on the input topics and produce the output topic records 

Processor topology steps:

- Build KStream objects for Customer(customerId key) and Balance (accountId key) from the input topics.
  Here is the code to build KStream from Customer topic: 	

		// read the customer stream
		final KStream<String, Customer> customersStream = builder.stream(customerTopicName, Consumed.with(Serdes.String(), customerSerde));					
		customersStream.print(Printed.<String, Customer>toSysOut().withLabel("Customers Stream"));
		
- Create a CustomerByAccountId KStream from Customer  stream by repartitioning by accountId, which is the key required to join with the transaction stream:
	
		// create a new customer stream with accountId as key
		final KStream<String, Customer> customersStreamByAccountId = customersStream.map((key, value) -> KeyValue.pair(value.getAccountId(),value));  		
		customersStreamByAccountId.print(Printed.<String, Customer>toSysOut().withLabel("CustomersByAccountId Stream")); 
  
- Create a TransactionByAccountId KStream from Transaction stream by repartitioning by accountId, which is the key required to join with the transaction stream:

>Note: This repartitioning should be avoided, Transaction stream can be used directly in joins. TODO: Investigate why Confluent rest-proxy APIs doesn't serialize the keys as String.

- Join CustomerByAccountId KStream with TransactionByAccountId KStream and produce CustomerBalance KStream:
	
		// join the customersStreamByAccountId and transactionsStreamByAccountId to create customerBalancesStream
		final KStream<String, CustomerBalance> customerBalancesStream = 
		customersStreamByAccountId.join(transactionsStreamByAccountId,
		(customer, transaction) -> new CustomerBalance(customer.getAccountId(),customer.getCustomerId(),customer.getPhoneNumber(),transaction.getBalance()),
		JoinWindows.of(Duration.ofMinutes(5))); 
		
- Persist CustomerBalance KStream to Customer Balance topic:

		// persist the results in CustomerBalance topic
	    customerBalancesStream.to(customerBalanceTopicName, Produced.with(Serdes.String(), customerBalanceSerde));
	    
-  Create a KStream from Customer Balance (note: this is only used to illustrate/verify that the Customer Balance topic has the correct data - not used for a a Production code context)    

	 	// read the customer balances from topic
	 	final KStream<String, CustomerBalance> customerBalanceStreamFromTopic = builder.stream(customerBalanceTopicName, Consumed.with(Serdes.String(), customerBalanceSerde));
	 	customerBalanceStreamFromTopic.print(Printed.<String, CustomerBalance>toSysOut().withLabel("CustomerBalance Stream (loaded from topic)"));			

## Testing
	
For unit testing, I used [kafka-streams-test-utils](https://kafka.apache.org/20/documentation/streams/developer-guide/testing.html).

Here is a sample code to verify the data in Customer Balance (output topic)  for a Customer and a Balance entry with the same key (accountId):
	
	    @Test
    	public void shouldReturnNonEmptyCustomerBalanceTopicForCorrelatedInputs() {
    	        
	        // add customer and balance entries 
	        Customer customer= new Customer("customerId1","name1","phone1","accountId1");
	        customerTopic.pipeInput("customerId1",customer);
	        
	        Transaction transaction= new Transaction("balanceId1","accountId1",100F);
	        balanceTopic.pipeInput("balanceId1",transaction);
	        
	        // assert if the output topic is non empty
	        assertFalse(customerBalanceTopic.isEmpty());
	        
	        // verify the customer balance output
	        assertEquals(new CustomerBalance("accountId1","customerId1","phone1",100F),customerBalanceTopic.readValue());        
    	}
	  
>Notes: TODO Integration Testing (not included in this delivery)  
	
## Build and Run the application

You can import the code straight into your preferred IDE or run the sample using the following command (in the root project folder):

		mvn spring-boot:run		

Sample Input (customer and balance input entries):
	
		curl --request POST \
		  --url http://localhost:8082/topics/Customer \
		  --header 'accept: application/vnd.kafka.v2+json' \
		  --header 'content-type: application/vnd.kafka.avro.v2+json' \
		  --data '{
		    "key_schema": "{\"name\":\"key\",\"type\": \"string\"}",
		    "value_schema_id": "1",
		    "records": [
		        {
		            "key" : "customerId1",
		            "value": {
		                "customerId": "customerId1",
		                "name": "name1",
		                "phoneNumber": "phone1",
		                "accountId": "accountId1"
		            }
		        }
		    ]
		}'
		
		curl --request POST \
		  --url http://localhost:8082/topics/Balance \
		  --header 'accept: application/vnd.kafka.v2+json' \
		  --header 'content-type: application/vnd.kafka.avro.v2+json' \
		  --data '{
		    "key_schema": "{\"name\":\"key\",\"type\": \"string\"}",
		    "value_schema_id": "3",
		    "records": [
		        {
		            "key" : "accountId1",
		            "value": {
		                "balanceId": "balanceId",
		                "accountId" : "accountId1",
		                "balance" : 20.23
		            }
		        }
		    ]
		}'
		
Output :

	 	Customers Stream: customerId1, {"customerId": "customerId1", "name": "name1", "phoneNumber": "phone1", "accountId": "accountId1"}
	 	Transactions Stream: accountId1, {"balanceId": "balanceId", "accountId": "accountId1", "balance": 20.23}
	 	CustomersByAccountId Stream: accountId1, {"customerId": "customerId1", "name": "name1", "phoneNumber": "phone1", "accountId": "accountId1"}
	 	TransactionsByAccountId Stream: accountId1, {"balanceId": "balanceId", "accountId": "accountId1", "balance": 20.23}
	 	CustomersByAccountId joined with TransactionsByAccountId Stream: accountId1, {"accountId": "accountId1", "customerId": "customerId1", "phoneNumber": "phone1", "balance": 20.23}
	 	CustomerBalance Stream (loaded from topic): accountId1, {"accountId": "accountId1", "customerId": "customerId1", "phoneNumber": "phone1", "balance": 20.23}
 	
## Notes

This delivery is not a "production-ready" solution. There are items that are not included: error handling, multi-instance Kafka Streams clients support, integration testing etc.
   	
 	
		
	
  		
	