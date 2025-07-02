package model.domain;

import model.domain.interfaces.EntityOnHorizon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterTest {

    // mocks dos duendes q formarão o cluster
    @Mock
    private Duende mockDuende1;
    @Mock
    private Duende mockDuende2;

    private Cluster cluster;

    @BeforeEach
    void setUp() {

        // comportamento padrão dos mocks antes de cada teste
        when(mockDuende1.getCoins()).thenReturn(1000L);
        when(mockDuende1.getPosition()).thenReturn(50.0);

        when(mockDuende2.getCoins()).thenReturn(2000L);

        // instancia do cluster q vai ser testada
        cluster = new Cluster(mockDuende1, mockDuende2);
    }

    @Test
    @DisplayName("Teste de Domínio: Construtor deve inicializar o cluster com os valores somados dos duendes")
    void constructor_deveInicializarComValoresSomados() {
        // verificando se os valores iniciais estão corretos
        assertThat(cluster.getQuantityDuendes()).isEqualTo(2);
        assertThat(cluster.getCoins()).isEqualTo(3000L); // 1000 + 2000
        assertThat(cluster.getPosition()).isEqualTo(50.0);
        assertThat(cluster.getDuendes()).containsExactlyInAnyOrder(mockDuende1, mockDuende2);
    }

    @Test
    @DisplayName("Teste Estrutural: Deve adicionar um novo duende ao cluster corretamente")
    void addToCluster_adicionandoDuende_atualizaEstadoDoCluster() {
        Duende duendeExtra = mock(Duende.class);
        when(duendeExtra.getCoins()).thenReturn(500L);

        cluster.addToCluster(duendeExtra);

        assertThat(cluster.getQuantityDuendes()).isEqualTo(3);
        assertThat(cluster.getCoins()).isEqualTo(3500L); // 3000 + 500
        assertThat(cluster.getDuendes()).contains(duendeExtra);
    }

    @Test
    @DisplayName("Teste Estrutural: Deve fundir um cluster com outro corretamente")
    void addToCluster_adicionandoOutroCluster_fundeOsDoisClusters() {
        Cluster outroCluster = mock(Cluster.class);
        when(outroCluster.getQuantityDuendes()).thenReturn(3);
        when(outroCluster.getCoins()).thenReturn(5000L);

        // retorna uma lista vazia pra simplificar, o importante aqui é a interaçao

        when(outroCluster.getDuendes()).thenReturn(new ArrayList<>());

        cluster.addToCluster(outroCluster);

        assertThat(cluster.getQuantityDuendes()).isEqualTo(5); // 2 + 3
        assertThat(cluster.getCoins()).isEqualTo(8000L); // 3000 + 5000
    }

    @Test
    @DisplayName("Teste de Fronteira: Deve lançar exceção ao adicionar um tipo de entidade inválido")
    void addToCluster_comTipoInvalido_lancaExcecao() {
        EntityOnHorizon entidadeInvalida = mock(EntityOnHorizon.class);

        assertThatThrownBy(() -> cluster.addToCluster(entidadeInvalida))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Teste de Domínio: Deve perder metade das moedas ao ser roubado")
    void beingStealed_deveReduzirMoedasPelaMetade() {

        long moedasPerdidas = cluster.beingStealed();

        assertThat(moedasPerdidas).isEqualTo(1500L); // Metade de 3000
        assertThat(cluster.getCoins()).isEqualTo(1500L); // O que sobrou
    }

    @Test
    @DisplayName("Teste de Interação: Deve interagir corretamente ao roubar outra entidade")
    void steal_chamaMetodosCorretosEAtualizaMoedas() {

        // simulando uma vitima que perde 500 moedas
        EntityOnHorizon vitima = mock(EntityOnHorizon.class);
        when(vitima.beingStealed()).thenReturn(500L);

        cluster.steal(vitima);

        verify(vitima).beingStealed(); // verifica se o método da vítima foi chamado
        assertThat(cluster.getCoins()).isEqualTo(3500L); // 3000 (inicial) + 500 (roubado)
    }

    @Test
    @DisplayName("Teste de Fronteira: Move não deve ultrapassar os limites do horizonte")
    void move_respeitaLimitesDoHorizonte() throws Exception {

        // injeta um mock de Random para controlar o resultado do movimento
        // (precisei criar outro construtor pra isso)

        Random mockRandom = mock(Random.class);
        // esse "Reflection" substitui o campo 'random' na instância do cluster
        java.lang.reflect.Field field = Cluster.class.getDeclaredField("random");
        field.setAccessible(true);
        field.set(cluster, mockRandom);

        //cenario 1 -
        // tentando mover pra mt alem do horizonte

        when(mockRandom.nextDouble()).thenReturn(0.99);

        cluster.move(100.0);

        assertThat(cluster.getPosition()).isEqualTo(99.0); // deve ser maxHorizon - 1

        //cenario 2 -
        //tenta mover pra uma posição negativa

        cluster.setPosition(10.0);
        when(mockRandom.nextDouble()).thenReturn(0.01);

        cluster.move(100.0);

        assertThat(cluster.getPosition()).isZero();
    }
}
