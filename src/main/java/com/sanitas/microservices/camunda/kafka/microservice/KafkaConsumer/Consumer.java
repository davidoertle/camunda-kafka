package com.sanitas.microservices.camunda.kafka.microservice.KafkaConsumer;


import com.sanitas.microservices.camunda.kafka.microservice.Config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.camunda.bpm.engine.RuntimeService;

import java.util.Collections;
import java.util.Properties;

public class Consumer {

    private final static String TOPIC = "test-topic";
    private final static String BOOTSTRAP_SERVERS ="localhost:9092";

    private static KafkaConfig kafkaConfig;

    public Consumer(KafkaConfig config) {
        kafkaConfig = config;
    }


    public void runConsumer(RuntimeService runtimeService, String topic) {
        final org.apache.kafka.clients.consumer.Consumer<Long, String> consumer = createConsumer();
        runtimeService.startProcessInstanceByKey("loanApproval");

        final int giveUp = 1000;   int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
                runtimeService.startProcessInstanceByKey("loanApproval");
            });

            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");
    }

    private static org.apache.kafka.clients.consumer.Consumer<Long, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        // Create the consumer using props.
        final org.apache.kafka.clients.consumer.Consumer<Long, String> consumer =
                new KafkaConsumer<Long, String>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(kafkaConfig.getTopic()));
        return consumer;
    }
}
