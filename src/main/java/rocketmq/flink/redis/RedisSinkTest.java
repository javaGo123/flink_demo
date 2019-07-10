package rocketmq.flink.redis;

import redis.clients.jedis.Jedis;

public class RedisSinkTest {

    public static void main(String[] args) {
        Jedis jedis=new Jedis("10.5.2.97",6379);
        System.out.println("Server is running: " + jedis.ping());
        System.out.println("result:"+jedis.hgetAll("flink"));

    }

}
