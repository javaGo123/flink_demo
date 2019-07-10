package rocketmq.flink.Case;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class DiffProducer {

    public static void main(String[] args) {

        DefaultMQProducer producer = new DefaultMQProducer("p_diff");
        producer.setNamesrvAddr("10.5.2.201:9876");
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 1000; i++) {
            Message msg=null;
            if (i%2==0){
                 msg = new Message("diff_source", "", "a", ("张三" + i).getBytes());
            }else {
                 msg = new Message("diff_source", "", "b", ("李四" + i).getBytes());
            }
            try {
                producer.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("send " + i);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
