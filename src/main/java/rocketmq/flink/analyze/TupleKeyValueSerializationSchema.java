package rocketmq.flink.analyze;

import org.apache.flink.api.java.tuple.Tuple2;
import rocketmq.flink.common.serialization.KeyValueSerializationSchema;

import java.nio.charset.StandardCharsets;

public class TupleKeyValueSerializationSchema implements KeyValueSerializationSchema<Tuple2<String,String>> {

    @Override
    public byte[] serializeKey(Tuple2 tuple) {
        if (tuple == null) {
            return null;
        }
        Object key = tuple.f0;
        return key != null ? key.toString().getBytes(StandardCharsets.UTF_8) : null;
    }

    @Override
    public byte[] serializeValue(Tuple2 tuple) {
        if (tuple == null) {
            return null;
        }
        Object value = tuple.f1;
        return value != null ? value.toString().getBytes(StandardCharsets.UTF_8) : null;
    }

}
