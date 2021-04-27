package com.radubostan.kafka;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ibm.gbs.schema.Customer;
import com.ibm.gbs.schema.CustomerBalance;
import com.ibm.gbs.schema.Transaction;
import com.radubostan.kafka.streams.KafkaStreamsCustomerProcessor;

import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;


@SpringBootTest
class KafkaStreamsApplicationTests {
	
	private static final String SCHEMA_REGISTRY_SCOPE = KafkaStreamsApplicationTests.class.getName();
	private static final String MOCK_SCHEMA_REGISTRY_URL = "mock://" + SCHEMA_REGISTRY_SCOPE;
	
    private TopologyTestDriver topologyTestDriver;
    private TestInputTopic<String, Customer> customerTopic;
    private TestInputTopic<String, Transaction> balanceTopic;
    private TestOutputTopic<String, CustomerBalance> customerBalanceTopic;    
    private Topology topology;
    private final Properties config=new Properties();
    private final SpecificAvroSerde<Customer> customerSerde = new SpecificAvroSerde<>();				
    private final SpecificAvroSerde<Transaction> transactionSerde = new SpecificAvroSerde<>();	
    private final SpecificAvroSerde<CustomerBalance> customerBalanceSerde = new SpecificAvroSerde<>();

    
    @Autowired
    private KafkaStreamsCustomerProcessor kafkaStreamsCustomerProcessor;
    
    @BeforeEach
    void beforeEach()  {    
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "test_app_id");
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "mock:1234");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
		config.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, MOCK_SCHEMA_REGISTRY_URL);
        config.put(StreamsConfig.STATE_DIR_CONFIG,"test");
      
        // Create test driver
    	createTopologyTestDriver();
        // create topics
        createTopics();    
        
    }
    

    @AfterEach
    void afterEach() {
      topologyTestDriver.close();
      MockSchemaRegistry.dropScope(SCHEMA_REGISTRY_SCOPE);
    }
    
    @Test
    public void shouldReturnEmptyCustomerBalanceTopicForEmptyInputs() { 
        // assert if the output topic is empty
        assertTrue(customerBalanceTopic.isEmpty());
    }
    
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
       
    
    @Test
    public void shouldReturnEmptyCustomerBalanceTopicForDifferentAccountId() {
    	        
        // add customer and balance entries 
        Customer customer= new Customer("customerId1","name1","phone1","accountId1");
        customerTopic.pipeInput("customerId1",customer);
        
        Transaction transaction= new Transaction("balanceId1","accountId2",100F);
        balanceTopic.pipeInput("balanceId1",transaction);
        
        // assert if the output topic is non empty
        assertTrue(customerBalanceTopic.isEmpty());    
    }
        
    
    private void createTopologyTestDriver() {
    	 Map<String, String> configMap = singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, MOCK_SCHEMA_REGISTRY_URL);	 
    	 customerSerde.configure(configMap, false);
         transactionSerde.configure(configMap, false);
         customerBalanceSerde.configure(configMap, false);
    	 topology = kafkaStreamsCustomerProcessor.buildTopology(new StreamsBuilder(),configMap);
         topologyTestDriver = new TopologyTestDriver(topology, config);                 
    }
    
    private void createTopics () {
    	 // Define input and output topics to use in tests
        customerTopic = topologyTestDriver.createInputTopic(
        		"Customer",
            	Serdes.String().serializer(),
            	customerSerde.serializer());
        
        balanceTopic = topologyTestDriver.createInputTopic(
                "Balance",
                Serdes.String().serializer(),
                transactionSerde.serializer());
        
        customerBalanceTopic = topologyTestDriver.createOutputTopic(
                "CustomerBalance",
                Serdes.String().deserializer(),
                customerBalanceSerde.deserializer());
    }
}