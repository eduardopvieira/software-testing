package dubles_de_teste;

import model.entities.GuardiaoDoHorizonte;
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
class GuardiaoDoHorizonteTest {

    @Mock
    private Random mockRandom;


    @Test
    @DisplayName("Deve lançar exceção se o ID for inválido")
    void constructor_comIdInvalido_lancaExcecao() {
        assertThatThrownBy(() -> new GuardiaoDoHorizonte(0, 100.0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GuardiaoDoHorizonte(-1, 100.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve ser criado com 0 moedas e na posição correta")
    void constructor_comDadosValidos_inicializaEstadoCorretamente() {
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 200.0);
        assertThat(guardiao.getId()).isEqualTo(1);
        assertThat(guardiao.getCoins()).isZero();
        assertThat(guardiao.getPosition()).isEqualTo(200.0);
    }


    @Test
    @DisplayName("beingStealed não deve alterar as moedas e deve retornar 0")
    void beingStealed_naoAlteraMoedasERetornaZero() {
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 100.0);
        guardiao.addCoins(5000L);
        long moedasPerdidas = guardiao.beingStealed();

        assertThat(moedasPerdidas).isZero();
        assertThat(guardiao.getCoins()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("steal não deve ter nenhum efeito")
    void steal_naoExecutaNenhumaAcao() {
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 100.0);
        EntityOnHorizon vitima = mock(EntityOnHorizon.class);
        guardiao.steal(vitima);

        verify(vitima, never()).beingStealed();
        assertThat(guardiao.getCoins()).isZero();
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar moedas negativas")
    void addCoins_comValorNegativo_lancaExcecao() {
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 100.0);
        assertThatThrownBy(() -> guardiao.addCoins(-100L))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("Move deve usar fator de 1M quando o guardião tem poucas moedas")
    void move_comPocasMoedas_usaFatorMinimo() throws Exception {
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 500.0);
        guardiao.addCoins(500L);
        when(mockRandom.nextDouble()).thenReturn(0.75); // r = 0.5
        setRandom(guardiao, mockRandom);

        guardiao.move(10_000_000.0);

        double expectedPosition = 500.0 + (0.5 * 1_000_000);
        assertThat(guardiao.getPosition()).isEqualTo(Math.round(expectedPosition * 10) / 10.0);
    }

    @Test
    @DisplayName("Move deve usar o valor real das moedas quando for alto")
    void move_comMuitasMoedas_usaFatorReal() throws Exception {
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 500.0);
        guardiao.addCoins(2_000_000L);
        when(mockRandom.nextDouble()).thenReturn(0.75);
        setRandom(guardiao, mockRandom);

        guardiao.move(10_000_000.0);

        double expectedPosition = 500.0 + (0.5 * 2_000_000);
        assertThat(guardiao.getPosition()).isEqualTo(Math.round(expectedPosition * 10) / 10.0);
    }

    @Test
    @DisplayName("Move não deve ultrapassar os limites do horizonte")
    void move_respeitaLimitesDoHorizonte() throws Exception {
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 50.0);
        guardiao.addCoins(2_000_000L);
        setRandom(guardiao, mockRandom);

        when(mockRandom.nextDouble()).thenReturn(0.99);
        guardiao.move(100.0);
        assertThat(guardiao.getPosition()).isEqualTo(99.0);

        guardiao.setPosition(50.0);
        when(mockRandom.nextDouble()).thenReturn(0.01);
        guardiao.move(100.0);
        assertThat(guardiao.getPosition()).isZero();
    }

    private void setRandom(GuardiaoDoHorizonte guardiao, Random mockRandom) throws Exception {
        java.lang.reflect.Field field = GuardiaoDoHorizonte.class.getDeclaredField("random");
        field.setAccessible(true);
        field.set(guardiao, mockRandom);
    }
}
