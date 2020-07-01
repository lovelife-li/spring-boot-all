package com.example.springboot.simplequeue;


import com.example.springboot.queue.DirectModel;
import com.example.springboot.queue.FanoutModel;
import com.example.springboot.queue.SimpleQueue;
import com.example.springboot.queue.WorkQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ldb
 * @date 2020/5/24
 * @dsscription
 */
@RestController
public class QueueController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 简单队列发送
     */
    @GetMapping("/simpleQueue")
    public void simpleQueue() {
        rabbitTemplate.convertAndSend(SimpleQueue.SIMPLEQUEUE, "我是简单队列");
    }

    /**
     * 工作队列发送
     */
    @GetMapping("/workQueue")
    public void workQueue() {
        for (int i = 0; i < 20; i++) {
            rabbitTemplate.convertAndSend(WorkQueue.WORKQUEUE, "我是" + i + "消息");
        }
    }

    /**
     * 订阅模式发送
     */
    @GetMapping("/fanout")
    public void fanoutModel() {
        for (int i = 0; i < 5; i++) {
            rabbitTemplate.convertAndSend(FanoutModel.FANOUT_EXCHANGE, "", "我是广播者" + i);
        }
    }

    /**
     * 路由模式发送
     */
    @GetMapping("/direct")
    public void directModel() {
        rabbitTemplate.convertAndSend(DirectModel.DIRECT_EXCHANGE,DirectModel.INFO,"我是info消息");
        rabbitTemplate.convertAndSend(DirectModel.DIRECT_EXCHANGE,DirectModel.ERROR,"我是error消息");
        rabbitTemplate.convertAndSend(DirectModel.DIRECT_EXCHANGE,"all","我是路由交换机1的all消息");
        rabbitTemplate.convertAndSend(DirectModel.DIRECT_EXCHANGE2,DirectModel.ALL,"我是all消息");
        rabbitTemplate.convertAndSend(DirectModel.DIRECT_EXCHANGE2,DirectModel.ERROR,"我是路由交换机2的error消息");
    }

}
