package model.domain;

import model.domain.interfaces.EntityOnHorizon;

import java.util.Random;

public class GuardiaoDoHorizonte implements EntityOnHorizon {
    private int id;
    private long coins;
    private double position;
    private final Random random = new Random();

    public GuardiaoDoHorizonte(int id, double initialPosition) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do guardião deve ser positivo.");
        }
        this.id = id;
        this.coins = 0;
        this.position = initialPosition;
        System.out.println("Guardião do Horizonte (ID: " + this.id + ") foi criado na posição " + this.position);
    }

    @Override
    public void move(double maxHorizon) {
        long coinFactor = this.coins;
        int minCoinFactor = 1000000;
        if (coinFactor <= minCoinFactor) {
            coinFactor = minCoinFactor;
        }
        
        // xn+1 ← xn+1 + r * gn+1
        double r = random.nextDouble() * 2 - 1;
        double newPos = Math.round((this.position + r * coinFactor) * 10) / 10.0;

        if (newPos > maxHorizon) {
            newPos = maxHorizon-1;
        } else if (newPos < 0) {
            newPos = 0;
        }

        this.setPosition(newPos);
        System.out.println("Guardião " + this.id + " moveu-se para " + this.getPosition());
    }

    @Override
    public void steal(EntityOnHorizon victim) {
    }

    @Override
    public long beingStealed() {
        System.out.println("O Guardião do Horizonte não pode ser roubado!");
        return 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public long getCoins() {
        return this.coins;
    }

    @Override
    public void addCoins(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("A quantidade de moedas a adicionar não pode ser negativa.");
        }
        this.coins += amount;
        System.out.println("Guardião agora tem " + this.coins + " moedas.");
    }

    @Override
    public double getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(double position) {
        this.position = position;
    }
}
