package model;

public class SimulacaoParams {
    double minHorizon, maxHorizon;
    Long maxCoins;

    public SimulacaoParams(double minHorizon, double maxHorizon, Long maxCoins) {
        this.minHorizon = minHorizon;
        this.maxHorizon = maxHorizon;
        this.maxCoins = maxCoins;
    }
    public double getMinHorizon() {
        return minHorizon;
    }
    public void setMinHorizon(double minHorizon) {
        this.minHorizon = minHorizon;
    }
    public double getMaxHorizon() {
        return maxHorizon;
    }
    public void setMaxHorizon(double maxHorizon) {
        this.maxHorizon = maxHorizon;
    }
    public Long getMaxCoinsLong() {
        return this.maxCoins;
    }
    public void setMaxCoinsLong(Long maxCoinsLong) {
        this.maxCoins = maxCoinsLong;
    }

}
