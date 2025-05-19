package model;

import java.util.Random;

public class Duende {
    private int id;
    private long coins;
    private double position;
    private double minHorizon, maxHorizon; //precisam desses parametros pra limitar o movimento

    public Duende(int id, double minHorizon, double maxHorizon) {
        this.id = id;
        this.coins = 1000000L;
        this.position = id * 0.1; // Posição inicial baseada no ID
        this.minHorizon = minHorizon;
        this.maxHorizon = maxHorizon;

    }

    public void move() {
        Random random = new Random();
        double posAntiga = getPosition();
        double movimento = random.nextDouble() * 2 - 1; // -1 a 1

        double newPos = posAntiga + (movimento * (this.coins/100000));
        
        if (newPos < minHorizon) {
            newPos = minHorizon;
        } else if (newPos > maxHorizon) {
            newPos = maxHorizon;
        }

        this.setPosition(newPos);
        
        System.out.println("Duende " + this.getId() + " saiu de " + posAntiga + " para " + this.getPosition());
    }

    private Long giveCoins() {
        Long perdido = this.coins / 2;
        this.coins = this.coins - perdido;
        System.out.println("O Duende " + id + " perdeu " + perdido + " dinheiros. Coitado.");
        return perdido;
    }

    public void steal(Duende victim) {
        if (victim != null && victim != this) {
            Long roubado = victim.giveCoins();
            this.coins = this.coins + roubado;
            System.out.println("O Duende " + id + " roubou o Duende " + victim.getId() + " com sucesso.");
        }
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Long getCoins() { return coins; }
    public void setCoins(Long coins) { this.coins = coins; }
    public double getPosition() { return position; }
    public void setPosition(double position) { this.position = position; }
}