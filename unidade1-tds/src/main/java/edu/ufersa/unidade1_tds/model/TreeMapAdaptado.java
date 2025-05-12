package edu.ufersa.unidade1_tds.model;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

public class TreeMapAdaptado {
    private TreeMap<Double, Duende> treeMapPrincipal = new TreeMap<>();

    public void addDuende(Duende duende) {
        double chave = duende.getPosition();

        while (treeMapPrincipal.containsKey(chave)) {
            chave += 0.1;
        }
        duende.setPosition(chave);
        treeMapPrincipal.put(duende.getPosition(), duende);
    }

    public Duende findNearestDuende(Duende atual) {
        double chaveAtual = atual.getPosition();

        Map.Entry<Double, Duende> anterior = treeMapPrincipal.lowerEntry(chaveAtual);
        Map.Entry<Double, Duende> posterior = treeMapPrincipal.higherEntry(chaveAtual);

        if (anterior == null) return posterior != null ? posterior.getValue() : null;
        if (posterior == null) return anterior.getValue();

        double distEsq = calcularDistancia(chaveAtual, anterior.getKey());
        double distDir = calcularDistancia(chaveAtual, posterior.getKey());

        if (distEsq < distDir) {
            return anterior.getValue();
        } else if (distEsq > distDir) {
            return posterior.getValue();
        } else {
            return verMaisRico(anterior.getValue(), posterior.getValue());
        }
    }

    private double calcularDistancia(double a, double b) {
        return Math.abs(a - b);
    }

    private Duende verMaisRico(Duende a, Duende b) {
        if (Objects.equals(a.getMoney(), b.getMoney())) {
            return new Random().nextBoolean() ? a : b;
        }
        return a.getMoney() > b.getMoney() ? a : b;
    }

    public TreeMap<Double, Duende> getTreeMapPrincipal() {
        return treeMapPrincipal;
    }

    public void clear() {
        treeMapPrincipal.clear();
    }
}
