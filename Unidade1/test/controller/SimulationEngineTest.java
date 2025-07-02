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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    @DisplayName("Deve retornar false para condição de término quando há mais de 2 entidades")
    void verificarCondicaoDeTermino_QuandoMaisDe2Entidades_RetornaFalse() {
        when(mockTreeMap.size()).thenReturn(3);
        assertFalse(simulationEngine.verificarCondicaoDeTermino());
    }

    @Test
    @DisplayName("Deve retornar true para condição de término quando há 2 ou menos entidades")
    void verificarCondicaoDeTermino_Quando2OuMenosEntidades_RetornaTrue() {
        when(mockTreeMap.size()).thenReturn(2);
        assertTrue(simulationEngine.verificarCondicaoDeTermino());

        when(mockTreeMap.size()).thenReturn(1);
        assertTrue(simulationEngine.verificarCondicaoDeTermino());
    }

    @Test
    @DisplayName("Deve mover um duende para uma posição vazia sem colisões")
    void executarRodada_QuandoMoveParaEspacoVazio_ApenasMove() {
        when(mockDuende1.getPosition()).thenReturn(10.0);
        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(10.0)));
        when(mockTreeMap.get(10.0)).thenReturn(mockDuende1);
        when(mockTreeMap.get(anyDouble())).thenReturn(null);

        simulationEngine.executarRodada();

        verify(mockDuende1).move(1000.0);
        verify(mockTreeMap).remove(10.0);
        verify(mockTreeMap).put(mockDuende1.getPosition(), mockDuende1);
        verify(mockDuende1, never()).steal(any());
    }

    @Test
    @DisplayName("Deve chamar a lógica de roubo quando um vizinho é encontrado")
    void executarRodada_QuandoHaVizinho_ChamaLogicaDeRoubo() {
        when(mockDuende1.getPosition()).thenReturn(10.0);
        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(10.0)));
        when(mockTreeMap.get(anyDouble())).thenReturn(null);
        when(mockTreeMap.get(10.0)).thenReturn(mockDuende1);
        when(mockTma.findNearestEntidade(mockDuende1)).thenReturn(mockDuende2);

        simulationEngine.executarRodada();

        verify(mockDuende1).steal(mockDuende2);
    }

    @Test
    @DisplayName("Deve criar um Cluster quando dois Duendes colidem")
    void executarRodada_QuandoDuendeColideComDuende_CriaCluster() {
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

        verify(mockTreeMap).put(eq(20.0), isA(Cluster.class));
        verify(mockTreeMap).remove(10.0);
        verify(mockTreeMap).remove(20.0);
    }

    @Test
    @DisplayName("Deve absorver moedas quando o Guardião colide com um Duende")
    void executarRodada_QuandoGuardiãoColideComDuende_AbsorveMoedas() {
        when(mockGuardiao.getPosition()).thenReturn(50.0);
        when(mockDuende1.getPosition()).thenReturn(60.0);

        doAnswer(invocation -> {
            when(mockGuardiao.getPosition()).thenReturn(60.0);
            return null;
        }).when(mockGuardiao).move(anyDouble());

        when(mockDuende1.beingStealed()).thenReturn(500L);

        when(mockTreeMap.keySet()).thenReturn(new HashSet<>(List.of(50.0, 60.0)));
        when(mockTreeMap.get(50.0)).thenReturn(mockGuardiao);
        when(mockTreeMap.get(60.0)).thenReturn(mockDuende1);

        simulationEngine.executarRodada();

        verify(mockDuende1).beingStealed();
        verify(mockGuardiao).addCoins(500L);
        verify(mockTreeMap).put(eq(60.0), eq(mockGuardiao));
    }
}
