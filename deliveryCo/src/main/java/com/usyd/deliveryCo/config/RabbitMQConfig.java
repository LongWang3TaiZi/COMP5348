package com.usyd.deliveryCo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RabbitMQConfig {
    // delivery queue
    @Bean
    public Queue deliveryQueue() {
        return new Queue("deliveryCOQueue", true);
    }

    // webhook queue
    @Bean
    public Queue webhookQueue() {
        return new Queue("webhookQueue", true);
    }

    // set trust all
    @PostConstruct
    public void setTrustAll() {
        System.setProperty("spring.amqp.deserialization.trust.all", "true");
    }

    // dead letter queue
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("deadLetterQueue", true);
    }
}