package model.domain;

import model.domain.interfaces.EntityOnHorizon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterTest {

    @Mock
    private Duende mockDuende1;
    @Mock
    private Duende mockDuende2;

    private Cluster cluster;

    @BeforeEach
    void setUp() {
        when(mockDuende1.getCoins()).thenReturn(1000L);
        when(mockDuende1.getPosition()).thenReturn(50.0);
        when(mockDuende2.getCoins()).thenReturn(2000L);
        cluster = new Cluster(mockDuende1, mockDuende2);
    }

    @Test
    @DisplayName("Construtor deve inicializar o cluster com os valores somados dos duendes")
    void constructor_deveInicializarComValoresSomados() {
        assertThat(cluster.getQuantityDuendes()).isEqualTo(2);
        assertThat(cluster.getCoins()).isEqualTo(3000L);
        assertThat(cluster.getPosition()).isEqualTo(50.0);
        assertThat(cluster.getDuendes()).containsExactlyInAnyOrder(mockDuende1, mockDuende2);
    }

    @Test
    @DisplayName("Deve adicionar um novo duende ao cluster corretamente")
    void addToCluster_adicionandoDuende_atualizaEstadoDoCluster() {
        Duende duendeExtra = mock(Duende.class);
        when(duendeExtra.getCoins()).thenReturn(500L);
        cluster.addToCluster(duendeExtra);

        assertThat(cluster.getQuantityDuendes()).isEqualTo(3);
        assertThat(cluster.getCoins()).isEqualTo(3500L);
        assertThat(cluster.getDuendes()).contains(duendeExtra);
    }

    @Test
    @DisplayName("Deve fundir um cluster com outro corretamente")
    void addToCluster_adicionandoOutroCluster_fundeOsDoisClusters() {
        Cluster outroCluster = mock(Cluster.class);
        when(outroCluster.getQuantityDuendes()).thenReturn(3);
        when(outroCluster.getCoins()).thenReturn(5000L);
        when(outroCluster.getDuendes()).thenReturn(new ArrayList<>());
        cluster.addToCluster(outroCluster);

        assertThat(cluster.getQuantityDuendes()).isEqualTo(5);
        assertThat(cluster.getCoins()).isEqualTo(8000L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar um tipo de entidade inválido")
    void addToCluster_comTipoInvalido_lancaExcecao() {
        EntityOnHorizon entidadeInvalida = mock(EntityOnHorizon.class);
        assertThatThrownBy(() -> cluster.addToCluster(entidadeInvalida))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar definir uma posição negativa")
    void setPosition_comValorNegativo_lancaExcecao() {
        assertThatThrownBy(() -> cluster.setPosition(-1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A posição não pode ser negativa.");
    }

    @Test
    @DisplayName("Deve perder metade das moedas ao ser roubado")
    void beingStealed_deveReduzirMoedasPelaMetade() {
        long moedasPerdidas = cluster.beingStealed();
        assertThat(moedasPerdidas).isEqualTo(1500L);
        assertThat(cluster.getCoins()).isEqualTo(1500L);
    }

    @Test
    @DisplayName("Deve interagir corretamente ao roubar outra entidade")
    void steal_chamaMetodosCorretosEAtualizaMoedas() {
        EntityOnHorizon vitima = mock(EntityOnHorizon.class);
        when(vitima.beingStealed()).thenReturn(500L);
        cluster.steal(vitima);

        verify(vitima).beingStealed();
        assertThat(cluster.getCoins()).isEqualTo(3500L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar roubar de uma vítima nula ou de si mesmo")
    void steal_comVitimaInvalida_lancaExcecao() {
        assertThatThrownBy(() -> cluster.steal(null)) //victim == null
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> cluster.steal(cluster)) // victim == this
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar moedas negativas")
    void addCoins_comValorNegativo_lancaExcecao() {
        assertThatThrownBy(() -> cluster.addCoins(-100L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve respeitar os limites do horizonte ao se mover")
    void move_respeitaLimitesDoHorizonte() throws Exception {
        Random mockRandom = mock(Random.class);
        java.lang.reflect.Field field = Cluster.class.getDeclaredField("random");
        field.setAccessible(true);
        field.set(cluster, mockRandom);

        when(mockRandom.nextDouble()).thenReturn(0.99);
        cluster.move(100.0);
        assertThat(cluster.getPosition()).isEqualTo(99.0);

        cluster.setPosition(10.0);
        when(mockRandom.nextDouble()).thenReturn(0.01);
        cluster.move(100.0);
        assertThat(cluster.getPosition()).isZero();
    }
}
