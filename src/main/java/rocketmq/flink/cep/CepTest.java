package rocketmq.flink.cep;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import rocketmq.flink.cep.bean.MonitoringEvent;
import rocketmq.flink.cep.bean.TemperatureEvent;
import rocketmq.flink.cep.bean.TemperatureWarning;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

//https://cloud.tencent.com/developer/article/1368819
//https://blog.51cto.com/1196740/2361712

public class CepTest {

    public static final double TEMPERATURE_THRESHOLD=100;

    public static void main(String[] args) {



        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        ArrayList<TemperatureEvent> sourceDatas = new ArrayList<>();


        for (int i = 0; i < 105; i++) {
            TemperatureEvent temperatureEvent = new TemperatureEvent();
            temperatureEvent.setRackID(new Random(3).nextInt());
            temperatureEvent.setTemperature(i);
            sourceDatas.add(temperatureEvent);
        }


        DataStreamSource<TemperatureEvent> monitoringEventDataStreamSource = env.fromCollection(sourceDatas);


        Pattern<TemperatureEvent, TemperatureEvent> within = Pattern.<TemperatureEvent>begin("First")
                .subtype(TemperatureEvent.class)
                .where(new IterativeCondition<TemperatureEvent>() {
                    @Override
                    public boolean filter(TemperatureEvent temperatureEvent, Context<TemperatureEvent> context) throws Exception {
                        return temperatureEvent.getTemperature() >= TEMPERATURE_THRESHOLD;
                    }
                })
                .next("Second")
                .subtype(TemperatureEvent.class)
                .where(new IterativeCondition<TemperatureEvent>() {
                    @Override
                    public boolean filter(TemperatureEvent temperatureEvent, Context<TemperatureEvent> context) throws Exception {
                        return temperatureEvent.getTemperature() >= TEMPERATURE_THRESHOLD;
                    }
                })
                .within(Time.seconds(10));


        //KeyedStream<MonitoringEvent, Tuple> rackID = monitoringEventDataStreamSource.keyBy("rackID");

        PatternStream<TemperatureEvent> rackID = CEP.pattern(monitoringEventDataStreamSource.keyBy("rackID"), within);




        SingleOutputStreamOperator<TemperatureWarning> select = rackID.select(new PatternSelectFunction<TemperatureEvent, TemperatureWarning>() {
            @Override
            public TemperatureWarning select(Map<String, List<TemperatureEvent>> pattern) throws Exception {
                List<TemperatureEvent> first = pattern.get("First");
              //  TemperatureEvent second = (TemperatureEvent) pattern.get("Second");

                System.out.println("test: "+first.get(0).getTemperature());

                return new TemperatureWarning(
                        first.get(0).getRackID(),
                        5);


            }
        });


        select.print();

        try {
            env.execute("cep");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
