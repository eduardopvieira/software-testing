package baseados_em_propriedade;

import Controller.SimulationSetup;
import model.datastructure.TreeMapAdaptado;
import model.entities.GuardiaoDoHorizonte;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;

import static org.assertj.core.api.Assertions.assertThat;

class SimulationSetupTest {

    @Property
    @Label("criarCenario deve sempre criar o número correto de duendes")
    void criarCenarioProperty(@ForAll @IntRange(min = 2, max = 20) int numDuendes) {
        SimulationSetup setup = new SimulationSetup(numDuendes, 1000.0);

        TreeMapAdaptado cenario = setup.criarCenario();

        assertThat(cenario.treeMapPrincipal).hasSize(numDuendes);
    }

    @Property
    @Label("criarGuardião deve sempre calcular ID e Posição corretamente")
    void criarGuardiaoProperty(
            @ForAll @IntRange(min = 2, max = 20) int numDuendes,
            @ForAll @DoubleRange(min = 1.0, max = 1_000_000.0) double maxHorizon) {

        SimulationSetup setup = new SimulationSetup(numDuendes, maxHorizon);

        GuardiaoDoHorizonte guardiao = setup.criarGuardião();

        assertThat(guardiao.getId()).isEqualTo(numDuendes + 1);
        assertThat(guardiao.getPosition()).isEqualTo(maxHorizon * 0.8);
    }
}
