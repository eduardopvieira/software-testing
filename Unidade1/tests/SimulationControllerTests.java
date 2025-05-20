import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import src.Controller.SimulationController;
import src.model.Duende;
import src.datastructure.TreeMapAdaptado;

class SimulationControllerTest {

    private List<Duende> duendes;

    @BeforeEach
    void setUp() {
        // Configuração inicial para os testes
        duendes = List.of(
                new Duende(1),
                new Duende(2),
                new Duende(3)
        );
    }

    @Test
    void testCriarDuendesQuantidadeValida() {
        int quantidade = 5;
        List<Duende> resultado = SimulationController.criarDuendes(quantidade);

        assertEquals(quantidade, resultado.size());
        for (int i = 0; i < quantidade; i++) {
            assertEquals(i, resultado.get(i).getId());
        }
    }

    @Test
    void testCriarDuendesQuantidadeInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            SimulationController.criarDuendes(1); // Abaixo do mínimo
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SimulationController.criarDuendes(21); // Acima do máximo
        });
    }

    @Test
    void testInicializarTreeMapComDuendesValidos() {
        TreeMapAdaptado tma = SimulationController.inicializarTreeMap(duendes);

        assertNotNull(tma);
        assertFalse(tma.treeMapPrincipal.isEmpty());
        assertEquals(duendes.size(), tma.treeMapPrincipal.size());
    }

    @Test
    void testInicializarTreeMapComListaVazia() {
        assertThrows(IllegalArgumentException.class, () -> {
            SimulationController.inicializarTreeMap(List.of());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SimulationController.inicializarTreeMap(null);
        });
    }

    @Test
    void testVerificarChegadaPorMoedas() {
        Duende duende = new Duende(1);
        duende.setCoins(100);

        assertTrue(SimulationController.verificarChegada(duende, 50L));
    }

    @Test
    void testVerificarChegadaPorHorizonte() {
        Duende duende = new Duende(1);
        duende.setPosition(100);

        assertTrue(SimulationController.verificarChegada(duende, Long.MAX_VALUE));
    }

    @Test
    void testVerificarChegadaSemCondicoes() {
        Duende duende = new Duende(1);
        duende.setCoins(10);
        duende.setPosition(10);

        assertFalse(SimulationController.verificarChegada(duende, 100L));
    }

    @Test
    void testVerificarChegadaDuendeNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            SimulationController.verificarChegada(null, 100L);
        });
    }

    @Test
    void testMoverERoubarParametrosNulos() {
        assertThrows(IllegalArgumentException.class, () -> {
            SimulationController.moverERoubar(null, new TreeMapAdaptado(), null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SimulationController.moverERoubar(new Duende(1), null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SimulationController.moverERoubar(new Duende(1), new TreeMapAdaptado(), null);
        });
    }

    // Teste para verificar se os métodos estáticos podem ser chamados sem exceções
    @Test
    void testChamadaMetodosEstaticos() {
        assertDoesNotThrow(() -> {
            SimulationController.getMaxHorizon();
        });
    }
}
