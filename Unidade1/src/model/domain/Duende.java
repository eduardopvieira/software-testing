package model.domain;

import model.domain.interfaces.EntityOnHorizon;
import java.util.Random;

public class Duende implements EntityOnHorizon {

    // <<< MUDANÇA 1: Deixa de ser 'static final' e vira um campo de instância final >>>
    private final Random random;

    private int id;
    private long coins;
    private double position;

    public Duende(int id) {
        this.id = id;
        this.coins = 1000000L;
        this.position = id * 0.1;
        random = new Random();
    }

    //esse construtor é necessário pra poder fazer o mock
    public Duende(int id, Random random) {
        this.id = id;
        this.coins = 1000000L;
        this.position = id * 0.1;
        this.random = random;
    }

    public void move(double maxHorizon) {
        double posAntiga = getPosition();
        double movimento = random.nextDouble() * 2 - 1;
        double newPos = Math.round((posAntiga + movimento * this.coins) * 10) / 10.0;

        if (newPos > maxHorizon) {
            newPos = maxHorizon - 1;
        } else if (newPos < 0) {
            newPos = 0;
        }
        this.setPosition(newPos);
        System.out.println("Duende " + this.getId() + " moveu-se para " + this.getPosition());
    }

    @Override
    public long beingStealed() {
        long perdido = this.coins / 2;
        this.coins -= perdido;
        System.out.println("O Duende " + id + " perdeu " + perdido + " moedas. Coitado.");
        return perdido;
    }

    @Override
    public void steal(EntityOnHorizon victim) {
        if (victim != null && victim != this) {
            long stolenCoins = victim.beingStealed();
            this.addCoins(stolenCoins);
            System.out.println("Duende " + this.getId() + " roubou " + stolenCoins + " moedas de Duende/Cluster " + victim.getId());
        } else {
            throw new IllegalArgumentException("A entidade não pode ser nula ou roubar a si mesma.");
        }
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public long getCoins() {
        return coins;
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public void setPosition(double position) {
        if (position < 0) {
            throw new IllegalArgumentException("Posição não pode ser negativa.");
        }
        this.position = position;
    }

    @Override
    public void addCoins(long amount) {
        this.coins += amount;
    }


    public void setCoins(long i) {
        this.coins = i;
    }
}
