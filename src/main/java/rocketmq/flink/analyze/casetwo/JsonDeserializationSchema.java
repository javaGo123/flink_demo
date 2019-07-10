package rocketmq.flink.analyze.casetwo;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sensetime.plutus.commonapis.Commonapis;
import com.sensetime.plutus.table_extract.CasinoTableExtractService;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import rocketmq.flink.analyze.StandStackBean;
import rocketmq.flink.common.serialization.KeyValueDeserializationSchema;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonDeserializationSchema implements KeyValueDeserializationSchema<String> {


    @Override
    public String deserializeKeyAndValue(byte[] key, byte[] value) {
        StandStackBean standStackBean = deserializePbValue(value);
        standStackBean.setStandKey(key != null ? new String(key, StandardCharsets.UTF_8) : null);
        return new Gson().toJson(standStackBean);
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }


    private StandStackBean deserializePbValue(byte[] value){

        StandStackBean standStackBean = new StandStackBean();
        try {
            CasinoTableExtractService.TableStatus tableStatus = CasinoTableExtractService.TableStatus.parseFrom(value);

            List<StandStackBean.StackBean> stacks = new ArrayList<>();
            // 获取赌桌物体信息，赌桌上所有的物体都被包含在内
            List<CasinoTableExtractService.ObjectInfo> objectInfosList = tableStatus.getObjectInfosList();
            for (CasinoTableExtractService.ObjectInfo objectInfo : objectInfosList) {
                StandStackBean.StackBean stackBean=new StandStackBean.StackBean();

                stackBean.setBetting_box(2);
                stackBean.setAssociate_id(3);
                stackBean.setIn_motion("true");
                stackBean.setTotal_value("0");
                stackBean.setStack_id(8888);



                List<StandStackBean.StackBean.StackArrayBean> stack_array =new ArrayList<>();
                Commonapis.ObjectAnnotation object = objectInfo.getObject();
                // 获取 赌场筹码堆信息
                List<Commonapis.CasinoChipAnnotation> casinoChipsList = object.getCasinoChips().getCasinoChipsList();

                // 获取 标准坐标系之下的物体坐标
                com.sensetime.plutus.commonapis.Commonapis.Location location = object.getLocation();
                com.sensetime.viper.commonapis.Commonapis.BoundingPoly bounding = location.getBounding();

                //坐标位置
                StandStackBean.StackBean.LocationBean locationBean=new StandStackBean.StackBean.LocationBean();
                List<com.sensetime.viper.commonapis.Commonapis.Vertex> verticesList = bounding.getVerticesList();
                for (int i = 0; i < verticesList.size(); i++) {
                    if (i==0){
                        locationBean.setX1(verticesList.get(i).getX());
                        locationBean.setY1(verticesList.get(i).getY());
                    } else if (i == 1) {
                        locationBean.setX2(verticesList.get(i).getX());
                        locationBean.setY2(verticesList.get(i).getY());
                    }
                }
                stackBean.setLocation(locationBean);

                for (Commonapis.CasinoChipAnnotation casinoChipAnnotation : casinoChipsList) {
                    StandStackBean.StackBean.StackArrayBean stackArrayBean=new StandStackBean.StackBean.StackArrayBean();
                    stackArrayBean.setDenomination(StringUtils.isEmpty(casinoChipAnnotation.getChipValue())?0: Integer.parseInt(casinoChipAnnotation.getChipValue()));
                    stackArrayBean.setType(StringUtils.isEmpty(casinoChipAnnotation.getChipType())?"":casinoChipAnnotation.getChipType());
                    stack_array.add(stackArrayBean);
                }
                stackBean.setStack_array(stack_array);
                stacks.add(stackBean);
            }

            standStackBean.setStack(stacks);

            //游戏状态
            standStackBean.setGame_stae("gaming");

            //时间设置 yyyy-MM-dd HH:mm:ss
         /*   Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            standStackBean.setTime_stamp(sdf.format(date));*/

            standStackBean.setTime_stamp("3333");

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }




        return standStackBean;

    }

}
