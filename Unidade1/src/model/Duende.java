package model;

import java.util.Random;

public class Duende {
    private int id;
    private long money;
    private double position;

    public Duende(int id) {
        this.id = id;
        this.money = 1000000L;
        this.position = id * 0.1; // Posição inicial baseada no ID
    }

    public void move() {
        Random random = new Random();
        double posAntiga = getPosition();
        double movimento = random.nextDouble() * 2 - 1; // -1 a 1
        
        double novaPosicao = Math.max(0, Math.min(30, posAntiga + movimento)); //delimita o movimento pra ficar entre 0 até 30
        this.setPosition(novaPosicao);
        
        System.out.println("Duende " + this.getId() + " saiu de " + posAntiga + " para " + this.getPosition());
    }

    private Long giveMoney() {
        Long perdido = this.money / 2;
        this.money = this.money - perdido;
        System.out.println("O Duende " + id + " perdeu " + perdido + " dinheiros. Coitado.");
        return perdido;
    }

    public void steal(Duende victim) {
        if (victim != null && victim != this) {
            Long roubado = victim.giveMoney();
            this.money = this.money + roubado;
            System.out.println("O Duende " + id + " roubou o Duende " + victim.getId() + " com sucesso.");
        }
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Long getMoney() { return money; }
    public void setMoney(Long money) { this.money = money; }
    public double getPosition() { return position; }
    public void setPosition(double position) { this.position = position; }
}