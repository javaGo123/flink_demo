package rocketmq.flink.analyze.caseone;

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
import rocketmq.flink.common.selector.DefaultTopicSelector;
import rocketmq.flink.common.serialization.SimpleKeyValueSerializationSchema;
import rocketmq.flink.redis.CustomRedisWrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class StandStackTest {

    private static BufferedImage image;

    public static void main(String[] args) {

        StreamExecutionEnvironment evn = StreamExecutionEnvironment.getExecutionEnvironment();

        //evn.getConfig().registerTypeWithKryoSerializer(StandStackBean.class,StandStackBean.class);

        try {
            image = ImageIO.read(new FileInputStream("src/main/resources/birdview.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties consumerProps = new Properties();
        consumerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_GROUP, "stand_c1");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_TOPIC, "stand_stack_source");


        evn.setParallelism(4);

        SingleOutputStreamOperator<StandStackBean> dataStream = evn.addSource(new RocketMQSource<Map>(new PbDeserializationSchema("k", "v"), consumerProps))
                .map(new MapFunction<Map, StandStackBean>() {
                    @Override
                    public StandStackBean map(Map value) throws Exception {
                        StandStackBean standStackBean = new Gson().fromJson((String) value.get("v"), StandStackBean.class);
                        int centerX = (standStackBean.getStack().get(0).getLocation().getX1() + standStackBean.getStack().get(0).getLocation().getX2()) / 2;
                        int centerY = (standStackBean.getStack().get(0).getLocation().getY1() + standStackBean.getStack().get(0).getLocation().getY2()) / 2;

                        standStackBean.getStack().get(0).setBetting_box(CommonUtils.positionToBoxNum(image, centerX, centerY));
                        standStackBean.setStandKey((String) value.get("k"));
                        System.out.println("来了老弟==>  " + value.get("k").toString() + "  Betting==>" + standStackBean.getStack().get(0).getBetting_box()+" x: "+centerX+" y:"+centerY);
                        return standStackBean;
                    }
                });

        //往redis里面写入
        //实例化Flink和Redis关联类FlinkJedisPoolConfig，设置Redis端口
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("10.5.2.97").setPort(6379).build();
        dataStream.map(new MapFunction<StandStackBean, Tuple2<String,String>>() {
            @Override
            public Tuple2<String, String> map(StandStackBean value) throws Exception {
                return new Tuple2<>(value.getStandKey(),new Gson().toJson(value));
            }
        }).addSink(new RedisSink<Tuple2<String,String>>(conf,new CustomRedisWrapper()));


        //往rocketmq中新建的topic写入数据
        Properties producerProps = new Properties();
        producerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        int msgDelayLevel = RocketMQConfig.MSG_DELAY_LEVEL05;
        producerProps.setProperty(RocketMQConfig.MSG_DELAY_LEVEL, String.valueOf(msgDelayLevel));
        boolean batchFlag = msgDelayLevel <= 0;

        dataStream.map(new MapFunction<StandStackBean, Map>() {
            @Override
            public Map map(StandStackBean value) throws Exception {
                HashMap map=new HashMap();
                map.put("k",value.getStandKey());
                map.put("v",new Gson().toJson(value).toString());
                return map;
            }
        }).addSink(new RocketMQSink<Map>(new SimpleKeyValueSerializationSchema("k", "v"),
                new DefaultTopicSelector<Map>("stand-sink"), producerProps)
                .withBatchFlushOnCheckpoint(batchFlag));


        try {
            //设置job名称
            evn.execute("stand");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
