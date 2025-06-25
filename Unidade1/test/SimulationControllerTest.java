//import Controller.SimulationController;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import static org.junit.jupiter.api.Assertions.*;
//
//import model.Duende;
//import datastructure.TreeMapAdaptado;
//import view.SimulationView;
//
//import java.util.ArrayList;
//import java.util.List;
//import javax.swing.JFrame;
//
//public class SimulationControllerTest {
//
//    @BeforeEach
//    void setUp() {
//        Controller.SimulationController.iniciarSimulacao(2, 100.0, 1000L);
//    }
//
//    @Test
//    public void testIniciarSimulacaoComParametrosValidos() {
//        assertDoesNotThrow(() -> Controller.SimulationController.iniciarSimulacao(2, 100.0, 1000L));
//    }
//
//    @Test
//    public void testIniciarSimulacaoComNumDuendesInvalido() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.iniciarSimulacao(1, 100.0, 1000L));
//
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.iniciarSimulacao(21, 100.0, 1000L));
//    }
//
//    @Test
//    public void testIniciarSimulacaoComMaxHorizonInvalido() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.iniciarSimulacao(2, 0, 1000L));
//
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.iniciarSimulacao(2, -1.0, 1000L));
//    }
//
//    @Test
//    public void testIniciarSimulacaoComMaxCoinsInvalido() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.iniciarSimulacao(2, 100.0, 0));
//
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.iniciarSimulacao(2, 100.0, -1L));
//    }
//
//    @Test
//    public void testIniciarSimulacaoComMaxCoinsExcedente() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.iniciarSimulacao(2, 100.0, 2000001L));
//    }
//
//    @Test
//    public void testCriarDuendesComQuantidadeValida() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(3);
//        assertEquals(3, duendes.size());
//        assertEquals(0, duendes.get(0).getId());
//        assertEquals(1, duendes.get(1).getId());
//        assertEquals(2, duendes.get(2).getId());
//    }
//
//    @Test
//    public void testCriarDuendesComQuantidadeInvalida() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.criarDuendes(1));
//
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.criarDuendes(21));
//    }
//
//    @Test
//    public void testInicializarTreeMapComDuendesValidos() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        TreeMapAdaptado tma = Controller.SimulationController.inicializarTreeMap(duendes);
//        assertFalse(tma.treeMapPrincipal.isEmpty());
//        assertEquals(2, tma.treeMapPrincipal.size());
//    }
//
//    @Test
//    public void testInicializarTreeMapComListaNula() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.inicializarTreeMap(null));
//    }
//
//    @Test
//    public void testInicializarTreeMapComListaVazia() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.inicializarTreeMap(new ArrayList<>()));
//    }
//
//    @Test
//    public void testCriterioDeParadaPorMoedas() {
//        Duende duende = new Duende(1);
//        assertTrue(Controller.SimulationController.verificarChegada(duende, 1000000L));
//    }
//
//    @Test
//    public void testCriarEExibirJanelaComDuendesValidos() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        SimulationView panel = Controller.SimulationController.criarEExibirJanela(duendes);
//        assertNotNull(panel);
//
//        JFrame frame = (JFrame) panel.getTopLevelAncestor();
//        assertNotNull(frame);
//        assertEquals("Simulação de Duendes", frame.getTitle());
//    }
//
//    @Test
//    public void testCriarEExibirJanelaComListaNula() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.criarEExibirJanela(null));
//    }
//
//    @Test
//    public void testCriarEExibirJanelaComListaVazia() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.criarEExibirJanela(new ArrayList<>()));
//    }
//
//    @Test
//    public void testMoverERoubarComParametrosValidos() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        TreeMapAdaptado tma = Controller.SimulationController.inicializarTreeMap(duendes);
//        SimulationView panel = Controller.SimulationController.criarEExibirJanela(duendes);
//
//        assertDoesNotThrow(() ->
//                Controller.SimulationController.moverERoubar(duendes.getFirst(), tma, panel));
//    }
//
//    @Test
//    public void testMoverERoubarComDuendeNulo() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        TreeMapAdaptado tma = Controller.SimulationController.inicializarTreeMap(duendes);
//        SimulationView panel = Controller.SimulationController.criarEExibirJanela(duendes);
//
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.moverERoubar(null, tma, panel));
//    }
//
//    @Test
//    public void testMoverERoubarComTreeMapNulo() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        SimulationView panel = Controller.SimulationController.criarEExibirJanela(duendes);
//
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.moverERoubar(duendes.getFirst(), null, panel));
//    }
//
//    @Test
//    public void testMoverERoubarComPanelNulo() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        TreeMapAdaptado tma = Controller.SimulationController.inicializarTreeMap(duendes);
//
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.moverERoubar(duendes.getFirst(), tma, null));
//    }
//
//    @Test
//    public void testVerificarChegadaPorMoedas() {
//        Duende duende = new Duende(1);
//        duende.setCoins(1000L);
//        assertTrue(Controller.SimulationController.verificarChegada(duende, 1000L));
//    }
//
//    @Test
//    public void testVerificarChegadaPorPosicao() {
//        SimulationController.setMaxHorizon(1.0);
//        Duende duende = new Duende(1);
//        duende.setPosition(1);
//        assertTrue(Controller.SimulationController.verificarChegada(duende, 2000000L));
//    }
//
//    @Test
//    public void testVerificarChegadaFalso() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        SimulationView sv = new SimulationView(duendes);
//        TreeMapAdaptado tm = Controller.SimulationController.inicializarTreeMap(duendes);
//        Controller.SimulationController.setMaxHorizon(1500000);
//        Duende duende = duendes.getFirst();
//
//        assertFalse(Controller.SimulationController.verificarChegada(duende, 1500000L));
//    }
//
//    @Test
//    public void testVerificarChegadaSemCondicao() {
//        Duende duende = new Duende(1);
//        duende.setCoins(500L);
//        duende.setPosition(50.0);
//        assertFalse(Controller.SimulationController.verificarChegada(duende, 1000L));
//    }
//
//    @Test
//    public void testVerificarChegadaComDuendeNulo() {
//        assertThrows(IllegalArgumentException.class, () ->
//                Controller.SimulationController.verificarChegada(null, 1000L));
//    }
//
//    @Test
//    public void testGetMaxHorizon() {
//        Controller.SimulationController.iniciarSimulacao(2, 150.0, 1000L);
//        assertEquals(150.0, Controller.SimulationController.getMaxHorizon());
//    }
//
//    @Test
//    public void testExecutarLogicaSimulacaoNaoLancaExcecoes() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        TreeMapAdaptado tma = Controller.SimulationController.inicializarTreeMap(duendes);
//        SimulationView panel = Controller.SimulationController.criarEExibirJanela(duendes);
//
//        assertDoesNotThrow(() ->
//                Controller.SimulationController.executarLogicaSimulacao(duendes, tma, panel));
//    }
//
//    @Test
//    public void testPausaVisualizacaoNaoLancaExcecoes() {
//        assertDoesNotThrow(SimulationController::pausaVisualizacao);
//    }
//
//    @Test
//    public void testExibirResultadosFinaisNaoLancaExcecoes() {
//        List<Duende> duendes = Controller.SimulationController.criarDuendes(2);
//        assertDoesNotThrow(() -> Controller.SimulationController.exibirResultadosFinais(duendes));
//    }
//}
