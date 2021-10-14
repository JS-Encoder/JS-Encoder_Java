package com.lzq.web.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/9/2 10:33
 */
@Configuration
public class RabbitmqQueueConfig {
    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("delete_exchange", "x-delayed-message",true, false,args);
    }

    @Bean
    public Queue queue() {
        Queue queue = new Queue("delete_queue", true);
        return queue;
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(delayExchange()).with("delete").noargs();
    }
}
