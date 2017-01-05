package com.dbassel.sample.spring.amqpfallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.StatefulRetryOperationsInterceptor;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * Created by
 *
 * @author Basel Darvish on 5/1/17.
 */
@Slf4j
@EnableRabbit
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "default_exchange";
    public static final String QUEUE = "default_queue";

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(20);
        factory.setMaxConcurrentConsumers(50);
        factory.setPrefetchCount(50);
        factory.setAdviceChain(interceptor());
        return factory;
    }

    @Bean
    StatefulRetryOperationsInterceptor interceptor() {
        return RetryInterceptorBuilder.stateful()
                .maxAttempts(10)
                .backOffOptions(1000, 2.0, 20 * 1000) // initialInterval, multiplier, maxInterval
                .messageKeyGenerator(message -> {
                    String m = new String(message.getBody(), Charset.defaultCharset());
                    log.info("Message key is '{}'", m);
                    return m;
                })
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue getQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(getQueue()).to(topicExchange()).with(QUEUE);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(EXCHANGE);
        rabbitTemplate.setRoutingKey(QUEUE);
        return rabbitTemplate;
    }

}
