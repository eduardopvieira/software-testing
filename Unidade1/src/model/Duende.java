package model;

import java.util.Random;

public class Duende {

    public static double posAtual = 0.0;

    private int id;

    private long money;

    private double position;

    public Duende (int id) {
        this.id = id;
        this.money = 1000000L;
        this.position = posAtual++;
    }

    public void move() {
        Random random = new Random();
        double posAntiga = getPosition();
        this.setPosition(random.nextDouble(3) - 1); // -1, 0, or 1
        System.out.println("Duende " + this.getId() + " saiu de " + posAntiga + " para " + this.getPosition());
    }

    private Long giveMoney() {
        Long perdido = this.money/2;
        this.money = this.money - perdido;
        System.out.println("O Duende " + id + " perdeu " + perdido + " dinheiros. Coitado.");
        return perdido;
    }

    public void steal(Duende victim) {
        Long roubado = victim.giveMoney();
        this.money = this.money + roubado;
        System.out.println("O Duende " + id + " roubou o Duende " + victim.getId() + " com sucesso.");
    }


    //Getters e Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getMoney() {
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
