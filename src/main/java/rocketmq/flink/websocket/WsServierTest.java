package rocketmq.flink.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.client.WebSocketClient;

public class WsServierTest {

    public static void main(String[] args) {

        WebSocketImpl.DEBUG = false;
        int port = 18887; // 端口
        WsServer s = new WsServer(port);
        s.start();


    }

}
