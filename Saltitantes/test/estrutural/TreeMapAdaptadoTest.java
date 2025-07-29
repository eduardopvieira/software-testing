package estrutural;

import model.datastructure.TreeMapAdaptado;
import model.entities.Cluster;
import model.entities.Duende;
import model.entities.GuardiaoDoHorizonte;
import model.entities.interfaces.EntityOnHorizon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreeMapAdaptadoTest {

    private TreeMapAdaptado treeMapAdaptado;

    @Mock
    private Duende mockDuende1;
    @Mock
    private Duende mockDuende2;
    @Mock
    private Cluster mockCluster;
    @Mock
    private GuardiaoDoHorizonte mockGuardiao;

    @BeforeEach
    void setUp() {
        treeMapAdaptado = new TreeMapAdaptado();
    }


    @Test
    @DisplayName("Deve lançar exceção ao adicionar entidade nula")
    void comEntidadeNula_lancaExcecao() {
        assertThatThrownBy(() -> treeMapAdaptado.addDuendeInicial(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Teste Estrutural: Deve adicionar duende em posição deslocada se a original estiver ocupada")
    void addDuendeInicial_quandoPosicaoOcupada_adicionaEmPosicaoDeslocada() {
        when(mockDuende1.getPosition()).thenReturn(10.0);

        final double[] positionHolder = {10.0};

        when(mockDuende2.getPosition()).thenAnswer(invocation -> positionHolder[0]);

        doAnswer(invocation -> {
            positionHolder[0] = invocation.getArgument(0);
            return null;
        }).when(mockDuende2).setPosition(anyDouble());

        treeMapAdaptado.addDuendeInicial(mockDuende1);
        treeMapAdaptado.addDuendeInicial(mockDuende2);

        verify(mockDuende2).setPosition(10.1);

        assertThat(treeMapAdaptado.treeMapPrincipal)
                .hasSize(2)
                .containsKeys(10.0, 10.1)
                .containsValues(mockDuende1, mockDuende2);
    }


    @Test
    @DisplayName("Deve retornar nulo se houver menos de duas entidades")
    void comMenosDeDuasEntidades_retornaNulo() {
        treeMapAdaptado.treeMapPrincipal.put(10.0, mockDuende1);
        assertThat(treeMapAdaptado.findNearestEntidade(mockDuende1)).isNull();
    }

    @Test
    @DisplayName("Deve encontrar o vizinho mais próximo à direita")
    void comVizinhoMaisProximoADireita_retornaVizinhoCorreto() {
        when(mockDuende1.getPosition()).thenReturn(50.0);
        Duende vizinhoEsquerdo = mock(Duende.class);
        Duende vizinhoDireito = mock(Duende.class);
        treeMapAdaptado.treeMapPrincipal.put(50.0, mockDuende1);
        treeMapAdaptado.treeMapPrincipal.put(10.0, vizinhoEsquerdo);
        treeMapAdaptado.treeMapPrincipal.put(60.0, vizinhoDireito);

        assertThat(treeMapAdaptado.findNearestEntidade(mockDuende1)).isEqualTo(vizinhoDireito);
    }

    @Test
    @DisplayName("Deve retornar nulo se os únicos vizinhos forem guardiões")
    void quandoUnicosVizinhosSaoGuardioes_retornaNulo() {
        when(mockDuende1.getPosition()).thenReturn(50.0);
        treeMapAdaptado.treeMapPrincipal.put(50.0, mockDuende1);
        treeMapAdaptado.treeMapPrincipal.put(40.0, mockGuardiao);

        assertThat(treeMapAdaptado.findNearestEntidade(mockDuende1)).isNull();
    }

    @Test
    @DisplayName("Deve ignorar múltiplos guardiões e encontrar o vizinho válido")
    void quandoHaMultiplosGuardioes_ignoraTodosEEncontraProximo() {
        when(mockDuende1.getPosition()).thenReturn(50.0);
        GuardiaoDoHorizonte outroGuardiao = mock(GuardiaoDoHorizonte.class);
        Duende vizinhoValido = mock(Duende.class);

        treeMapAdaptado.treeMapPrincipal.put(10.0, vizinhoValido);
        treeMapAdaptado.treeMapPrincipal.put(40.0, outroGuardiao);
        treeMapAdaptado.treeMapPrincipal.put(50.0, mockDuende1);
        treeMapAdaptado.treeMapPrincipal.put(60.0, mockGuardiao);

        assertThat(treeMapAdaptado.findNearestEntidade(mockDuende1)).isEqualTo(vizinhoValido);
    }


    @Test
    @DisplayName("Deve lançar exceção ao buscar vizinho para entidade nula")
    void findNearestEntidade_comEntidadeNula_lancaExcecao() {
        assertThatThrownBy(() -> treeMapAdaptado.findNearestEntidade(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve retornar o vizinho da direita se não houver vizinho à esquerda")
    void findNearestEntidade_semVizinhoAEsquerda_retornaVizinhoDaDireita() {
        when(mockDuende1.getPosition()).thenReturn(10.0);

        treeMapAdaptado.treeMapPrincipal.put(10.0, mockDuende1);
        treeMapAdaptado.treeMapPrincipal.put(20.0, mockDuende2);

        EntityOnHorizon maisProximo = treeMapAdaptado.findNearestEntidade(mockDuende1);

        assertThat(maisProximo).isEqualTo(mockDuende2);
    }

    @Test
    @DisplayName("Deve retornar o vizinho da esquerda se não houver vizinho à direita")
    void findNearestEntidade_semVizinhoADireita_retornaVizinhoDaEsquerda() {
        when(mockDuende1.getPosition()).thenReturn(30.0);

        treeMapAdaptado.treeMapPrincipal.put(10.0, mockDuende2);
        treeMapAdaptado.treeMapPrincipal.put(30.0, mockDuende1);

        EntityOnHorizon maisProximo = treeMapAdaptado.findNearestEntidade(mockDuende1);

        assertThat(maisProximo).isEqualTo(mockDuende2);
    }

    @Test
    @DisplayName("Deve retornar a entidade com mais moedas")
    void comMoedasDiferentes_retornaEntidadeComMaiorValor() {
        when(mockDuende1.getCoins()).thenReturn(100L);
        when(mockDuende2.getCoins()).thenReturn(200L);
        assertThat(treeMapAdaptado.verMaisRico(mockDuende1, mockDuende2)).isEqualTo(mockDuende2);
    }

    @Test
    @DisplayName("Deve retornar uma das duas entidades se as moedas forem iguais")
    void comMoedasIguais_retornaUmaDasDuas() {
        when(mockDuende1.getCoins()).thenReturn(500L);
        when(mockDuende2.getCoins()).thenReturn(500L);
        EntityOnHorizon resultado = treeMapAdaptado.verMaisRico(mockDuende1, mockDuende2);
        assertThat(resultado).isIn(mockDuende1, mockDuende2);
    }


    @Test
    @DisplayName("Deve formatar o nome para cada tipo de entidade e para tipos desconhecidos")
    void paraCadaTipoDeEntidade_retornaStringCorreta() {
        when(mockDuende1.getId()).thenReturn(7);
        when(mockCluster.getQuantityDuendes()).thenReturn(5);
        when(mockGuardiao.getId()).thenReturn(99);
        EntityOnHorizon entidadeDesconhecida = mock(EntityOnHorizon.class);

        assertThat(TreeMapAdaptado.getNomeEntidade(mockDuende1)).isEqualTo("Duende 7");
        assertThat(TreeMapAdaptado.getNomeEntidade(mockCluster)).isEqualTo("Cluster com 5 duendes");
        assertThat(TreeMapAdaptado.getNomeEntidade(mockGuardiao)).isEqualTo("Guardião 99");
        assertThat(TreeMapAdaptado.getNomeEntidade(entidadeDesconhecida)).isEqualTo("Entidade desconhecida");
        assertThat(TreeMapAdaptado.getNomeEntidade(null)).isEqualTo("Entidade desconhecida");
    }
}
