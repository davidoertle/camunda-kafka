package com.sanitas.microservices.camunda.kafka.microservice;

import com.sanitas.microservices.camunda.kafka.microservice.Config.KafkaConfig;
import com.sanitas.microservices.camunda.kafka.microservice.KafkaConsumer.Consumer;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableProcessApplication
public class ProcessApplication {
	
	@Autowired
	private  RuntimeService runtimeService;

    @Autowired
    private KafkaConfig kafkaConfig;
	
	public static void main(String... args) {
		SpringApplication.run(ProcessApplication.class, args);
	}


	@EventListener
	private void processPostDeploy(PostDeployEvent event) {
        runtimeService.startProcessInstanceByKey("loanApproval");
		Consumer kafkaConsumer = new Consumer(kafkaConfig);
		kafkaConsumer.runConsumer(runtimeService, kafkaConfig.getTopic());

		//runtimeService.startProcessInstanceByKey("loanApproval");

	}



/*
	@PostDeploy
	public void startProcessInstance(ProcessEngine processEngine) {

		// start a new instance of our process
	runtimeService.startProcessInstanceByKey("loanApproval");
		System.out.println("---------------------hllo");

	}
	*/

}