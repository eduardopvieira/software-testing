package edu.ufersa.unidade1_tds.service;

import edu.ufersa.unidade1_tds.model.Duende;
import edu.ufersa.unidade1_tds.model.TreeMapAdaptado;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DuendeService {
    private TreeMapAdaptado tma = new TreeMapAdaptado();
    private int iteracaoAtual = 0;;
    private boolean simulacaoIniciada = false;

    public void iniciarSimulacao(int numDuendes) {
        tma.clear();
        simulacaoIniciada = true;

        for (int i = 0; i < numDuendes; i++) {
            Duende duende = new Duende(i);
            tma.addDuende(duende);
        }
    }

    public void executarIteracao() {
        List<Duende> iterarDuendes = new ArrayList<>(tma.getTreeMapPrincipal().values());

        for (Duende duende : iterarDuendes) {
            double chave = duende.getPosition();
            tma.getTreeMapPrincipal().remove(chave);
            duende.move();
            tma.addDuende(duende);
            Duende vitima = tma.findNearestDuende(duende);
            if (vitima != null && vitima != duende) {
                duende.steal(vitima);
            }
        }

        iteracaoAtual++;
    }

    public List<Duende> getDuendes() {
        return new ArrayList<>(tma.getTreeMapPrincipal().values());
    }

    public int getIteracaoAtual() {
        return iteracaoAtual;
    }

     public void resetarSimulacao() {
        tma.clear();
        iteracaoAtual = 0;
        simulacaoIniciada = false;
    }

    public boolean isSimulacaoIniciada() {
        return simulacaoIniciada;
    }

}
