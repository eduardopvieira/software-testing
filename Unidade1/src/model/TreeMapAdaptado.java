package model;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

public class TreeMapAdaptado {

    public TreeMap<Double, Duende> treeMapPrincipal = new TreeMap<>();

    public TreeMapAdaptado() {}

    public void addDuende(Duende duende) {
        double chave = duende.getPosition();

        while (treeMapPrincipal.containsKey(chave)) {
            chave += 0.1;
        }
        duende.setPosition(chave);
        System.out.println("Conflito de duendes. Duende " + duende.getId() + " foi movido para " + chave);
        treeMapPrincipal.put(duende.getPosition(), duende);
    }

    public Duende findDuendeById(int id) {
        for (Duende duende : treeMapPrincipal.values()) {
            if (duende.getId() == id) {
                return duende;
            }
        }
        return null;
    }

    public Duende findNearestDuende(Duende atual) {

        double chaveAtual = atual.getPosition();

        //* pega o nó anterior
        Map.Entry<Double, Duende> anterior = treeMapPrincipal.lowerEntry(chaveAtual);

        //* pega o nó posterior
        Map.Entry<Double, Duende> posterior = treeMapPrincipal.higherEntry(chaveAtual);

        if (anterior == null) return posterior != null ? posterior.getValue() : null;
        if (posterior == null) return anterior.getValue();

        double distEsq = calcularDistancia(atual.getPosition(), anterior.getKey());
        double distDir = calcularDistancia(atual.getPosition(), posterior.getKey());

        if (distEsq < distDir) {
            return anterior.getValue();
        } else if (distEsq > distDir) {
            return posterior.getValue();
        } else {
            return verMaisRico(anterior.getValue(), posterior.getValue());
        }

    }

    public double calcularDistancia(double A, double B) {
        return Math.abs(A - B);
    }

    public Duende verMaisRico(Duende A, Duende B) {

        //* funçao otimizada, caso os 2 duendes tenham o mesmo dinheiro, sorteia um deles pra roubar
        if (Objects.equals(A.getMoney(), B.getMoney())) {
            return new Random().nextBoolean() ? A : B;
        }

        return A.getMoney() > B.getMoney() ? A : B;
    }

}
