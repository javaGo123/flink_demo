package rocketmq.flink.cep.bean;

public class TemperatureWarning {

    private int rackID;
    private double averageTemperature;

    public TemperatureWarning() {
    }

    public TemperatureWarning(int rackID, double averageTemperature) {
        this.rackID = rackID;
        this.averageTemperature = averageTemperature;
    }

    public int getRackID() {
        return rackID;
    }

    public void setRackID(int rackID) {
        this.rackID = rackID;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }
}
