package rocketmq.flink.cep.bean;

public abstract  class MonitoringEvent {

    private int rackID;


    public int getRackID() {
        return rackID;
    }

    public void setRackID(int rackID) {
        this.rackID = rackID;
    }


}
