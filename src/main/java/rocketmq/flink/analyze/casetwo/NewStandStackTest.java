package rocketmq.flink.analyze.casetwo;

import com.google.gson.Gson;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import rocketmq.flink.CommonUtils;
import rocketmq.flink.RocketMQConfig;
import rocketmq.flink.RocketMQSink;
import rocketmq.flink.RocketMQSource;
import rocketmq.flink.analyze.StandStackBean;
import rocketmq.flink.analyze.TupleKeyValueSerializationSchema;
import rocketmq.flink.common.selector.DefaultTopicSelector;
import rocketmq.flink.redis.CustomRedisWrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class NewStandStackTest {

    private static BufferedImage image;

    public static void main(String[] args) {

        StreamExecutionEnvironment evn = StreamExecutionEnvironment.getExecutionEnvironment();

        try {
            image = ImageIO.read(new FileInputStream("src/main/resources/birdview.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties consumerProps = new Properties();
        consumerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_GROUP, "stand_c_2");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_TOPIC, "stand_stack_source");

        //读取rocketmq中的数据
        SingleOutputStreamOperator<Tuple2<String, String>> dataStream = evn.addSource(new RocketMQSource<>(new JsonDeserializationSchema(), consumerProps))
                .map(new MapFunction<String, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> map(String value) throws Exception {
                        StandStackBean bean = new Gson().fromJson(value, StandStackBean.class);
                        int centerX = (bean.getStack().get(0).getLocation().getX1() + bean.getStack().get(0).getLocation().getX2()) / 2;
                        int centerY = (bean.getStack().get(0).getLocation().getY1() + bean.getStack().get(0).getLocation().getY2()) / 2;

                        bean.getStack().get(0).setBetting_box(CommonUtils.positionToBoxNum(image, centerX, centerY));

                        System.out.println("centerX==>"+centerX+"  centerY===>"+centerY+"  bettingBox===> "+bean.getStack().get(0));

                        return new Tuple2<>(bean.getStandKey(), new Gson().toJson(bean));
                    }
                });

        //往redis里面写入
        //实例化Flink和Redis关联类FlinkJedisPoolConfig，设置Redis端口
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("10.5.2.97").setPort(6379).build();
        dataStream.addSink(new RedisSink<Tuple2<String,String>>(conf,new CustomRedisWrapper()));


         //往rocketmq中写入
        Properties producerProps = new Properties();
        producerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        int msgDelayLevel = RocketMQConfig.MSG_DELAY_LEVEL05;
        producerProps.setProperty(RocketMQConfig.MSG_DELAY_LEVEL, String.valueOf(msgDelayLevel));


        dataStream.addSink(new RocketMQSink<Tuple2<String, String>>(new TupleKeyValueSerializationSchema(),new DefaultTopicSelector<>("json-sink"),producerProps));


        try {
            evn.execute("testStandStack");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
