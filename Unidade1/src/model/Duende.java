package model;

import java.util.Random;

public class Duende {
    private int id;
    private long coins;
    private double position;

    public Duende(int id) {
        this.id = id;
        this.coins = 1000000L;
        this.position = id * 0.1;
    }

    public void move(double maxHorizon) {
        Random random = new Random();
        double posAntiga = getPosition();
        double movimento = random.nextDouble() * 2 - 1; // -1 a 1

        double newPos = posAntiga + movimento * this.coins;

        //! O teste desse trecho depende de aleatoriedade, ja que o valor gerado é random.
        if (newPos > maxHorizon) {
            newPos = maxHorizon;
        } else if (newPos < 0) {
            newPos = 0;
        }

        this.setPosition(newPos);
        
        System.out.println("Duende " + this.getId() + " saiu de " + posAntiga + " para " + this.getPosition());
    }

    public Long giveCoins() {
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
        } else {
            throw new IllegalArgumentException("O duende não pode ser nulo ou roubar a si mesmo.");
        }
    }

    // Getters e Setters
    public int getId() { return id; }
    public Long getCoins() { return coins; }
    public double getPosition() { return position; }
    public void setPosition(double position) {
        if (position < 0) {
            throw new IllegalArgumentException("Posição não pode ser negativa.");
        }
        this.position = position; }

    public void setCoins(Long i) {
        this.coins = i;
    }
}
