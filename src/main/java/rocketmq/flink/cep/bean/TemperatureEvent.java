package rocketmq.flink.cep.bean;

public class TemperatureEvent extends MonitoringEvent {

    private double temperature;



    @Override
    public void setRackID(int rackID) {
        super.setRackID(rackID);
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

}
