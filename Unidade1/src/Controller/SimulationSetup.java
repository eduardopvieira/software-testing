package Controller;

import model.domain.Duende;
import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;
import java.util.ArrayList;
import java.util.List;

public class SimulationSetup {
    private final int numDuendes;
    private final double maxHorizon;

    public SimulationSetup(int numDuendes, double maxHorizon) {
        validarEntradas(numDuendes, maxHorizon);
        this.numDuendes = numDuendes;
        this.maxHorizon = maxHorizon;
    }

    public TreeMapAdaptado criarCenario() {
        List<Duende> duendes = criarDuendes();
        return inicializarTreeMap(duendes);
    }

    public GuardiaoDoHorizonte criarGuardião() {
        double posGuardiao = this.maxHorizon * 0.8;
        return new GuardiaoDoHorizonte(this.numDuendes + 1, posGuardiao);
    }

    private void validarEntradas(int numDuendes, double maxHorizon) {
        if (numDuendes <= 1 || numDuendes > 20) {
            throw new IllegalArgumentException("A quantidade de duendes deve ser de 2 a 20.");
        }
        if (maxHorizon <= 0) {
            throw new IllegalArgumentException("O horizonte máximo deve ser maior que zero.");
        }
    }

    private List<Duende> criarDuendes() {
        List<Duende> duendes = new ArrayList<>();
        for (int i = 0; i < this.numDuendes; i++) {
            duendes.add(new Duende(i));
        }
        return duendes;
    }

    private TreeMapAdaptado inicializarTreeMap(List<Duende> duendes) {
        TreeMapAdaptado tma = new TreeMapAdaptado();
        duendes.forEach(tma::addDuendeInicial);
        return tma;
    }
}
