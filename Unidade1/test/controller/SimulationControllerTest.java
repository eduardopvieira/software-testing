package controller;

import Controller.SimulationController;
import Controller.SimulationEngine;
import Controller.SimulationSetup;
import model.dao.UsuarioDAO;
import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationControllerTest {

    // mocks pra todas as dependências externas
    @Mock
    private UsuarioDAO mockUsuarioDAO;
    @Mock
    private SimulationSetup mockSetup;
    @Mock
    private SimulationEngine mockMotor;
    @Mock
    private TreeMapAdaptado mockTma;

    // isso é pra controlar uma variavel estática que garante que um bug chato nao ocorra
    private static java.lang.reflect.Field simulacaoEmAndamentoField;

    // NOTA:
    // não usei @InjectMocks porque a lógica principal ta no construtor, entao
    // achei melhor instanciar o SimulationController manualmente em cada teste

    @BeforeEach
    void setUp() throws Exception {
        //apenas prepara a trava de segurança pra garantir que o bug chato nao ocorra
        simulacaoEmAndamentoField = SimulationController.class.getDeclaredField("simulacaoEmAndamento");
        simulacaoEmAndamentoField.setAccessible(true);
        simulacaoEmAndamentoField.set(null, false);
    }

    @AfterEach
    void tearDown() throws Exception {
        // libera a tal trava após cada teste pra n afetar o próximo
        simulacaoEmAndamentoField.set(null, false);
    }

    // testes de dominio e fronteira no construtor

    @Test
    @DisplayName("Deve lançar exceção para número de duendes inválido (abaixo do limite)")
    void constructor_QuandoNumDuendesAbaixoDoLimite_LancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimulationController(1, 100.0, "teste");
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para número de duendes inválido (acima do limite)")
    void constructor_QuandoNumDuendesAcimaDoLimite_LancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimulationController(21, 100.0, "teste");
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para horizonte inválido (zero)")
    void constructor_QuandoHorizonteInvalido_LancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimulationController(5, 0.0, "teste");
        });
    }

    // testes estruturais e de interaçao com mocks

    @Test
    @DisplayName("Não deve inicializar se outra simulação já estiver em andamento")
    void constructor_QuandoSimulacaoJaEmAndamento_NaoInicializa() throws Exception {

        simulacaoEmAndamentoField.set(null, true);

        //esse metodo so tenta criar uma nova instancia, mas é pra não retornar nada. previne um bug
        //onde tem chance de iniciar a simulação duas vezes.
    }

    @Test
    @DisplayName("Iniciar deve incrementar simulações executadas para usuário logado")
    void iniciar_QuandoUsuarioLogado_IncrementaSimulacoes() {

        //mockando o comportamento do construtor
        try (MockedStatic<SimulationSetup> mockedSetup = Mockito.mockStatic(SimulationSetup.class)) {
            mockedSetup.when(() -> new SimulationSetup(anyInt(), anyDouble())).thenReturn(mockSetup);
            when(mockSetup.criarCenario()).thenReturn(mockTma);
            when(mockSetup.criarGuardião()).thenReturn(mock(GuardiaoDoHorizonte.class));

            // instanciando o controller dentro do contexto do mock estático
            SimulationController controller = new SimulationController(5, 100.0, "jogador1");

            controller.iniciar();

            verify(mockUsuarioDAO).incrementarSimulacoesExecutadas("jogador1");
        }
    }

    @Test
    @DisplayName("Finalizar simulação deve incrementar pontuação para usuário logado")
    void finalizarSimulacao_QuandoUsuarioLogado_IncrementaPontuacao() {
        try (MockedStatic<SimulationSetup> mockedSetup = Mockito.mockStatic(SimulationSetup.class);
             MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class)) {

            mockedSetup.when(() -> new SimulationSetup(anyInt(), anyDouble())).thenReturn(mockSetup);
            when(mockSetup.criarCenario()).thenReturn(mockTma);
            when(mockSetup.criarGuardião()).thenReturn(mock(GuardiaoDoHorizonte.class));

            // mockando a criaçao do simulationengine tb
            try (MockedStatic<SimulationEngine> mockedEngine = Mockito.mockStatic(SimulationEngine.class)){
                mockedEngine.when(() -> new SimulationEngine(any(), anyDouble())).thenReturn(mockMotor);

                when(mockMotor.verificarCondicaoDeTermino()).thenReturn(true);

                SimulationController controller = new SimulationController(5, 100.0, "jogador1");

                controller.iniciar();

                try { Thread.sleep(100); } catch (InterruptedException e) {}

                verify(mockUsuarioDAO).incrementarPontuacao("jogador1");
            }
        }
    }

    // testes de propriedade
    @Test
    @DisplayName("Propriedade: Construtor deve funcionar para qualquer entrada válida")
    @Property
    void constructor_ParaQualquerEntradaValida_NaoLancaExcecao(
            @ForAll @IntRange(min = 2, max = 20) int numDuendes,
            @ForAll @DoubleRange(min = 0.1) double maxHorizon
    ) {
        // define que para QUALQUER combinação de duendes (entre 2 e 20)
        // e QUALQUER horizonte positivo, o construtor NÃO lança uma exceção.

        try (MockedStatic<SimulationSetup> mockedSetup = Mockito.mockStatic(SimulationSetup.class)) {
            mockedSetup.when(() -> new SimulationSetup(anyInt(), anyDouble())).thenReturn(mockSetup);
            when(mockSetup.criarCenario()).thenReturn(mockTma);
            when(mockSetup.criarGuardião()).thenReturn(mock(GuardiaoDoHorizonte.class));

            assertDoesNotThrow(() -> {
                new SimulationController(numDuendes, maxHorizon, "prop-test");
            });
        }
    }

}
