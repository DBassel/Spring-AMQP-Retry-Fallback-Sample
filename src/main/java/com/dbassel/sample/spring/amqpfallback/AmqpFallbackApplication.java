package com.dbassel.sample.spring.amqpfallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class AmqpFallbackApplication {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public static void main(String[] args) {
        SpringApplication.run(AmqpFallbackApplication.class, args);
    }

    @PostConstruct
    void init() {
        rabbitTemplate.convertAndSend("Sample message 1");
        log.info("Sending sample message");
    }

}
