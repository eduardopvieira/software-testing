package controller;

import Controller.SimulationEngine;
import model.domain.Cluster;
import model.domain.Duende;
import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;
import model.domain.interfaces.EntityOnHorizon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationEngineTest {

    @Mock
    private TreeMapAdaptado mockTma;
    @Mock
    private TreeMap<Double, EntityOnHorizon> mockTreeMap;

    @Mock
    private Duende mockDuende1;
    @Mock
    private Duende mockDuende2;
    @Mock
    private Cluster mockCluster1;
    @Mock
    private Cluster mockCluster2;
    @Mock
    private GuardiaoDoHorizonte mockGuardiao;

    private SimulationEngine simulationEngine;

    @BeforeEach
    void setUp() {
        mockTma.treeMapPrincipal = mockTreeMap;
        simulationEngine = new SimulationEngine(mockTma, 1000.0);
    }


    @Test
    @DisplayName("Deve retornar 1 (Vitória) quando restam 2 entidades")
    void verificarCondicaoDeTermino_Com2Entidades_Retorna1() {
        when(mockTreeMap.size()).thenReturn(2);
        assertThat(simulationEngine.verificarCondicaoDeTermino()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve retornar -1 (Derrota) quando moedas do guardião superam as outras")
    void verificarCondicaoDeTermino_ComGuardiãoMaisRico_RetornaMenos1() {
        when(mockTreeMap.size()).thenReturn(3);
        when(mockGuardiao.getCoins()).thenReturn(1001L);
        when(mockDuende1.getCoins()).thenReturn(500L);
        when(mockDuende2.getCoins()).thenReturn(500L);
        when(mockTreeMap.values()).thenReturn(List.of(mockGuardiao, mockDuende1, mockDuende2));
        assertThat(simulationEngine.verificarCondicaoDeTermino()).isEqualTo(-1);
    }


    @Test
    @DisplayName("Deve retornar 0 (Continuar) quando nenhuma condição de parada é atingida")
    void verificarCondicaoDeTermino_quandoNenhumaCondicaoAtendida_retorna0() {
        when(mockTreeMap.size()).thenReturn(3);
        when(mockGuardiao.getCoins()).thenReturn(500L);
        when(mockDuende1.getCoins()).thenReturn(1000L);
        when(mockDuende2.getCoins()).thenReturn(1000L);
        List<EntityOnHorizon> entidades = List.of(mockGuardiao, mockDuende1, mockDuende2);
        when(mockTreeMap.values()).thenReturn(entidades);
        int status = simulationEngine.verificarCondicaoDeTermino();
        assertThat(status).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve mover duende para posição vazia e não chamar roubo")
    void executarRodada_quandoMoveParaEspacoVazio_apenasMove() {
        when(mockDuende1.getPosition()).thenReturn(10.0, 15.0);
        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(10.0)));
        when(mockTreeMap.get(10.0)).thenReturn(mockDuende1);
        when(mockTreeMap.get(15.0)).thenReturn(null);
        when(mockTma.findNearestEntidade(mockDuende1)).thenReturn(null);

        simulationEngine.executarRodada();

        verify(mockDuende1, never()).steal(any(EntityOnHorizon.class));
        verify(mockDuende1).move(1000.0);
        verify(mockTreeMap).put(15.0, mockDuende1);
    }

    @Test
    @DisplayName("Deve criar um Cluster quando dois Duendes colidem")
    void executarRodada_quandoDuendeColideComDuende_criaCluster() {
        when(mockDuende1.getPosition()).thenReturn(10.0);
        when(mockDuende2.getPosition()).thenReturn(20.0);
        doAnswer(invocation -> {
            when(mockDuende1.getPosition()).thenReturn(20.0);
            return null;
        }).when(mockDuende1).move(anyDouble());

        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(10.0, 20.0)));
        when(mockTreeMap.get(10.0)).thenReturn(mockDuende1);
        when(mockTreeMap.get(20.0)).thenReturn(mockDuende2);

        simulationEngine.executarRodada();

        ArgumentCaptor<EntityOnHorizon> captor = ArgumentCaptor.forClass(EntityOnHorizon.class);
        verify(mockTreeMap, atLeastOnce()).put(eq(20.0), captor.capture());
        assertThat(captor.getValue()).isInstanceOf(Cluster.class);
    }

    @Test
    @DisplayName("Deve absorver moedas quando o Guardião colide com um Duende")
    void executarRodada_quandoGuardiãoColideComDuende_absorveDuende() {
        when(mockGuardiao.getPosition()).thenReturn(50.0);
        when(mockDuende1.getPosition()).thenReturn(60.0);
        doAnswer(invocation -> {
            when(mockGuardiao.getPosition()).thenReturn(60.0);
            return null;
        }).when(mockGuardiao).move(anyDouble());
        when(mockDuende1.getCoins()).thenReturn(5000L);
        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(50.0, 60.0)));
        when(mockTreeMap.get(50.0)).thenReturn(mockGuardiao);
        when(mockTreeMap.get(60.0)).thenReturn(mockDuende1);
        simulationEngine.executarRodada();
        verify(mockGuardiao).addCoins(5000L);
        verify(mockTreeMap, atLeastOnce()).put(eq(60.0), eq(mockGuardiao));
    }

    @Test
    @DisplayName("Deve fundir dois clusters quando um colide com o outro")
    void executarRodada_quandoClusterColideComCluster_deveFundirClusters() {
        when(mockCluster1.getPosition()).thenReturn(30.0);
        when(mockCluster2.getPosition()).thenReturn(40.0);

        doAnswer(invocation -> {
            when(mockCluster1.getPosition()).thenReturn(40.0);
            return null;
        }).when(mockCluster1).move(anyDouble());

        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(30.0, 40.0)));
        when(mockTreeMap.get(30.0)).thenReturn(mockCluster1);
        when(mockTreeMap.get(40.0)).thenReturn(mockCluster2);
        simulationEngine.executarRodada();
        verify(mockCluster1).addToCluster(mockCluster2);
        verify(mockTreeMap).put(40.0, mockCluster1);
    }

    @Test
    @DisplayName("Deve adicionar um duende a um cluster existente após colisão")
    void executarRodada_quandoClusterColideComDuende_deveAdicionarDuendeAoCluster() {
        when(mockCluster1.getPosition()).thenReturn(70.0);
        when(mockDuende1.getPosition()).thenReturn(80.0);

        doAnswer(invocation -> {
            when(mockCluster1.getPosition()).thenReturn(80.0);
            return null;
        }).when(mockCluster1).move(anyDouble());

        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(70.0, 80.0)));
        when(mockTreeMap.get(70.0)).thenReturn(mockCluster1);
        when(mockTreeMap.get(80.0)).thenReturn(mockDuende1);
        simulationEngine.executarRodada();
        verify(mockCluster1).addToCluster(mockDuende1);
        verify(mockTreeMap).put(80.0, mockCluster1);
    }

    @Test
    @DisplayName("Deve chamar o método steal quando um vizinho válido é encontrado")
    void logicaRoubo_comVizinhoValido_chamaSteal() {
        when(mockDuende1.getPosition()).thenReturn(10.0, 15.0);
        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(10.0)));

        when(mockTreeMap.get(10.0)).thenReturn(mockDuende1);
        when(mockTreeMap.get(15.0)).thenReturn(null);

        when(mockTma.findNearestEntidade(mockDuende1)).thenReturn(mockDuende2);

        simulationEngine.executarRodada();

        verify(mockDuende1).steal(mockDuende2);
    }
}
