package view;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;


public class FluxoDeSistemaTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;

    /**
     * Prepara o ambiente antes de cada teste, criando a tela de login.
     */
    @Override
    protected void onSetUp() {
        LoginView loginView = GuiActionRunner.execute(LoginView::new);
        window = new FrameFixture(robot(), loginView.getFrame());
        window.show();
    }
    
    @Override
    protected void onTearDown() {
        window.cleanUp();
    }

    @Test
    public void deveRealizarLoginEIniciarSimulacaoComSucesso() {
        LoginViewObject loginPage = new LoginViewObject(window);

        // Executa a jornada do usuário de forma fluente
        ConfigSimulacaoViewObject configPage = loginPage
                .preencherLogin("teste")
                .preencherSenha("123")
                .clicarLoginComSucesso();

        SimulationViewObject simulacaoPage = configPage
                .preencherDuendes("5")
                .preencherHorizonte("10000")
                .clicarIniciarSimulacao();

        // Verificação final
        simulacaoPage.estaVisivel();
    }

    @Test
    public void deveExibirMensagemDeErroParaLoginInvalido() {
        LoginViewObject loginPage = new LoginViewObject(window);

        loginPage
                .preencherLogin("usuario_invalido")
                .preencherSenha("senha_errada")
                .clicarLoginComFalha(); // Permanece na mesma página

        // Verificação final
        loginPage.verificarMensagemDeErro("Login ou senha inválidos.");
    }
}