package datastructure;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

import model.domain.interfaces.EntityOnHorizon;
import model.domain.Duende;
import model.domain.Cluster;

public class TreeMapAdaptado {

    public TreeMap<Double, EntityOnHorizon> treeMapPrincipal = new TreeMap<>();

    public void addDuendeInicial(Duende entidade) {
        if (entidade == null) {
            throw new IllegalArgumentException("Entidade não pode ser nula.");
        }
        double chave = entidade.getPosition();

        while (treeMapPrincipal.containsKey(chave)) {
            chave += 0.1; // Incrementa a chave para evitar duplicatas
        }
        entidade.setPosition(chave);
        treeMapPrincipal.put(entidade.getPosition(), entidade);
    }

    public EntityOnHorizon findNearestEntidade(EntityOnHorizon atual) {
        if (atual == null) {
            throw new IllegalArgumentException("Entidade não pode ser nula.");
        }
        if (treeMapPrincipal.size() < 2) {
            return null; // não há vizinhos para encontrar
        }

        double chaveAtual = atual.getPosition();
        Map.Entry<Double, EntityOnHorizon> anterior = treeMapPrincipal.lowerEntry(chaveAtual);
        Map.Entry<Double, EntityOnHorizon> posterior = treeMapPrincipal.higherEntry(chaveAtual);

        if (anterior == null) {
            return posterior.getValue();
        }
        if (posterior == null) {
            return anterior.getValue();
        }

        double distEsq = Math.abs(chaveAtual - anterior.getKey());
        double distDir = Math.abs(chaveAtual - posterior.getKey());

        if (distEsq < distDir) {
            return anterior.getValue();
        } else if (distDir < distEsq) {
            return posterior.getValue();
        } else {
            return verMaisRico(anterior.getValue(), posterior.getValue());
        }
    }

    public EntityOnHorizon verMaisRico(EntityOnHorizon A, EntityOnHorizon B) {
        if (A == null || B == null) {
            throw new IllegalArgumentException("Entidades para comparação não podem ser nulas.");
        }

        if (Objects.equals(A.getCoins(), B.getCoins())) {
            return new Random().nextBoolean() ? A : B;
        }

        return A.getCoins() > B.getCoins() ? A : B;
    }

    public static String getNomeEntidade(EntityOnHorizon entidade) {
        if (entidade instanceof Duende) {
            return "Duende " + ((Duende) entidade).getId();
        } else if (entidade instanceof Cluster) {
            return "Cluster com " + ((Cluster) entidade).getQuantityDuendes() + " duendes";
        } else {
            return "Entidade desconhecida";
        }
    }
}
