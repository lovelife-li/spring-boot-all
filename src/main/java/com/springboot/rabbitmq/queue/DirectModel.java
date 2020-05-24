package com.springboot.rabbitmq.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author ldb
 * @date 2020/5/24
 * @dsscription 路由模式，队列通过route_key绑定交换机.接受指定route_key的消息。
 */
@Component
public class DirectModel {
    private final static Logger logger = LoggerFactory.getLogger(WorkQueue.class);

    public static final String DIRECT_QUEUE1 = "direct_queue1";
    public static final String INFO = "info";
    public static final String ERROR = "error";
    public static final String ALL = "all";
    public static final String DIRECT_QUEUE2 = "direct_queue2";
    public static final String DIRECT_EXCHANGE = "direct_exchange";
    public static final String DIRECT_EXCHANGE2 = "direct_exchange2";

    @Bean(name = DIRECT_QUEUE1)
    public Queue queue1() {
        logger.info("queue : {}", DIRECT_QUEUE1);
        // 队列持久化
        return new Queue(DIRECT_QUEUE1, true, false, false, null);
    }

    @Bean(name = DIRECT_QUEUE2)
    public Queue queue2() {
        logger.info("queue : {}", DIRECT_QUEUE2);
        // 队列持久化
        return new Queue(DIRECT_QUEUE2, true, false, false, null);
    }

    @Bean(name = "directExchange")
    public DirectExchange fanoutExchange() {
        logger.info("exchange : {}", DIRECT_EXCHANGE);
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }
    @Bean(name = "directExchange2")
    public DirectExchange fanoutExchange2() {
        logger.info("exchange : {}", DIRECT_EXCHANGE2);
        return new DirectExchange(DIRECT_EXCHANGE2, true, false);
    }

    @Bean
    Binding directBinding1(@Qualifier(DIRECT_QUEUE1) Queue queue,
                           @Qualifier("directExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(INFO);
    }

    @Bean
    Binding directBinding2(@Qualifier(DIRECT_QUEUE2) Queue queue,
                           @Qualifier("directExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(ERROR);
    }

    @Bean
    Binding directBinding3(@Qualifier(DIRECT_QUEUE2) Queue queue,
                           @Qualifier("directExchange2") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(ALL);
    }

    @RabbitListener(queues = DIRECT_QUEUE1)
    public void process(Message message) {
        try {
            String msg = new String(message.getBody());
            if (StringUtils.isEmpty(msg)) {
                logger.warn("接收的数据为空");
                return;
            }
            System.out.println(DIRECT_QUEUE1 + ":" + msg);
        } catch (Exception e) {
            logger.warn("处理接收到数据，发生异常：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = DIRECT_QUEUE2)
    public void process2(Message message) {
        try {
            String msg = new String(message.getBody());
            if (StringUtils.isEmpty(msg)) {
                logger.warn("接收的数据为空");
                return;
            }
            System.out.println(DIRECT_QUEUE2 + ":" + msg);
        } catch (Exception e) {
            logger.warn("处理接收到数据，发生异常：{}", e.getMessage());
            e.printStackTrace();
        }
    }
}
