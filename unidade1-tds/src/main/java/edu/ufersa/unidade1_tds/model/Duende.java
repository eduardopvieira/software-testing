package edu.ufersa.unidade1_tds.model;

import java.util.Random;

public class Duende {
    private static double posAtual = 0.0;

    private int id;
    private long money;
    private double position;

    public Duende(int id) {
        this.id = id;
        this.money = 1000000L;
        this.position = posAtual++;
    }

    public void move() {
        Random random = new Random();
        double posAntiga = getPosition();
        this.setPosition(posAntiga + (random.nextDouble() * 2 - 1));
    }

    public Long giveMoney() {
        Long perdido = this.money / 2;
        this.money -= perdido;
        return perdido;
    }

    public void steal(Duende victim) {
        if (victim != null && victim != this) {
            Long roubado = victim.giveMoney();
            this.money += roubado;
        }
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }
}
