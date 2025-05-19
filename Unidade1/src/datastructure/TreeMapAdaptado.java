package datastructure;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

import model.Duende;

public class TreeMapAdaptado {
    public TreeMap<Double, Duende> treeMapPrincipal = new TreeMap<>();

    public void addDuende(Duende duende) {
        double chave = duende.getPosition();
        
        while (treeMapPrincipal.containsKey(chave)) {
            chave += 0.1;
            System.out.println("Conflito de duendes. Duende " + duende.getId() + " foi movido para " + chave);
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

        double distEsq = Math.abs(chaveAtual - anterior.getKey());
        double distDir = Math.abs(chaveAtual - posterior.getKey());

        if (distEsq < distDir) {
            return anterior.getValue();
        } else if (distEsq > distDir) {
            return posterior.getValue();
        } else {
            return verMaisRico(anterior.getValue(), posterior.getValue());
        }
    }

    private Duende verMaisRico(Duende A, Duende B) {
        if (Objects.equals(A.getOuro(), B.getOuro())) {
            return new Random().nextBoolean() ? A : B;
        }
        return A.getOuro() > B.getOuro() ? A : B;
    }
}