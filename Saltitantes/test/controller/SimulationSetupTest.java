package controller;

import Controller.SimulationSetup;
import model.entities.Duende;
import model.entities.GuardiaoDoHorizonte;
import model.datastructure.TreeMapAdaptado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SimulationSetupTest {

    // essa classe é responsável por configurar o cenário inicial da simulação, e por ser mais da área de UI,
    // limitamos os testes a algumas funcionalidades básicas e validações de entrada.

    // testes de dominio e fronteira

    @Test
    @DisplayName("Deve lançar exceção para número de duendes abaixo do limite")
    void constructor_ComNumeroDeDuendesInvalido_LancaExcecao() {
        // testando a fronteira inferior (1) e superior (21)
        assertThatThrownBy(() -> new SimulationSetup(1, 100.0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new SimulationSetup(21, 100.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção para horizonte máximo inválido")
    void constructor_ComHorizonteInvalido_LancaExcecao() {
        // testando a fronteira (0) e um valor inválido (-1)
        assertThatThrownBy(() -> new SimulationSetup(5, 0.0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new SimulationSetup(5, -1.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve ser instanciado com sucesso para entradas válidas")
    void constructor_ComEntradasValidas_NaoLancaExcecao() {
        // testa se a criação ocorre sem erros para um caso válido
        assertThatCode(() -> new SimulationSetup(10, 500.0))
                .doesNotThrowAnyException();
    }

    // testes de funcionalidade

    @Test
    @DisplayName("Deve criar um cenário com o número correto de duendes")
    void criarCenario_RetornaMapaComNumeroCorretoDeDuendes() {
        int numDuendes = 7;
        SimulationSetup setup = new SimulationSetup(numDuendes, 1000.0);

        TreeMapAdaptado cenario = setup.criarCenario();

        assertThat(cenario).isNotNull();
        assertThat(cenario.treeMapPrincipal).hasSize(numDuendes);
        assertThat(cenario.treeMapPrincipal.values()).allMatch(entidade -> entidade instanceof Duende);
    }

    @Test
    @DisplayName("Deve criar um guardião com ID e posição corretos")
    void criarGuardião_RetornaGuardiaoComEstadoCorreto() {

        int numDuendes = 10;
        double maxHorizon = 1000.0;
        SimulationSetup setup = new SimulationSetup(numDuendes, maxHorizon);

        GuardiaoDoHorizonte guardiao = setup.criarGuardião();

        assertThat(guardiao).isNotNull();
        // ID esperado: numDuendes + 1 = 11
        assertThat(guardiao.getId()).isEqualTo(numDuendes + 1);
        // posicao esperada: 1000.0 * 0.8 = 800.0
        assertThat(guardiao.getPosition()).isEqualTo(maxHorizon * 0.8);
    }
}
