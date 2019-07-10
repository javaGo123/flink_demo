package rocketmq.flink.cep.bean;

public class PowerEvent extends MonitoringEvent {

    private double voltage;



    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }




}
