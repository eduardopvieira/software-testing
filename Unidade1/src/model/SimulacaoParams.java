package model;

public class SimulacaoParams {
    double minHorizon, maxHorizon, pontoParada;

    public SimulacaoParams(double minHorizon, double maxHorizon, double pontoParada) {
        this.minHorizon = minHorizon;
        this.maxHorizon = maxHorizon;
        this.pontoParada = pontoParada;
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
    public double getPontoParada() {
        return pontoParada;
    }
    public void setPontoParada(double pontoParada) {
        this.pontoParada = pontoParada;
    }

}
