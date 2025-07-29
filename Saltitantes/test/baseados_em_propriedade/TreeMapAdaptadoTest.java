package baseados_em_propriedade;

import model.datastructure.TreeMapAdaptado;
import model.entities.Duende;
import model.entities.interfaces.EntityOnHorizon;
import net.jqwik.api.*;
import net.jqwik.api.Label;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreeMapAdaptadoTest {

    @Property
    @Label("verMaisRico deve sempre retornar a entidade com mais moedas")
    void verMaisRicoProperty(@ForAll long coinsA, @ForAll long coinsB) {
        Assume.that(coinsA != coinsB);

        EntityOnHorizon mockA = mock(EntityOnHorizon.class);
        EntityOnHorizon mockB = mock(EntityOnHorizon.class);
        when(mockA.getCoins()).thenReturn(coinsA);
        when(mockB.getCoins()).thenReturn(coinsB);
        TreeMapAdaptado tma = new TreeMapAdaptado();

        EntityOnHorizon expectedWinner = coinsA > coinsB ? mockA : mockB;

        EntityOnHorizon actualWinner = tma.verMaisRico(mockA, mockB);

        assertThat(actualWinner).isEqualTo(expectedWinner);
    }

    @Property
    @Label("verMaisRico com empate deve retornar uma das duas entidades")
    void verMaisRicoTieProperty(@ForAll long coins) {
        EntityOnHorizon mockA = mock(EntityOnHorizon.class);
        EntityOnHorizon mockB = mock(EntityOnHorizon.class);
        when(mockA.getCoins()).thenReturn(coins);
        when(mockB.getCoins()).thenReturn(coins);
        TreeMapAdaptado tma = new TreeMapAdaptado();

        EntityOnHorizon winner = tma.verMaisRico(mockA, mockB);

        assertThat(winner).isIn(mockA, mockB);
    }

    @Property
    @Label("addDuendeInicial deve sempre aumentar o tamanho do mapa em 1")
    void addDuendeInicialSizeInvariant(@ForAll("initialDuendes") Duende[] initialDuendes, @ForAll("duende") Duende duendeToAdd) {
        TreeMapAdaptado tma = new TreeMapAdaptado();
        for (Duende d : initialDuendes) {
            tma.addDuendeInicial(d);
        }
        int initialSize = tma.treeMapPrincipal.size();

        tma.addDuendeInicial(duendeToAdd);

        assertThat(tma.treeMapPrincipal.size()).isEqualTo(initialSize + 1);
    }

    // gera duendes pros testes
    @Provide
    Arbitrary<Duende> duende() {
        return Arbitraries.integers().between(1, 1000).map(Duende::new);
    }

    //gera um array de duendes para o teste addDuendeInicialSizeInvariant
    @Provide
    Arbitrary<Duende[]> initialDuendes() {
        return duende().array(Duende[].class).ofSize(5);
    }
}
