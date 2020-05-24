package com.springboot.rabbitmq.queue;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ldb
 * @date 2020/5/24
 * @dsscription 测试简单队列
 */
@SpringBootTest
public class SimpleQueueTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSend(){
        rabbitTemplate.convertAndSend(SimpleQueue.SIMPLEQUEUE,"hello,world");
    }

}
