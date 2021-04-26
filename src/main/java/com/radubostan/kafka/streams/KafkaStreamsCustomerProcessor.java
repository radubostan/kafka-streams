package com.radubostan.kafka.streams;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.ibm.gbs.schema.Customer;
import com.ibm.gbs.schema.CustomerBalance;
import com.ibm.gbs.schema.Transaction;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import static java.util.Collections.singletonMap;

/**
 * Class responsible to create Kafka streams from Customer and Balance topics, 
 * create a joined Customer Balance using accountId as a key 
 * and persist it to CustomerBalance topic
 */
@Configuration
public class KafkaStreamsCustomerProcessor {

    @Value("${application.name}")
    private String appName;
	
    @Value("${application.client.id}")
    private String appClientId;
    
    @Value("${schema.registry.url}")
    private String schemaRegistryUrl;

    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    @Value("${customer.topic.name}")
    private String customerTopicName;
    
    @Value("${balance.topic.name}")
    private String balanceTopicName;

    @Value("${customer.balance.topic.name}")
    private String customerBalanceTopicName;

    @Value("${state.store.name}")
    private String stateStoreName;

    @Bean
    @Primary
    public KafkaStreams kafkaStreams(KafkaProperties kafkaProperties) {
    	
    	final Properties streamsConfiguration = new Properties();
		
		// Give the Streams application a unique name.  The name must be unique in the Kafka cluster
		// against which the application is run.
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, appClientId);
		
		// Where to find Kafka broker(s).
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		
		// Where to find the Confluent schema registry instance(s)
		streamsConfiguration.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
		
		// Specify default (de)serializers for record keys and for record values.
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
		streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, stateStoreName);
		streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		// Records should be flushed every 10 seconds. This is less than the default
		// in order to keep this example interactive.
		streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
    	
		// build the topology
		Topology topology = buildTopology(new StreamsBuilder(),singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl));

        final KafkaStreams kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
	   
	    // Always (and unconditionally) clean local state prior to starting the processing topology.	  
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        
        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {        
        	  kafkaStreams.close();
          } catch (final Exception e) {
            // ignored
          }
        }));
        return kafkaStreams;
    }

	public Topology buildTopology(StreamsBuilder builder,final Map<String, String> serdeConfig) {		 	
		
		// declare Avro Serde for all topics 
		final SpecificAvroSerde<Customer> customerSerde = new SpecificAvroSerde<>();
		customerSerde.configure(serdeConfig, false);
		
		final SpecificAvroSerde<Transaction> transactionSerde = new SpecificAvroSerde<>();
		transactionSerde.configure(serdeConfig, false);
		
		final SpecificAvroSerde<CustomerBalance> customerBalanceSerde = new SpecificAvroSerde<>();
		customerBalanceSerde.configure(serdeConfig, false);
		
		// read the customer stream
		final KStream<String, Customer> customersStream = builder.stream(customerTopicName, Consumed.with(Serdes.String(), customerSerde));					
		customersStream.print(Printed.<String, Customer>toSysOut().withLabel("Customers Stream"));
		
		// read the transactions streams
		final KStream<String, Transaction> transactionsStream = builder.stream(balanceTopicName, Consumed.with(Serdes.String(), transactionSerde));	
		transactionsStream.print(Printed.<String, Transaction>toSysOut().withLabel("Transactions Stream"));		
		
		// create a new customer stream with accountId as key
		final KStream<String, Customer> customersStreamByAccountId = customersStream.map((key, value) -> KeyValue.pair(value.getAccountId(),value));  		
		customersStreamByAccountId.print(Printed.<String, Customer>toSysOut().withLabel("CustomersByAccountId Stream"));
		
		// create a new transaction stream with accountId as key
		// TODO needs investigation, this repartitioning should be avoided - was added because the entries created with the Confluent rest-proxy API don't serialize the key (accountId) as String   
		final KStream<String, Transaction> transactionsStreamByAccountId = transactionsStream.map((key, value) -> KeyValue.pair(value.getAccountId(),value));  
		transactionsStreamByAccountId.print(Printed.<String, Transaction>toSysOut().withLabel("TransactionsByAccountId Stream"));
	    
		// join the customersStreamByAccountId and transactionsStreamByAccountId to create customerBalancesStream
	    final KStream<String, CustomerBalance> customerBalancesStream = 
	    	customersStreamByAccountId.join(transactionsStreamByAccountId,
	        (customer, transaction) -> new CustomerBalance(customer.getAccountId(),customer.getCustomerId(),customer.getPhoneNumber(),transaction.getBalance()),
	        JoinWindows.of(Duration.ofMinutes(5)));
	    
	    customerBalancesStream.print(Printed.<String, CustomerBalance>toSysOut().withLabel("CustomersByAccountId joined with TransactionsByAccountId Stream"));
	    
	    // persist the results in CustomerBalance topic
	    customerBalancesStream.to(customerBalanceTopicName, Produced.with(Serdes.String(), customerBalanceSerde));
	    
	    // read the customer balances from topic
	 	final KStream<String, CustomerBalance> customerBalanceStreamFromTopic = builder.stream(customerBalanceTopicName, Consumed.with(Serdes.String(), customerBalanceSerde));
	 	customerBalanceStreamFromTopic.print(Printed.<String, CustomerBalance>toSysOut().withLabel("CustomerBalance Stream (loaded from topic)"));	
	    
		// finish the topology
		return builder.build();
	 }
}
