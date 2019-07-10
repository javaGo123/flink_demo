package rocketmq.flink.analyze;

import akka.protobuf.CodedOutputStream;
import com.sensetime.plutus.commonapis.Commonapis;
import com.sensetime.plutus.table_extract.CasinoTableExtractService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StandStackProduce {

    public static void main(String[] args) {


        DefaultMQProducer producer = new DefaultMQProducer("p_stand");
        producer.setNamesrvAddr("10.5.2.97:9876");
        //producer.setNamesrvAddr("10.5.2.201:9876");

        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 1000; i++) {
            int value = i % 3;
            Message msg=null;

            msg = new Message("stand_stack_source", "", i+"", buildPBMsg(i+"",1483,288,1609,336));

            try {
                producer.send(msg);
               // Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public static byte[] buildPBMsg(String cashValue, double x, double y, double x1, double y1){

        CasinoTableExtractService.TableStatus.Builder tableStatusBuilder = CasinoTableExtractService.TableStatus.newBuilder();
        List<CasinoTableExtractService.ObjectInfo> objectInfosList = new ArrayList<>();

        CasinoTableExtractService.ObjectInfo.Builder objectInfoBuilder = CasinoTableExtractService.ObjectInfo.newBuilder();
        // 设置ObjectInfo属性
       setObjectInfo(objectInfoBuilder,x,y,x1,y1);

        objectInfosList.add(objectInfoBuilder.build());

        // 设置赌桌物体信息
        tableStatusBuilder.addAllObjectInfos(objectInfosList);

        CasinoTableExtractService.TableStatus build = tableStatusBuilder.build();
        //return build.toString().getBytes();
        return build.toByteArray();
    }


    private static void setObjectInfo(CasinoTableExtractService.ObjectInfo.Builder objectInfoBuilder, double x, double y, double x1, double y1) {
        // 设置object属性
        Commonapis.ObjectAnnotation.Builder objectBuilder = objectInfoBuilder.getObjectBuilder();

        // 设置
        Commonapis.CasinoChipsAnnotation.Builder casinoChipsBuilder = objectBuilder.getCasinoChipsBuilder();
        List<Commonapis.CasinoChipAnnotation> casinoChipAnnotationList = new ArrayList<>();

        //设置筹码类型
        Commonapis.CasinoChipAnnotation.Builder casionChip = Commonapis.CasinoChipAnnotation.newBuilder();
        //设置筹码类型
        casionChip.setChipType("cash");
        casionChip.setChipValue("600");
        Commonapis.CasinoChipAnnotation build = casionChip.build();
        casinoChipAnnotationList.add(build);

        casinoChipsBuilder.addAllCasinoChips(casinoChipAnnotationList);

        // 设置是否是站着的筹码
        casinoChipsBuilder.setIsChipStanding(false);


        objectBuilder.setCasinoChips(casinoChipsBuilder.build());



        //设置坐标
        Commonapis.Location.Builder locationBuilder = objectBuilder.getLocationBuilder();

        com.sensetime.viper.commonapis.Commonapis.BoundingPoly.Builder boundingBuilder = com.sensetime.viper.commonapis.Commonapis.BoundingPoly.newBuilder();
        com.sensetime.viper.commonapis.Commonapis.Vertex.Builder vertex = com.sensetime.viper.commonapis.Commonapis.Vertex.newBuilder();
        vertex.setX((int) x).setY((int) y);
        com.sensetime.viper.commonapis.Commonapis.Vertex.Builder vertex2 = com.sensetime.viper.commonapis.Commonapis.Vertex.newBuilder();
        vertex2.setX((int) x1).setY((int) y1);

        List<com.sensetime.viper.commonapis.Commonapis.Vertex> vertexList=new ArrayList<>();

        vertexList.add(vertex.build());
        vertexList.add(vertex2.build());

        boundingBuilder.addAllVertices(vertexList);

        locationBuilder.setBounding(boundingBuilder.build());

        objectBuilder.setLocation(locationBuilder.build());






      /*  Commonapis.CasinoChipAnnotation.Builder casinoChipAnnotationBuilder = Commonapis.CasinoChipAnnotation.newBuilder();

        //this.setCasinoChipAnnotation(casinoChipAnnotationBuilder);

        Commonapis.CasinoChipAnnotation casinoChipAnnotation = casinoChipAnnotationBuilder.build();
        casinoChipAnnotationList.add(casinoChipAnnotation);*/


    }



}
