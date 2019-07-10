package rocketmq.flink.analyze;

import java.io.Serializable;
import java.util.List;

public class StandStackBean implements Serializable {

    /**
     * game_stae : gaming
     * time_stamp : yyyyMMddhhmmssSSS
     * stack : [{"betting_box":101,"associate_id":8912,"stack_id":8912,"stack_array":[{"denomination":100,"type":"cash"},{"denomination":200,"type":"cash"},{"denomination":200,"type":"cash"}],"total_value":"500","location":{"x1":320,"y1":178,"x2":289,"y2":129},"in_motion":"true"},{"betting_box":101,"associate_id":8912,"stack_id":8912,"stack_array":[{"denomination":500,"type":"cash"},{"denomination":200,"count":1,"type":"cash"},{"denomination":1000,"type":"cash"},{"denomination":1000,"type":"cash"}],"total_value":"2700","location":{"x1":320,"y1":178,"x2":289,"y2":129},"in_motion":"true"}]
     */

    private String game_stae;
    private String time_stamp;
    private List<StackBean> stack;
    private String standKey;

    public String getStandKey() {
        return standKey;
    }

    public void setStandKey(String standKey) {
        this.standKey = standKey;
    }

    public String getGame_stae() {
        return game_stae;
    }

    public void setGame_stae(String game_stae) {
        this.game_stae = game_stae;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public List<StackBean> getStack() {
        return stack;
    }

    public void setStack(List<StackBean> stack) {
        this.stack = stack;
    }

    public static class StackBean {
        /**
         * betting_box : 101
         * associate_id : 8912
         * stack_id : 8912
         * stack_array : [{"denomination":100,"type":"cash"},{"denomination":200,"type":"cash"},{"denomination":200,"type":"cash"}]
         * total_value : 500
         * location : {"x1":320,"y1":178,"x2":289,"y2":129}
         * in_motion : true
         */

        private int betting_box;
        private int associate_id;
        private int stack_id;
        private String total_value;
        private LocationBean location;
        private String in_motion;
        private List<StackArrayBean> stack_array;

        public int getBetting_box() {
            return betting_box;
        }

        public void setBetting_box(int betting_box) {
            this.betting_box = betting_box;
        }

        public int getAssociate_id() {
            return associate_id;
        }

        public void setAssociate_id(int associate_id) {
            this.associate_id = associate_id;
        }

        public int getStack_id() {
            return stack_id;
        }

        public void setStack_id(int stack_id) {
            this.stack_id = stack_id;
        }

        public String getTotal_value() {
            return total_value;
        }

        public void setTotal_value(String total_value) {
            this.total_value = total_value;
        }

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public String getIn_motion() {
            return in_motion;
        }

        public void setIn_motion(String in_motion) {
            this.in_motion = in_motion;
        }

        public List<StackArrayBean> getStack_array() {
            return stack_array;
        }

        public void setStack_array(List<StackArrayBean> stack_array) {
            this.stack_array = stack_array;
        }

        public static class LocationBean {
            /**
             * x1 : 320
             * y1 : 178
             * x2 : 289
             * y2 : 129
             */

            private int x1;
            private int y1;
            private int x2;
            private int y2;

            public int getX1() {
                return x1;
            }

            public void setX1(int x1) {
                this.x1 = x1;
            }

            public int getY1() {
                return y1;
            }

            public void setY1(int y1) {
                this.y1 = y1;
            }

            public int getX2() {
                return x2;
            }

            public void setX2(int x2) {
                this.x2 = x2;
            }

            public int getY2() {
                return y2;
            }

            public void setY2(int y2) {
                this.y2 = y2;
            }
        }

        public static class StackArrayBean {
            /**
             * denomination : 100
             * type : cash
             */

            private int denomination;
            private String type;

            public int getDenomination() {
                return denomination;
            }

            public void setDenomination(int denomination) {
                this.denomination = denomination;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
