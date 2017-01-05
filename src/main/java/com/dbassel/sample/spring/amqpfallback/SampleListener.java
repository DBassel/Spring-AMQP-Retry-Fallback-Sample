package com.dbassel.sample.spring.amqpfallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by
 *
 * @author Basel Darvish on 5/1/17.
 */
@Slf4j
@Component
public class SampleListener {

    @RabbitListener(queues = RabbitConfig.QUEUE)
    void onMessage(String message) {
        log.info("handling message {}", message);
        throw new RuntimeException("Some exception when handling the message");
    }
}
