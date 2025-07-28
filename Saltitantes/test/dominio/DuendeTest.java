package dominio;

import model.entities.Duende;
import model.entities.interfaces.EntityOnHorizon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DuendeTest {

    @Mock
    private Random mockRandom;
    @Mock
    private EntityOnHorizon mockVictim;

    @Test
    @DisplayName("Construtor deve criar um duende com estado inicial correto")
    void constructor_deveInicializarComValoresPadrao() {
        Duende duende = new Duende(5);
        assertThat(duende.getId()).isEqualTo(5);
        assertThat(duende.getCoins()).isEqualTo(1_000_000L);
        assertThat(duende.getPosition()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("Deve adicionar moedas ao saldo do duende")
    void addCoins_deveAumentarTotalDeMoedas() {
        Duende duende = new Duende(1);
        duende.addCoins(500L);
        assertThat(duende.getCoins()).isEqualTo(1_000_500L);
    }

    @Test
    @DisplayName("Deve perder metade das moedas ao ser roubado")
    void beingStealed_deveReduzirMoedasPelaMetadeERetornarValorPerdido() {
        Duende duende = new Duende(1);
        long moedasPerdidas = duende.beingStealed();
        assertThat(moedasPerdidas).isEqualTo(500_000L);
        assertThat(duende.getCoins()).isEqualTo(500_000L);
    }

    @Test
    @DisplayName("Deve roubar moedas de uma vítima válida")
    void steal_deveChamarBeingStealedDaVitimaEAdicionarMoedas() {
        Duende ladrao = new Duende(1);
        when(mockVictim.beingStealed()).thenReturn(200_000L);
        ladrao.steal(mockVictim);

        verify(mockVictim).beingStealed();
        assertThat(ladrao.getCoins()).isEqualTo(1_200_000L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar roubar de vítima nula ou de si mesmo")
    void steal_comVitimaInvalida_lancaExcecao() {
        Duende duende = new Duende(1);

        assertThatThrownBy(() -> duende.steal(null)) // victim == null
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> duende.steal(duende)) // victim == this
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao definir posição negativa")
    void setPosition_comValorNegativo_lancaExcecao() {
        Duende duende = new Duende(1);
        assertThatThrownBy(() -> duende.setPosition(-10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Move não deve ultrapassar o horizonte máximo")
    void move_quandoCalculoExcedeHorizonte_posicaoFicaNoLimite() {
        when(mockRandom.nextDouble()).thenReturn(0.99);
        Duende duende = new Duende(1, mockRandom);
        duende.setPosition(500.0);
        duende.move(1000.0);
        assertThat(duende.getPosition()).isEqualTo(999.0);
    }

    @Test
    @DisplayName("Move não deve ter posição menor que zero")
    void move_quandoCalculoEhNegativo_posicaoFicaZero() {
        when(mockRandom.nextDouble()).thenReturn(0.01);
        Duende duende = new Duende(1, mockRandom);
        duende.setPosition(10.0);
        duende.move(1000.0);
        assertThat(duende.getPosition()).isZero();
    }
}
