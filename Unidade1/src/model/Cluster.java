package model;

import model.interfaces.EntityOnHorizon;

import java.util.List;
import java.util.Random;

public class Cluster implements EntityOnHorizon {
    Random random = new Random();

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

    public void addToCluster(EntityOnHorizon entidade) {
        if (entidade instanceof Duende) {
            this.duendes.add((Duende) entidade);
            quantityDuendes++;
            coins += entidade.getCoins();
        } else if (entidade instanceof Cluster) {
            Cluster cluster = (Cluster) entidade;
            quantityDuendes += cluster.getQuantityDuendes();
            duendes.addAll(cluster.getDuendes());
            coins += cluster.getCoins();
        } else {
            throw new IllegalArgumentException("A entidade deve ser um Duende ou um Cluster.");
        }
    }

    public void move(double maxHorizon) {
        double posAntiga = getPosition();
        double movimento = random.nextDouble() * 2 - 1;
        double newPos = posAntiga + movimento * this.coins;

        if (newPos > maxHorizon) {
            newPos = maxHorizon;
        } else if (newPos < 0) {
            newPos = 0;
        }

        this.setPosition(newPos);
        System.out.println("Cluster " + this.getId() + " moveu-se para " + this.getPosition());
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
