package model.domain;

import model.domain.interfaces.EntityOnHorizon;
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
    @DisplayName("Teste de Domínio: Construtor deve criar um duende com estado inicial correto")
    void constructor_deveInicializarComValoresPadrao() {
        Duende duende = new Duende(5);
        assertThat(duende.getId()).isEqualTo(5);
        assertThat(duende.getCoins()).isEqualTo(1_000_000L);
        assertThat(duende.getPosition()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("Teste Estrutural: Deve adicionar moedas ao saldo do duende")
    void addCoins_deveAumentarTotalDeMoedas() {
        Duende duende = new Duende(1);
        duende.addCoins(500L);
        assertThat(duende.getCoins()).isEqualTo(1_000_500L);
    }

    @Test
    @DisplayName("Teste Estrutural: Deve perder metade das moedas ao ser roubado")
    void beingStealed_deveReduzirMoedasPelaMetadeERetornarValorPerdido() {
        Duende duende = new Duende(1);
        long moedasPerdidas = duende.beingStealed();
        assertThat(moedasPerdidas).isEqualTo(500_000L);
        assertThat(duende.getCoins()).isEqualTo(500_000L);
    }

    @Test
    @DisplayName("Teste de Interação: Deve roubar moedas de uma vítima e adicioná-las ao seu saldo")
    void steal_deveChamarBeingStealedDaVitimaEAdicionarMoedas() {
        Duende ladrao = new Duende(1);
        when(mockVictim.beingStealed()).thenReturn(200_000L);
        ladrao.steal(mockVictim);
        verify(mockVictim).beingStealed();
        assertThat(ladrao.getCoins()).isEqualTo(1_200_000L);
    }

    @Test
    @DisplayName("Teste de Fronteira: Deve lançar exceção ao definir posição negativa")
    void setPosition_comValorNegativo_lancaExcecao() {
        Duende duende = new Duende(1);
        assertThatThrownBy(() -> duende.setPosition(-10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Teste de Fronteira: Move não deve ultrapassar o horizonte máximo")
    void move_quandoCalculoExcedeHorizonte_posicaoFicaNoLimite() {
        when(mockRandom.nextDouble()).thenReturn(0.99); // gera um movimento positivo grande
        Duende duende = new Duende(1, mockRandom);
        duende.setPosition(500.0);
        duende.move(1000.0);
        assertThat(duende.getPosition()).isEqualTo(999.0);
    }

    @Test
    @DisplayName("Teste de Fronteira: Move não deve ter posição menor que zero")
    void move_quandoCalculoEhNegativo_posicaoFicaZero() {
        when(mockRandom.nextDouble()).thenReturn(0.01); // gera um movimento negativo grande
        Duende duende = new Duende(1, mockRandom);
        duende.setPosition(10.0);
        duende.move(1000.0);
        assertThat(duende.getPosition()).isZero();
    }
}
