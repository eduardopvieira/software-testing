package model.interfaces;

public interface EntityOnHorizon {
    double getPosition();
    void setPosition(double position);

    void steal(EntityOnHorizon victim);

    long beingStealed();

    int getId();

    long getCoins();
    void addCoins(long amount);
}
