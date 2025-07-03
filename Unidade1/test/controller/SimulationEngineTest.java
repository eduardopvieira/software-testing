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
    private GuardiaoDoHorizonte mockGuardiao;

    private SimulationEngine simulationEngine;

    @BeforeEach
    void setUp() {
        mockTma.treeMapPrincipal = mockTreeMap;
        simulationEngine = new SimulationEngine(mockTma, 1000.0);
    }

    // --- Testes para verificarCondicaoDeTermino ---

    @Test
    @DisplayName("Deve retornar 1 (Vitória) quando restam 2 entidades")
    void verificarCondicaoDeTermino_Com2Entidades_Retorna1() {
        when(mockTreeMap.size()).thenReturn(2);
        assertThat(simulationEngine.verificarCondicaoDeTermino()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve retornar -1 (Derrota) quando moedas do guardião superam as outras")
    void verificarCondicaoDeTermino_ComGuardiaoMaisRico_RetornaMenos1() {
        when(mockTreeMap.size()).thenReturn(3);
        when(mockGuardiao.getCoins()).thenReturn(1001L);
        when(mockDuende1.getCoins()).thenReturn(500L);
        when(mockDuende2.getCoins()).thenReturn(500L);
        when(mockTreeMap.values()).thenReturn(List.of(mockGuardiao, mockDuende1, mockDuende2));
        assertThat(simulationEngine.verificarCondicaoDeTermino()).isEqualTo(-1);
    }

    @Test
    @DisplayName("Deve criar um Cluster quando dois Duendes colidem")
    void executarRodada_quandoDuendeColideComDuende_criaCluster() {
        when(mockDuende1.getPosition()).thenReturn(10.0);

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
    @DisplayName("Deve absorver todas as moedas quando o Guardião colide com um Duende")
    void executarRodada_quandoGuardiaoColideComDuende_absorveDuende() {
        when(mockGuardiao.getPosition()).thenReturn(50.0);

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
}
