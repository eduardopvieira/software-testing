package Controller;

import model.datastructure.TreeMapAdaptado;
import model.entities.Cluster;
import model.entities.Duende;
import model.entities.GuardiaoDoHorizonte;
import model.entities.interfaces.EntityOnHorizon;
import java.util.ArrayList;
import java.util.List;

public class SimulationEngine {

    private final TreeMapAdaptado tma;
    private final double maxHorizon;

    public SimulationEngine(TreeMapAdaptado tma, double maxHorizon) {
        this.tma = tma;
        this.maxHorizon = maxHorizon;
    }

    public void executarRodada() {
        List<Double> chavesDaRodada = new ArrayList<>(tma.treeMapPrincipal.keySet());

        for (Double chave : chavesDaRodada) {
            EntityOnHorizon entidade = tma.treeMapPrincipal.get(chave);
            if (entidade != null) {
                processarTurno(entidade);
            }
        }
    }

    public int verificarCondicaoDeTermino() {
        long guardiaoCoins = 0;
        long duendesCoins = 0;

        for (EntityOnHorizon entidade : tma.treeMapPrincipal.values()) {
            if (entidade instanceof GuardiaoDoHorizonte) {
                guardiaoCoins = ((GuardiaoDoHorizonte) entidade).getCoins();
            } else {
                duendesCoins += entidade.getCoins();
            }
        }

        if (tma.treeMapPrincipal.size() <= 2) {
            System.out.println("FIM DE JOGO! Apenas 2 ou menos entidades restantes.");
            return 1;
        } else if (guardiaoCoins > duendesCoins) {
            System.out.println("FIM DE JOGO! Guardião do Horizonte venceu com " + guardiaoCoins + " moedas.");
            return -1;
        }

        return 0;
    }

    private void processarTurno(EntityOnHorizon entidade) {
        EntityOnHorizon ator = logicaMovimento(entidade);
        if (!(ator instanceof GuardiaoDoHorizonte)) {
            logicaRoubo(ator);
        }
    }

    private EntityOnHorizon logicaMovimento(EntityOnHorizon entidade) {
        tma.treeMapPrincipal.remove(entidade.getPosition());
        entidade.move(this.maxHorizon);
        EntityOnHorizon ocupante = tma.treeMapPrincipal.get(entidade.getPosition());

        if (ocupante == null) {
            tma.treeMapPrincipal.put(entidade.getPosition(), entidade);
            return entidade;
        } else {
            return resolverColisao(entidade, ocupante);
        }
    }

    private void logicaRoubo(EntityOnHorizon entidade) {
        EntityOnHorizon vitima = tma.findNearestEntidade(entidade);
        if (vitima != null && vitima != entidade) {
            entidade.steal(vitima);
        }
    }

    private EntityOnHorizon resolverColisao(EntityOnHorizon entidadeQueSeMoveu, EntityOnHorizon ocupante) {
        double posColisao = entidadeQueSeMoveu.getPosition();
        System.out.println("COLISÃO em " + posColisao + "! " + TreeMapAdaptado.getNomeEntidade(entidadeQueSeMoveu) + " vs " + TreeMapAdaptado.getNomeEntidade(ocupante));

        tma.treeMapPrincipal.remove(posColisao);
        EntityOnHorizon resultado;

        if (entidadeQueSeMoveu instanceof GuardiaoDoHorizonte || ocupante instanceof GuardiaoDoHorizonte) {
            resultado = resolverColisaoComGuardiao(entidadeQueSeMoveu, ocupante);
        } else if (entidadeQueSeMoveu instanceof Duende && ocupante instanceof Duende) {
            resultado = new Cluster((Duende) entidadeQueSeMoveu, (Duende) ocupante);
        } else if (entidadeQueSeMoveu instanceof Cluster && ocupante instanceof Cluster) {
            ((Cluster) entidadeQueSeMoveu).addToCluster(ocupante);
            resultado = entidadeQueSeMoveu;
        } else {
            Cluster cluster = (entidadeQueSeMoveu instanceof Cluster) ? (Cluster) entidadeQueSeMoveu : (Cluster) ocupante;
            EntityOnHorizon duende = (entidadeQueSeMoveu instanceof Duende) ? entidadeQueSeMoveu : ocupante;
            cluster.addToCluster(duende);
            resultado = cluster;
        }

        tma.treeMapPrincipal.put(posColisao, resultado);
        return resultado;
    }

    private EntityOnHorizon resolverColisaoComGuardiao(EntityOnHorizon e1, EntityOnHorizon e2) {
        GuardiaoDoHorizonte guardiao = (e1 instanceof GuardiaoDoHorizonte) ? (GuardiaoDoHorizonte) e1 : (GuardiaoDoHorizonte) e2;
        EntityOnHorizon outraEntidade = (guardiao == e1) ? e2 : e1;

        System.out.println("Guardião colidiu com " + TreeMapAdaptado.getNomeEntidade(outraEntidade));
        long moedasAbsorvidas = outraEntidade.getCoins();

        guardiao.addCoins(moedasAbsorvidas);
        System.out.println(TreeMapAdaptado.getNomeEntidade(outraEntidade) + " foi absorvido. Guardião obteve " + moedasAbsorvidas + " moedas.");
        return guardiao;
    }
}
