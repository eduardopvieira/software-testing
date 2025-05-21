import datastructure.TreeMapAdaptado;
import model.Duende;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TreeMapAdaptadoTest {

    private TreeMapAdaptado treeMapAdaptado;
    private Duende duende1, duende2, duende3, duende4;

    @BeforeEach
    void setUp() {
        treeMapAdaptado = new TreeMapAdaptado();

        duende1 = new Duende(1);
        duende1.setPosition(10.0);

        duende2 = new Duende(2);
        duende2.setPosition(20.0);

        duende3 = new Duende(3);
        duende3.setPosition(30.0);

        duende4 = new Duende(4);
        duende4.setPosition(40.0);
    }

    @Test
    void testAddDuende() {
        treeMapAdaptado.addDuende(duende1);
        assertEquals(1, treeMapAdaptado.treeMapPrincipal.size());
        assertEquals(duende1, treeMapAdaptado.treeMapPrincipal.get(10.0));
    }

    @Test
    void testAddDuendeComPosicaoExistente() {
        Duende duendePosicaoZero = new Duende(5);
        duendePosicaoZero.setPosition(0.0);

        Duende duendePosicao10 = new Duende(6);
        duendePosicao10.setPosition(10.0);

        treeMapAdaptado.addDuende(duendePosicaoZero);
        treeMapAdaptado.addDuende(duendePosicao10);

        assertEquals(2, treeMapAdaptado.treeMapPrincipal.size());
        assertTrue(treeMapAdaptado.treeMapPrincipal.containsKey(0.0));
        assertEquals(duendePosicaoZero, treeMapAdaptado.treeMapPrincipal.get(0.0));
    }

    @Test
    void testAddDuendeComPosicaoExistenteComColisao() {
        Duende duendeA = new Duende(5);
        duendeA.setPosition(10.0);

        Duende duendeB = new Duende(6);
        duendeB.setPosition(10.0); // Posição colidindo

        treeMapAdaptado.addDuende(duendeA);
        treeMapAdaptado.addDuende(duendeB);

        assertEquals(2, treeMapAdaptado.treeMapPrincipal.size());
        assertTrue(treeMapAdaptado.treeMapPrincipal.containsKey(10.0));
        assertTrue(treeMapAdaptado.treeMapPrincipal.containsKey(10.1));
        assertEquals(duendeA, treeMapAdaptado.treeMapPrincipal.get(10.0));
        assertEquals(duendeB, treeMapAdaptado.treeMapPrincipal.get(10.1));
    }

    @Test
    void testAddDuendeNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            treeMapAdaptado.addDuende(null);
        });
    }

    @Test
    void testAddDuendeComPosicaoInvalida() {
        Duende duendeComPosicaoInvalida = new Duende(6);

        assertThrows(IllegalArgumentException.class, () -> {
            duendeComPosicaoInvalida.setPosition(-1.0);
            treeMapAdaptado.addDuende(duendeComPosicaoInvalida);
        });
    }

    @Test
    void testFindNearestDuendeComDoisDuendes() {
        treeMapAdaptado.addDuende(duende1); // posição 10.0
        treeMapAdaptado.addDuende(duende2); // posição 20.0

        Duende duendeProcurado = new Duende(7);
        duendeProcurado.setPosition(14.0);
        duendeProcurado.setCoins(75L);

        Duende maisProximo = treeMapAdaptado.findNearestDuende(duendeProcurado);

        assertEquals(duende1, maisProximo);
    }

    @Test
    void testFindNearestDuendeComDistanciaIgualEMaiorRico() {
        Duende duendeA = new Duende(8);
        duendeA.setPosition(10.0);
        duendeA.setCoins(100L);

        Duende duendeB = new Duende(9);
        duendeB.setPosition(30.0);
        duendeB.setCoins(300L);

        treeMapAdaptado.addDuende(duendeA);
        treeMapAdaptado.addDuende(duendeB);

        Duende duendeProcurado = new Duende(10);
        duendeProcurado.setPosition(20.0);
        duendeProcurado.setCoins(200L);

        Duende maisProximo = treeMapAdaptado.findNearestDuende(duendeProcurado);

        assertEquals(duendeB, maisProximo);
    }

    @Test
    void testFindNearestDuendeComDistanciaIgualEMesmaRiqueza() {
        Duende duendeA = new Duende(11);
        duendeA.setPosition(10.0);
        duendeA.setCoins(100L);

        Duende duendeB = new Duende(12);
        duendeB.setPosition(30.0);
        duendeB.setCoins(100L);

        treeMapAdaptado.addDuende(duendeA);
        treeMapAdaptado.addDuende(duendeB);

        Duende duendeProcurado = new Duende(13);
        duendeProcurado.setPosition(20.0);
        duendeProcurado.setCoins(200L);

        Duende maisProximo = treeMapAdaptado.findNearestDuende(duendeProcurado);

        assertTrue(maisProximo.getId() == 11 || maisProximo.getId() == 12);
    }

    @Test
    void testFindNearestDuendeNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            treeMapAdaptado.findNearestDuende(null);
        });
    }

    @Test
    void testFindNearestDuendeComApenasUmDuende() {
        treeMapAdaptado.addDuende(duende1);

        assertThrows(IllegalStateException.class, () -> {
            treeMapAdaptado.findNearestDuende(duende1);
        });
    }

    @Test
    void testFindNearestDuendeComArvoreVazia() {
        assertThrows(IllegalStateException.class, () -> {
            treeMapAdaptado.findNearestDuende(duende1);
        });
    }

    @Test
    void testFindNearestDuendeSemEntradasValidas() {
        treeMapAdaptado.addDuende(duende1);
        treeMapAdaptado.addDuende(duende2);

        Duende duendeProcurado = new Duende(14);
        duendeProcurado.setPosition(5.0);

        // Neste caso, só o duende1 (10.0) deve ser considerado
        Duende maisProximo = treeMapAdaptado.findNearestDuende(duendeProcurado);
        assertEquals(duende1, maisProximo);
    }

    @Test
    void testNearestDuendeADireita() {

        Duende duendeEsquerda = new Duende(1);
        Duende duendeMeio = new Duende(2);
        Duende duendeDireita = new Duende(3);

        duendeEsquerda.setPosition(1.0);
        duendeMeio.setPosition(10.0);
        duendeDireita.setPosition(11.0);

        treeMapAdaptado.addDuende(duendeEsquerda);
        treeMapAdaptado.addDuende(duendeMeio);
        treeMapAdaptado.addDuende(duendeDireita);

        Duende maisProximo = treeMapAdaptado.findNearestDuende(duendeMeio);
        assertEquals(duendeDireita, maisProximo);
    }

    @Test
    void testVerMaisRicoComPrimeiroMaisRico() {
        duende3.setCoins(200L);
        duende1.setCoins(100L);

        Duende resultado = treeMapAdaptado.verMaisRico(duende3, duende1);
        assertEquals(duende3, resultado);
    }

    @Test
    void testVerMaisRicoComSegundoMaisRico() {
        duende1.setCoins(100L);
        duende2.setCoins(150L);
        Duende resultado = treeMapAdaptado.verMaisRico(duende1, duende2);

        assertEquals(duende2, resultado);
    }

    @Test
    void testVerMaisRicoComRiquezaIgual() {
        Duende duendeA = new Duende(15);
        duendeA.setCoins(100L);

        Duende duendeB = new Duende(16);
        duendeB.setCoins(100L);

        Duende resultado = treeMapAdaptado.verMaisRico(duendeA, duendeB);

        assertTrue(resultado == duendeA || resultado == duendeB);
    }

    @Test
    void testVerMaisRicoComNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            treeMapAdaptado.verMaisRico(null, duende1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            treeMapAdaptado.verMaisRico(duende1, null);
        });
    }
}
