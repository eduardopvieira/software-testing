
import org.junit.jupiter.api.Test;

import model.Duende;
import datastructure.TreeMapAdaptado;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationControllerTest {

    @Test
    public void testCriarDuendesComQuantidadeValida() {
        List<Duende> duendes = Controller.SimulationController.criarDuendes(3);
        assertEquals(3, duendes.size());
        assertEquals(0, duendes.getFirst().getId());
    }

    @Test
    public void testCriarDuendesComQuantidadeInvalida() {
        //!Teste de borda
        assertThrows(IllegalArgumentException.class, () -> Controller.SimulationController.criarDuendes(1));
        assertThrows(IllegalArgumentException.class, () -> Controller.SimulationController.criarDuendes(21));
    }

    @Test
    public void testIniciarSimulacaoComPosicaoInvalida() {
        //!Teste de borda
        assertThrows(IllegalArgumentException.class, () -> Controller.SimulationController.iniciarSimulacao(2, -1, 100));
        assertThrows(IllegalArgumentException.class, () -> Controller.SimulationController.iniciarSimulacao(2, 0, 100));
    }

    @Test
    public void testInicializarTreeMapComDuendesValidos() {
        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
        TreeMapAdaptado tma = Controller.SimulationController.inicializarTreeMap(duendes);
        assertFalse(tma.treeMapPrincipal.isEmpty());

        List<Duende> duendes2 = Controller.SimulationController.criarDuendes(20);
        TreeMapAdaptado tma2 = Controller.SimulationController.inicializarTreeMap(duendes2);
        assertTrue(tma2.treeMapPrincipal.size() > tma.treeMapPrincipal.size());
    }


    @Test
    public void testInicializarTreeMapComListaNulaOuVazia() {
        assertThrows(IllegalArgumentException.class, () -> Controller.SimulationController.inicializarTreeMap(null));
        assertThrows(IllegalArgumentException.class, () -> Controller.SimulationController.inicializarTreeMap(new ArrayList<>()));
    }

    @Test
    public void testVerificarChegadaPorMoedas() {
        Duende d = new Duende(1);
        d.setCoins(100L);
        Controller.SimulationController.iniciarSimulacao(2, 10, 100); // seta maxCoins = 100
        assertTrue(new Controller.SimulationController().verificarChegada(d, 100L));
    }

    @Test
    public void testVerificarChegadaPorPosicao() {
        Duende d = new Duende(1);
        d.setPosition(50);
        Controller.SimulationController.iniciarSimulacao(2, 50, 999); // seta maxHorizon = 50
        assertTrue(new Controller.SimulationController().verificarChegada(d, 999L));
    }

    @Test
    public void testVerificarChegadaSemCondicao() {
        Duende d = new Duende(1);
        d.setCoins(10L);
        d.setPosition(5);
        Controller.SimulationController.iniciarSimulacao(2, 100, 1000);
        assertFalse(new Controller.SimulationController().verificarChegada(d, 1000L));
    }

    @Test
    public void testMoverERoubarComParametrosNulos() {
        assertThrows(IllegalArgumentException.class, () -> Controller.SimulationController.moverERoubar(null, null, null));
    }

    // Métodos como executarLogicaSimulacao e criarEExibirJanela são difíceis de testar diretamente
    // pois envolvem GUI e Threads. Para isso, considerar testes de integração ou mocks avançados.
}
