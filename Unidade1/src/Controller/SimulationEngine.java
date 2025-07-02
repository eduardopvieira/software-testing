package Controller;

import model.domain.Cluster;
import model.domain.Duende;
import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;
import model.domain.interfaces.EntityOnHorizon;
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

    public boolean verificarCondicaoDeTermino() {
        if (tma.treeMapPrincipal.size() <= 2) {
            System.out.println("FIM DE JOGO! Apenas 2 ou menos entidades restantes.");
            return true;
        }
        return false;
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
            resultado = resolverColisaoComGuardião(entidadeQueSeMoveu, ocupante);
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

    private EntityOnHorizon resolverColisaoComGuardião(EntityOnHorizon e1, EntityOnHorizon e2) {
        GuardiaoDoHorizonte oGuardião = (e1 instanceof GuardiaoDoHorizonte) ? (GuardiaoDoHorizonte) e1 : (GuardiaoDoHorizonte) e2;
        EntityOnHorizon outraEntidade = (oGuardião == e1) ? e2 : e1;

        System.out.println("Guardião colidiu com " + TreeMapAdaptado.getNomeEntidade(outraEntidade));
        long moedasRoubadas = outraEntidade.beingStealed();
        oGuardião.addCoins(moedasRoubadas);
        System.out.println(TreeMapAdaptado.getNomeEntidade(outraEntidade) + " foi absorvido. Guardião obteve " + moedasRoubadas + " moedas.");
        return oGuardião;
    }
}
