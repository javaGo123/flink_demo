package rocketmq.flink.Case;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.executiongraph.restart.RestartStrategy;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.Kafka09TableSink;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.java_websocket.WebSocket;
import rocketmq.flink.RocketMQConfig;
import rocketmq.flink.RocketMQSink;
import rocketmq.flink.RocketMQSource;
import rocketmq.flink.common.selector.DefaultTopicSelector;
import rocketmq.flink.common.serialization.SimpleKeyValueDeserializationSchema;
import rocketmq.flink.common.serialization.SimpleKeyValueSerializationSchema;
import rocketmq.flink.websocket.WebsocketSink;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DiffSinkExample {

    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 每隔3000 ms进行启动一个检查点【设置checkpoint的周期】
        env.enableCheckpointing(3000);

        //如果启用了 checkpointing，但没有配置重启策略，则使用固定间隔 (fixed-delay) 策略
        // 间隔10秒 重启3次
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,Time.seconds(10)));
        //5分钟内若失败了3次则认为该job失败，重试间隔为10s
        //env.setRestartStrategy(RestartStrategies.failureRateRestart(3,Time.of(5,TimeUnit.MINUTES),Time.of(10,TimeUnit.SECONDS)));

        // 设置模式为exactly-once （这是默认值）
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 确保检查点之间有至少500 ms的间隔【checkpoint最小间隔】
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        // 检查点必须在一分钟内完成，或者被丢弃【checkpoint的超时时间】
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // 同一时间只允许进行一个检查点
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // 表示一旦Flink处理程序被cancel后，会保留Checkpoint数据，以便根据实际需要恢复到指定的Checkpoint
        //ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION:表示一旦Flink处理程序被cancel后，会保留Checkpoint数据，以便根据实际需要恢复到指定的Checkpoint
        //ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION: 表示一旦Flink处理程序被cancel后，会删除Checkpoint数据，只有job执行失败的时候才会保存checkpoint
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);



        Properties consumerProps = new Properties();
        consumerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR,"10.5.2.201:9876");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_GROUP,"diff_consumer_group");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_TOPIC,"diff_source");


        Properties producerProps = new Properties();
        producerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.201:9876");
        int msgDelayLevel = RocketMQConfig.MSG_DELAY_LEVEL05;
        producerProps.setProperty(RocketMQConfig.MSG_DELAY_LEVEL, String.valueOf(msgDelayLevel));

        boolean batchFlag = msgDelayLevel <= 0;

        DataStreamSource<Map> source = env.addSource(new RocketMQSource<Map>(new SimpleKeyValueDeserializationSchema("key", "value"), consumerProps)).setParallelism(1);

        //source.keyBy(0).transform()

      //过滤数据，往topic-a中发送数据
   /*   source.filter(new FilterFunction<Map>() {
          @Override
          public boolean filter(Map value) throws Exception {
              if ("a".equals(value.get("key"))) {
                   return true;
              }else{
                  return false;
              }
          }
      }).addSink(new RocketMQSink<Map>(new SimpleKeyValueSerializationSchema("key","value"),new DefaultTopicSelector<Map>("diff-sink-a"),producerProps))
              .setParallelism(1);

        //过滤数据，往topic-b中发送数据
        source.filter(new FilterFunction<Map>() {
            @Override
            public boolean filter(Map value) throws Exception {
                if ("b".equals(value.get("key"))) {
                    return true;
                }else{
                    return false;
                }
            }
        }).addSink(new RocketMQSink<Map>(new SimpleKeyValueSerializationSchema("key","value"),new DefaultTopicSelector<Map>("diff-sink-b"),producerProps))
                .setParallelism(1);*/

     /* source.filter(new FilterFunction<Map>() {
            @Override
            public boolean filter(Map value) throws Exception {
                if ("b".equals(value.get("key"))) {
                    return true;
                }else{
                    return false;
                }
            }
        }).addSink(new WebsocketSink()).setParallelism(1);*/







        //实例化Flink和Redis关联类FlinkJedisPoolConfig，设置Redis端口
        FlinkJedisPoolConfig redisConf = new FlinkJedisPoolConfig.Builder().setHost("10.5.2.97").setPort(6379).build();

        source.filter(new FilterFunction<Map>() {
            @Override
            public boolean filter(Map value) throws Exception {
                if ("b".equals(value.get("key"))) {
                    return true;
                }else{
                    return false;
                }
            }
        }).map(new MapFunction<Map, Tuple2<String,Integer>>() {
            @Override
            public Tuple2<String, Integer> map(Map value) throws Exception {
                return new Tuple2<String, Integer>(value.get("value").toString(),1);
            }
        }).addSink(new RedisSink<Tuple2<String,Integer>>(redisConf,new RedisExampleMapper()));





        try {
            env.execute("diff-sink-example");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //指定Redis key并将flink数据类型映射到Redis数据类型
    public static final class RedisExampleMapper implements RedisMapper<Tuple2<String,Integer>> {
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.HSET, "flink");
        }

        public String getKeyFromData(Tuple2<String, Integer> data) {
            return data.f0;
        }

        public String getValueFromData(Tuple2<String, Integer> data) {
            return data.f1.toString();
        }
    }


}
