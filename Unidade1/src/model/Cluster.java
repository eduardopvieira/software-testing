package model;

import model.interfaces.EntityOnHorizon;

import java.util.List;

public class Cluster implements EntityOnHorizon {
    List<Duende> duendes;
    long coins;
    double position;
    int quantityDuendes;

    public Cluster(Duende duende1, Duende duende2) {
        this.duendes = List.of(duende1, duende2);
        this.coins = duende1.getCoins() + duende2.getCoins();
        this.position = duende1.getPosition();
        this.quantityDuendes = 2;
    }

    public void addToCluster(Duende duende) {
        duendes.add(duende);
        coins += duende.getCoins();
        quantityDuendes++;
    }


    @Override
    public double getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(double position) {
        if (position < 0) {
            throw new IllegalArgumentException("A posição não pode ser negativa.");
        }
        this.position = position;
        System.out.println("Cluster agora está na posição " + position);
    }

    @Override
    public long beingStealed() {
        long perdido = this.coins / 2;
        this.coins -= perdido;
        System.out.println("O Cluster perdeu " + perdido + " moedas. Coitado.");
        return perdido;
    }

    @Override
    public void steal(EntityOnHorizon victim) {
        if (victim != null && victim != this) {
            long stolenCoins = victim.beingStealed();
            this.addCoins(stolenCoins);
            System.out.println("Duende " + this.getId() + " roubou " + stolenCoins + " moedas de Duende " + victim.getId());
        } else {
            throw new IllegalArgumentException("A entidade não pode ser nula ou roubar a si mesma.");
        }
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public long getCoins() {
        return this.coins;
    }

    public int getQuantityDuendes() {
        return this.quantityDuendes;
    }

    public List<Duende> getDuendes() {
        return this.duendes;
    }
    @Override
    public void addCoins(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("A quantidade de moedas a adicionar não pode ser negativa.");
        }
        this.coins += amount;
        System.out.println("Cluster agora tem " + coins + " moedas.");
    }
}
