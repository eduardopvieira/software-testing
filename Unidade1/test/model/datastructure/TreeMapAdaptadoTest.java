package model.datastructure;

import model.domain.Duende;
import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;
import model.domain.interfaces.EntityOnHorizon;
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
    private GuardiaoDoHorizonte mockGuardiao;

    @BeforeEach
    void setUp() {
        treeMapAdaptado = new TreeMapAdaptado();
    }


    @Test
    @DisplayName("Teste de Fronteira: Deve lançar exceção ao adicionar entidade nula")
    void addDuendeInicial_comEntidadeNula_lancaExcecao() {
        assertThatThrownBy(() -> treeMapAdaptado.addDuendeInicial(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entidade não pode ser nula.");
    }

    @Test
    @DisplayName("Teste Estrutural: Deve adicionar duende em posição deslocada se a original estiver ocupada")
    void addDuendeInicial_quandoPosicaoOcupada_adicionaEmPosicaoDeslocada() {
        when(mockDuende1.getPosition()).thenReturn(10.0);

        when(mockDuende2.getPosition()).thenReturn(10.0, 10.1);

        treeMapAdaptado.addDuendeInicial(mockDuende1);
        treeMapAdaptado.addDuendeInicial(mockDuende2);

        verify(mockDuende2).setPosition(10.1);

        assertThat(treeMapAdaptado.treeMapPrincipal).hasSize(2);
        assertThat(treeMapAdaptado.treeMapPrincipal.get(10.0)).isEqualTo(mockDuende1);
        assertThat(treeMapAdaptado.treeMapPrincipal.get(10.1)).isEqualTo(mockDuende2);
    }


    @Test
    @DisplayName("Teste de Fronteira: Deve retornar nulo se houver menos de duas entidades")
    void findNearestEntidade_comMenosDeDuasEntidades_retornaNulo() {

        treeMapAdaptado.treeMapPrincipal.put(10.0, mockDuende1);

        //garantir que é null
        assertThat(treeMapAdaptado.findNearestEntidade(mockDuende1)).isNull();
    }

    @Test
    @DisplayName("Teste Estrutural: Deve encontrar o vizinho mais próximo à esquerda")
    void findNearestEntidade_comVizinhoMaisProximoAEsquerda_retornaVizinhoCorreto() {

        when(mockDuende1.getPosition()).thenReturn(50.0);

        Duende vizinhoEsquerdo = mock(Duende.class);
        Duende vizinhoDireito = mock(Duende.class);

        treeMapAdaptado.treeMapPrincipal.put(50.0, mockDuende1);
        treeMapAdaptado.treeMapPrincipal.put(40.0, vizinhoEsquerdo);
        treeMapAdaptado.treeMapPrincipal.put(80.0, vizinhoDireito);

        EntityOnHorizon maisProximo = treeMapAdaptado.findNearestEntidade(mockDuende1);

        assertThat(maisProximo).isEqualTo(vizinhoEsquerdo);
    }

    @Test
    @DisplayName("Teste Estrutural: Deve ignorar um Guardião e encontrar o próximo vizinho válido")
    void findNearestEntidade_quandoVizinhoProximoEhGuardiao_ignoraEEncontraProximo() {

        when(mockDuende1.getPosition()).thenReturn(50.0);

        Duende vizinhoValido = mock(Duende.class);

        treeMapAdaptado.treeMapPrincipal.put(50.0, mockDuende1);
        treeMapAdaptado.treeMapPrincipal.put(60.0, mockGuardiao);
        treeMapAdaptado.treeMapPrincipal.put(90.0, vizinhoValido);

        EntityOnHorizon maisProximo = treeMapAdaptado.findNearestEntidade(mockDuende1);

        assertThat(maisProximo).isEqualTo(vizinhoValido);
    }

    @Test
    @DisplayName("Teste Estrutural: Deve usar verMaisRico como desempate para vizinhos equidistantes")
    void findNearestEntidade_quandoVizinhosEquidistantes_retornaOMaisRico() {
        when(mockDuende1.getPosition()).thenReturn(50.0);
        Duende vizinhoPobre = mock(Duende.class);
        when(vizinhoPobre.getCoins()).thenReturn(100L);
        Duende vizinhoRico = mock(Duende.class);
        when(vizinhoRico.getCoins()).thenReturn(1000L);

        treeMapAdaptado.treeMapPrincipal.put(50.0, mockDuende1);
        treeMapAdaptado.treeMapPrincipal.put(40.0, vizinhoPobre);
        treeMapAdaptado.treeMapPrincipal.put(60.0, vizinhoRico);

        EntityOnHorizon maisProximo = treeMapAdaptado.findNearestEntidade(mockDuende1);

        assertThat(maisProximo).isEqualTo(vizinhoRico);
    }

    @Test
    @DisplayName("Teste de Domínio: verMaisRico deve retornar a entidade com mais moedas")
    void verMaisRico_comMoedasDiferentes_retornaEntidadeComMaiorValor() {
        when(mockDuende1.getCoins()).thenReturn(100L);
        when(mockDuende2.getCoins()).thenReturn(200L);

        assertThat(treeMapAdaptado.verMaisRico(mockDuende1, mockDuende2)).isEqualTo(mockDuende2);
        assertThat(treeMapAdaptado.verMaisRico(mockDuende2, mockDuende1)).isEqualTo(mockDuende2);
    }
}
