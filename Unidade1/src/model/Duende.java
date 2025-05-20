package model;

import java.util.Random;

import Controller.SimulationController;

public class Duende {
    private int id;
    private long coins;
    private double position;

    public Duende(int id) {
        this.id = id;
        this.coins = 1000000L;
        this.position = id * 0.1; // Posição inicial baseada no ID
    }

    public void move() {
        Random random = new Random();
        double posAntiga = getPosition();
        double movimento = random.nextDouble() * 2 - 1; // -1 a 1

        double newPos = posAntiga + movimento * this.coins;
        
        if (newPos > SimulationController.getMaxHorizon()) {
            newPos = SimulationController.getMaxHorizon();
        } else if (newPos < 0) {
            newPos = 0;
        }

        this.setPosition(newPos);
        
        System.out.println("Duende " + this.getId() + " saiu de " + posAntiga + " para " + this.getPosition());
    }

    private Long giveCoins() {
        Long perdido = this.coins / 2;
        this.coins = this.coins - perdido;
        System.out.println("O Duende " + id + " perdeu " + perdido + " moedas. Coitado.");
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
