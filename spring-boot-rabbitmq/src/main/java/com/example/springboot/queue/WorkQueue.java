package com.example.springboot.queue;

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
 * @dsscription 工作队列，表示一个队列，多个消费者.MQ默认是用轮询分发的方式发送信息的
 */
@Component
public class WorkQueue {
    private final static Logger logger = LoggerFactory.getLogger(WorkQueue.class);

    public static final String WORKQUEUE = "workqueue";

    @Bean(name = WORKQUEUE)
    public Queue queue() {
        logger.info("queue : {}", WORKQUEUE);
        // 队列持久化
        return new Queue(WORKQUEUE, true, false, false, null);
    }

    @RabbitListener(queues = WORKQUEUE)
    public void process(Message message) {
        try {
            String msg = new String(message.getBody());
            if (StringUtils.isEmpty(msg)) {
                logger.warn("接收的数据为空");
                return;
            }
            System.out.println(WORKQUEUE + ":" + msg);
        } catch (Exception e) {
            logger.warn("处理接收到数据，发生异常：{}", e.getMessage());
            e.printStackTrace();
        }
    }
}
