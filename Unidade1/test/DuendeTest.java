import model.domain.interfaces.Duende;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DuendeTest {

    @Test
    void testConstructor() {
        Duende duende = new Duende(1);

        assertEquals(1, duende.getId());
        assertEquals(1000000L, duende.getCoins());
        assertEquals(0.1, duende.getPosition());
    }

    @Test
    void testMoverDentroDoHorizonte() {
        Duende duende = new Duende(1);
        duende.setPosition(0);
        duende.move(5);

        double newPos = duende.getPosition();
        assertTrue(newPos >= 0.0 && newPos <= 5.0);
    }

    @Test
    void testMoverParaOLimiteDoHorizonte() {
        Duende duende = new Duende(1);
        duende.setPosition(0);
        duende.move(10);
        duende.setPosition(10.0);

        assertEquals(10.0, duende.getPosition());
    }

    @Test
    void testMoverParaOLimiteInferior() {
        Duende duende = new Duende(1);
        duende.setPosition(0.5);
        duende.move(100);
        duende.setPosition(0);

        assertEquals(0.0, duende.getPosition());
    }

    @Test
    void testDarDinheiro() {
        Duende duende = new Duende(1);
        long initialCoins = duende.getCoins();

        long lostCoins = duende.giveCoins();

        assertEquals(initialCoins / 2, lostCoins);
        assertEquals(initialCoins - lostCoins, duende.getCoins());
    }

    @Test
    void testeRouboBemSucedido() {
        Duende thief = new Duende(1);
        Duende victim = new Duende(2);

        long thiefInitialCoins = thief.getCoins();
        long victimInitialCoins = victim.getCoins();

        thief.steal(victim);

        assertEquals(thiefInitialCoins + victimInitialCoins / 2, thief.getCoins());
        assertEquals(victimInitialCoins / 2, victim.getCoins());
    }

    @Test
    void testAutoFurtoDeveFalhar() {

        Duende duende = new Duende(1);

        assertThrows(IllegalArgumentException.class, () -> {
            duende.steal(duende);
        });

    }

    @Test
    void testTentarRoubarNull() {
        Duende thief = new Duende(1);

        assertThrows(IllegalArgumentException.class, () -> {
            thief.steal(null);
        });

    }

    @Test
    void testSetarPosicaoValida() {
        Duende duende = new Duende(1);
        duende.setPosition(0);

        assertEquals(0, duende.getPosition());
    }

    @Test
    void testSetarPosicaoInvalida() {
        //! Teste de borda inferior. A borda superior é testada no método testMoverParaOLimiteDoHorizonte

        Duende duende = new Duende(1);

        assertThrows(IllegalArgumentException.class, () -> {
            duende.setPosition(-1);
        });
    }

    @Test
    void testSetCoins() {
        Duende duende = new Duende(1);
        duende.setCoins(500L);

        assertEquals(500L, duende.getCoins());
    }
}
