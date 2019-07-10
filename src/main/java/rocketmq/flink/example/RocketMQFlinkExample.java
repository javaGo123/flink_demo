/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rocketmq.flink.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.util.Collector;
import rocketmq.flink.RocketMQConfig;
import rocketmq.flink.RocketMQSink;
import rocketmq.flink.RocketMQSource;
import rocketmq.flink.common.selector.DefaultTopicSelector;
import rocketmq.flink.common.serialization.SimpleKeyValueDeserializationSchema;
import rocketmq.flink.common.serialization.SimpleKeyValueSerializationSchema;


public class RocketMQFlinkExample {
    public static void main(String[] args) {

//        String[] jars = {"/home/flinkDemo-1.0-SNAPSHOT.jar",
//                "/home/flinkDemo-1.0-SNAPSHOT-jar-with-dependencies.jar"};

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        StreamExecutionEnvironment env = StreamExecutionEnvironment
//                .createRemoteEnvironment("10.5.6.11", 6123, "/home/flinkDemo-1.0-SNAPSHOT.jar");

       // env.enableCheckpointing(3000);

        Properties consumerProps = new Properties();
        //consumerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.11:9876");
        consumerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_GROUP, "c0010");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_TOPIC, "stand_stack_source");

        Properties producerProps = new Properties();
        //producerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.11:9876");
        producerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        int msgDelayLevel = RocketMQConfig.MSG_DELAY_LEVEL05;
        producerProps.setProperty(RocketMQConfig.MSG_DELAY_LEVEL, String.valueOf(msgDelayLevel));
        // TimeDelayLevel is not supported for batching
        boolean batchFlag = msgDelayLevel <= 0;



        env.addSource(new RocketMQSource<Map>(new SimpleKeyValueDeserializationSchema("id", "address"), consumerProps))
            .name("rocketmq-source")
            .setParallelism(1)
            .process(new ProcessFunction<Map, Map>() {
                @Override
                public void processElement(Map in, Context ctx, Collector<Map> out) throws Exception {
                    HashMap result = new HashMap();
                    result.put("id", in.get("id")+"处理==id"+new Random(100).nextInt());
                    String[] arr = in.get("address").toString().split("\\s+");
                    result.put("province", arr[arr.length - 1]+"处理=====province"+new Random(200).nextInt());
                    out.collect(result);
                }
            })
            .map(new MapFunction<Map, Map>() {
                @Override
                public Map map(Map value) throws Exception {
                    HashMap map=new HashMap();
                    map.put("id",value.get("id")+"哈哈: "+new Random(10).nextInt());
                    map.put("province",value.get("province")+"嘻嘻: "+new Random(10).nextInt());
                    return map;
                }
            })
            .name("upper-processor")
            .setParallelism(1)
            .addSink(new RocketMQSink<Map>(new SimpleKeyValueSerializationSchema("id", "province"),
                new DefaultTopicSelector<Map>("flink-sink2"), producerProps)
            .withBatchFlushOnCheckpoint(batchFlag))
            .name("rocketmq-sink")
            .setParallelism(1);



        try {
            env.execute("rocketmq-flink-example");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
