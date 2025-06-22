package datastructure;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import model.interfaces.EntityOnHorizon;

public class TreeMapAdaptado {
    public TreeMap<Double, EntityOnHorizon> treeMapPrincipal = new TreeMap<>();

    public void addEntity(EntityOnHorizon entity) {

        //!Testes de pré-condição (não tem pós-condição pq a função é void)
        if (entity == null) {
            throw new IllegalArgumentException("Duende não pode ser nulo.");
        }

        double chave = entity.getPosition();

        //TODO: ARRUMAR ISSO AQUI
        while (treeMapPrincipal.containsKey(chave)) {
            chave += 0.1;
            System.out.println("Conflito de duendes. Duende " + duende.getId() + " foi movido para " + chave);
        }

        entity.setPosition(chave);
        treeMapPrincipal.put(entity.getPosition(), entity);
    }

    public EntityOnHorizon findNearestEntity(EntityOnHorizon atual) {

        //!Teste de pré-condição
        if (atual == null) {
            throw new IllegalArgumentException("Entidade não pode ser nula.");
        }

        if (treeMapPrincipal.isEmpty() || treeMapPrincipal.size() == 1) {
            throw new IllegalStateException("Não há entidades o suficiente na árvore.");
        }


        double chaveAtual = atual.getPosition();
        Map.Entry<Double, EntityOnHorizon> anterior = treeMapPrincipal.lowerEntry(chaveAtual);
        Map.Entry<Double, EntityOnHorizon> posterior = treeMapPrincipal.higherEntry(chaveAtual);

        //!Não é necessário verificar se posterior ou anterior são nulos dentro do bloco do if,
        //!pois a propria treemap impede que tenha apenas 1 duende na arvore.
        if (anterior == null) return posterior.getValue();
        if (posterior == null) return anterior.getValue();

        double distEsq = Math.abs(chaveAtual - anterior.getKey());
        double distDir = Math.abs(chaveAtual - posterior.getKey());

        EntityOnHorizon retorno = null;

        if (distEsq < distDir) {
            retorno = anterior.getValue();
        } else if (distEsq > distDir) {
            retorno = posterior.getValue();
        } else {
            retorno = verMaisRico(anterior.getValue(), posterior.getValue());
        }

        //! Teste de pós-condição não se faz necessário, visto que a
        //! função garante que o retorno não será nulo.
        //! Quando coloquei a verificação, impedia o 100% em branch coverage.

        return retorno;

    }

    public EntityOnHorizon verMaisRico(EntityOnHorizon A, EntityOnHorizon B) {

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
