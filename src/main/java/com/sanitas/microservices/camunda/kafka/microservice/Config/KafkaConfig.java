package com.sanitas.microservices.camunda.kafka.microservice.Config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("kafka")
public class KafkaConfig {


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    private String topic;

    // standard getters and setters

}