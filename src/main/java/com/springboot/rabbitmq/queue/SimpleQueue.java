package com.springboot.rabbitmq.queue;

import com.springboot.rabbitmq.rabbit.config.TradeOrderQueueConfig;
import com.springboot.rabbitmq.rabbit.key.RabbitMqKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author ldb
 * @date 2020/5/24
 * @dsscription 简单队列
 */
@Component
public class SimpleQueue {
    private final static Logger logger = LoggerFactory.getLogger(SimpleQueue.class);

    public static final String SIMPLEQUEUE = "simplequeue";

    @Bean(name = SIMPLEQUEUE)
    public Queue queue() {
        logger.info("queue : {}", SIMPLEQUEUE);
        // 队列持久化
        return new Queue(SIMPLEQUEUE, true, false, false, null);
    }

    @RabbitListener(queues = SIMPLEQUEUE)
    public void process(Message message) {
        try {
            String msg = new String(message.getBody());
            if (StringUtils.isEmpty(msg)) {
                logger.warn("接收的数据为空");
                return;
            }
            System.out.println(SIMPLEQUEUE+":"+msg);
        } catch (Exception e) {
            logger.warn("处理接收到数据，发生异常：{}", e.getMessage());
            e.printStackTrace();
        }
    }
}
