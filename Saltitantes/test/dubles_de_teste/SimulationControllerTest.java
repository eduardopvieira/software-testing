package dubles_de_teste;

import Controller.SimulationController;
import Controller.SimulationEngine;
import model.dao.UsuarioDAO;
import model.datastructure.TreeMapAdaptado;
import view.SimulationView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationControllerTest {

    @Mock private UsuarioDAO mockUsuarioDAO;
    @Mock private SimulationEngine mockMotor;
    @Mock private SimulationView mockPanel;
    @Mock private TreeMapAdaptado mockTma;

    private SimulationController controller;

    @BeforeEach
    void setUp() {
        mockTma.treeMapPrincipal = new TreeMap<>();
    }


    @Test
    @DisplayName("MCDC (loginUsuario != null): Iniciar com usuário deve incrementar simulações")
    void iniciar_comUsuarioLogado_deveIncrementarSimulacoes() throws InterruptedException {
        controller = new SimulationController("jogador1", mockUsuarioDAO, mockMotor, mockPanel, mockTma);
        when(mockMotor.verificarCondicaoDeTermino()).thenReturn(1);

        controller.iniciar();
        Thread.sleep(100);

        verify(mockUsuarioDAO).incrementarSimulacoesExecutadas("jogador1");
    }

    @Test
    @DisplayName("MCDC (loginUsuario == null): Iniciar sem usuário NÃO deve incrementar simulações")
    void iniciar_semUsuarioLogado_naoDeveIncrementarSimulacoes() throws InterruptedException {
        controller = new SimulationController(null, mockUsuarioDAO, mockMotor, mockPanel, mockTma);
        when(mockMotor.verificarCondicaoDeTermino()).thenReturn(1);

        controller.iniciar();
        Thread.sleep(100);

        verify(mockUsuarioDAO, never()).incrementarSimulacoesExecutadas(anyString());
    }

    @Test
    @DisplayName("MCDC (A=true, B=true): Finalizar com Vitória e com usuário deve incrementar pontuação")
    void finalizarSimulacao_comVitoriaEUsuario_deveIncrementarPontuacao() throws InterruptedException {
        controller = new SimulationController("vencedor", mockUsuarioDAO, mockMotor, mockPanel, mockTma);
        when(mockMotor.verificarCondicaoDeTermino()).thenReturn(1);

        controller.iniciar();
        Thread.sleep(100);

        verify(mockUsuarioDAO).incrementarPontuacao("vencedor");
    }

    @Test
    @DisplayName("MCDC (A=true, B=false): Finalizar com Derrota e com usuário NÃO deve incrementar pontuação")
    void finalizarSimulacao_comDerrotaEUsuario_naoDeveIncrementarPontuacao() throws InterruptedException {
        controller = new SimulationController("perdedor", mockUsuarioDAO, mockMotor, mockPanel, mockTma);
        when(mockMotor.verificarCondicaoDeTermino()).thenReturn(-1);

        controller.iniciar();
        Thread.sleep(100);

        verify(mockUsuarioDAO, never()).incrementarPontuacao(anyString());
    }

    @Test
    @DisplayName("MCDC (A=false, B=true): Finalizar com Vitória e SEM usuário NÃO deve incrementar pontuação")
    void finalizarSimulacao_comVitoriaESemUsuario_naoDeveIncrementarPontuacao() throws InterruptedException {
        controller = new SimulationController(null, mockUsuarioDAO, mockMotor, mockPanel, mockTma);
        when(mockMotor.verificarCondicaoDeTermino()).thenReturn(1);

        controller.iniciar();
        Thread.sleep(100);

        verify(mockUsuarioDAO, never()).incrementarPontuacao(anyString());
    }
}
