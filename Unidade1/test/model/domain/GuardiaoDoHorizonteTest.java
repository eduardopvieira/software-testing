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
class GuardiaoDoHorizonteTest {

    @Mock
    private Random mockRandom;

    //testando construtor e estado inicial

    @Test
    @DisplayName("Teste de Fronteira: Deve lançar exceção se o ID for inválido")
    void constructor_comIdInvalido_lancaExcecao() {
        // testando ids validos (n pode ser zero ou negativo)
        assertThatThrownBy(() -> new GuardiaoDoHorizonte(0, 100.0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new GuardiaoDoHorizonte(-1, 100.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Teste de Domínio: Deve ser criado com 0 moedas e na posição inicial correta")
    void constructor_comDadosValidos_inicializaEstadoCorretamente() {

        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 200.0);

        assertThat(guardiao.getId()).isEqualTo(1);
        assertThat(guardiao.getCoins()).isZero();
        assertThat(guardiao.getPosition()).isEqualTo(200.0);
    }

    // testes de interaçao:

    @Test
    @DisplayName("Teste de Domínio: beingStealed não deve alterar as moedas e deve retornar 0")
    void beingStealed_naoAlteraMoedasERetornaZero() {

        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 100.0);
        guardiao.addCoins(5000L); // Dá moedas ao guardião

        long moedasPerdidas = guardiao.beingStealed();

        assertThat(moedasPerdidas).isZero();
        assertThat(guardiao.getCoins()).isEqualTo(5000L); // saldo nao mudou
    }

    @Test
    @DisplayName("Teste de Interação: steal não deve ter nenhum efeito")
    void steal_naoExecutaNenhumaAcao() {

        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 100.0);
        EntityOnHorizon vitima = mock(EntityOnHorizon.class); // criando um duble pra vitima

        guardiao.steal(vitima);

        verify(vitima, never()).beingStealed(); // metodo da vitima nunca foi chamado
        assertThat(guardiao.getCoins()).isZero();
    }

    // testando o movimento (metdoo move())

    @Test
    @DisplayName("Teste Estrutural: Move deve usar fator de 1M quando o guardião tem poucas moedas")
    void move_comPocasMoedas_usaFatorMinimo() throws Exception {
        // guardião com 500 moedas
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 500.0);
        guardiao.addCoins(500L);

        // injetando o mock de random pra manipular resultados
        when(mockRandom.nextDouble()).thenReturn(0.75); // resulta em r = 0.5
        setRandom(guardiao, mockRandom);

        guardiao.move(10000000.0);

        // posição esperada = 500.0 + 0.5 * 1000000 = 500500.0
        double expectedPosition = 500.0 + (0.5 * 1_000_000);
        assertThat(guardiao.getPosition()).isEqualTo(Math.round(expectedPosition * 10) / 10.0);
    }

    @Test
    @DisplayName("Teste Estrutural: Move deve usar o valor real das moedas quando for alto")
    void move_comMuitasMoedas_usaFatorReal() throws Exception {
        GuardiaoDoHorizonte guardiao = new GuardiaoDoHorizonte(1, 500.0);
        guardiao.addCoins(2_000_000L);

        when(mockRandom.nextDouble()).thenReturn(0.75); // r = 0.5
        setRandom(guardiao, mockRandom);

        guardiao.move(10000000.0);

        // posição esperada = 500.0 + 0.5 * 2000000 = 1000500.0
        double expectedPosition = 500.0 + (0.5 * 2_000_000);
        assertThat(guardiao.getPosition()).isEqualTo(Math.round(expectedPosition * 10) / 10.0);
    }

    //metodo auxiliar pra poder setar o random com algum resultado previsivel
    private void setRandom(GuardiaoDoHorizonte guardiao, Random mockRandom) throws Exception {
        java.lang.reflect.Field field = GuardiaoDoHorizonte.class.getDeclaredField("random");
        field.setAccessible(true);
        field.set(guardiao, mockRandom);
    }
}
