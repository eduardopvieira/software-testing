package datastructure;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

import model.Duende;

public class TreeMapAdaptado {
    public TreeMap<Double, Duende> treeMapPrincipal = new TreeMap<>();

    public void addDuende(Duende duende) {

        //!Teste de pré-condição (não tem pós-condição pq a função é void)
        if (duende == null) {
            throw new IllegalArgumentException("Duende não pode ser nulo.");
        }

        double chave = duende.getPosition();
        
        while (treeMapPrincipal.containsKey(chave)) {
            chave += 0.1;
            System.out.println("Conflito de duendes. Duende " + duende.getId() + " foi movido para " + chave);
        }
        duende.setPosition(chave);
        treeMapPrincipal.put(duende.getPosition(), duende);
    }

    public Duende findNearestDuende(Duende atual) {

        //!Teste de pré-condição
        if (atual == null) {
            throw new IllegalArgumentException("Duende não pode ser nulo.");
        }

        double chaveAtual = atual.getPosition();
        Map.Entry<Double, Duende> anterior = treeMapPrincipal.lowerEntry(chaveAtual);
        Map.Entry<Double, Duende> posterior = treeMapPrincipal.higherEntry(chaveAtual);

        if (anterior == null) return posterior != null ? posterior.getValue() : null;
        if (posterior == null) return anterior.getValue();

        double distEsq = Math.abs(chaveAtual - anterior.getKey());
        double distDir = Math.abs(chaveAtual - posterior.getKey());

        Duende retorno = null;

        if (distEsq < distDir) {
            retorno = anterior.getValue();
        } else if (distEsq > distDir) {
            retorno = posterior.getValue();
        } else {
            retorno = verMaisRico(anterior.getValue(), posterior.getValue());
        }

        //! Teste de pós-condição
        if (retorno == null) {
            throw new IllegalStateException("Nenhum duende encontrado.");
        }

        return retorno;

    }

    private Duende verMaisRico(Duende A, Duende B) {

        //!Teste de pré-condição
        if (A == null || B == null) {
            throw new IllegalArgumentException("Duende não pode ser nulo.");
        }

        if (Objects.equals(A.getCoins(), B.getCoins())) {
            return new Random().nextBoolean() ? A : B;
        }

        //! Teste de pós-condição não se faz necessário aqui. Se A ou B não forem nulos,
        //! a condição vai necessariamente retornar um dos 2.

        return A.getCoins() > B.getCoins() ? A : B;

    }
}
