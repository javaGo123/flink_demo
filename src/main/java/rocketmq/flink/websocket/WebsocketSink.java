package rocketmq.flink.websocket;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.java_websocket.WebSocketImpl;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class WebsocketSink extends RichSinkFunction<Map>  {


    private WsServer s;

    public WebsocketSink(){
        if (s == null) {
            WebSocketImpl.DEBUG = false;
            int port = 18887; // 端口
            s = new WsServer(port);
            s.start();
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        System.out.println("websocket执行了:=====> ");


    }


    @Override
    public void invoke(Map value, Context context) throws Exception {


        System.out.println("invoke");

        System.out.println("输出了 "+  value.get("key").toString().getBytes(StandardCharsets.UTF_8));

        s.sendByteMsg(value.get("kety").toString().getBytes(StandardCharsets.UTF_8));



    }


    @Override
    public void close() throws Exception {
        super.close();
        System.out.println("ws关闭了");
      /*  if (s != null) {
            s.stop();
        }*/

      s.stop();

    }
}
