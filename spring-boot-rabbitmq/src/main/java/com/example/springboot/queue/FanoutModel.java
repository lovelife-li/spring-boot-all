package com.example.springboot.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author ldb
 * @date 2020/5/24
 * @dsscription 订阅模式，队列订阅交换机.所有队列收到的消息相同。
 */
@Component
public class FanoutModel {
    private final static Logger logger = LoggerFactory.getLogger(WorkQueue.class);

    public static final String FANOUT_QUEUE1 = "fanout_queue1";
    public static final String FANOUT_QUEUE2 = "fanout_queue2";
    public static final String FANOUT_EXCHANGE = "fanout_exchange";

    @Bean(name = FANOUT_QUEUE1)
    public Queue queue1() {
        logger.info("queue : {}", FANOUT_QUEUE1);
        // 队列持久化
        return new Queue(FANOUT_QUEUE1, true, false, false, null);
    }

    @Bean(name = FANOUT_QUEUE2)
    public Queue queue2() {
        logger.info("queue : {}", FANOUT_QUEUE2);
        // 队列持久化
        return new Queue(FANOUT_QUEUE2, true, false, false, null);
    }

    @Bean(name = "fanoutExchange")
    public FanoutExchange fanoutExchange() {
        logger.info("exchange : {}", FANOUT_EXCHANGE);
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    @Bean
    Binding fanoutBinding1(@Qualifier(FANOUT_QUEUE1) Queue queue,
                           @Qualifier("fanoutExchange") FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    Binding fanoutBinding2(@Qualifier(FANOUT_QUEUE2) Queue queue,
                           @Qualifier("fanoutExchange") FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @RabbitListener(queues = FANOUT_QUEUE1)
    public void process(Message message) {
        try {
            String msg = new String(message.getBody());
            if (StringUtils.isEmpty(msg)) {
                logger.warn("接收的数据为空");
                return;
            }
            System.out.println(FANOUT_QUEUE1 + ":" + msg);
        } catch (Exception e) {
            logger.warn("处理接收到数据，发生异常：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = FANOUT_QUEUE2)
    public void process2(Message message) {
        try {
            String msg = new String(message.getBody());
            if (StringUtils.isEmpty(msg)) {
                logger.warn("接收的数据为空");
                return;
            }
            System.out.println(FANOUT_QUEUE2 + ":" + msg);
        } catch (Exception e) {
            logger.warn("处理接收到数据，发生异常：{}", e.getMessage());
            e.printStackTrace();
        }
    }
}
