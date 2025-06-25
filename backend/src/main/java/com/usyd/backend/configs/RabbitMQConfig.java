package com.usyd.backend.configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_QUEUE = "emailQueue";
    public static final String EMAIL_EXCHANGE = "emailExchange";
    public static final String EMAIL_ROUTING_KEY = "email.send";
    public static final String EMAIL_DEAD_LETTER_QUEUE = "emailDeadLetterQueue";
    public static final String EMAIL_DEAD_LETTER_EXCHANGE = "emailDeadLetterExchange";
    public static final String EMAIL_DEAD_LETTER_ROUTING_KEY = "email.deadLetter";

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", EMAIL_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue emailDeadLetterQueue() {
        return new Queue(EMAIL_DEAD_LETTER_QUEUE);
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public DirectExchange emailDeadLetterExchange() {
        return new DirectExchange(EMAIL_DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding emailDeadLetterBinding() {
        return BindingBuilder.bind(emailDeadLetterQueue()).to(emailDeadLetterExchange()).with(EMAIL_DEAD_LETTER_ROUTING_KEY);
    }
}
