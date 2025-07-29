package sistema;

import model.dao.DatabaseManager;
import model.dao.UsuarioDAO;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import view.LoginView;

// Garante que os testes rodem em ordem alfabética
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FluxoSistemaCompletoTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private UsuarioDAO usuarioDAO;

    /**
     * Prepara o ambiente: Limpa e popula o banco de dados e abre a tela de login.
     */
    @Override
    protected void onSetUp() {
        // Prepara o banco de dados para um estado limpo e conhecido
        DatabaseManager.criarTabelaUsuarios();
        this.usuarioDAO = new UsuarioDAO();
        usuarioDAO.excluirUsuario("teste");
        usuarioDAO.excluirUsuario("novo_usuario");
        usuarioDAO.adicionarUsuario("teste", "123", "/model/avatar/avatar-duende.jpg");
        
        // Inicia a aplicação
        LoginView loginView = GuiActionRunner.execute(LoginView::new);
        window = new FrameFixture(robot(), loginView.getFrame());
        window.show();
    }

    @Override
    protected void onTearDown() {
        window.cleanUp();
    }

    // --- TESTES DE LOGIN E NAVEGAÇÃO ---

    @Test
    public void testA_deveFalharAoTentarLoginComCredenciaisInvalidas() {
        LoginViewObject loginPage = new LoginViewObject(window);
        loginPage.preencherLogin("usuario_invalido").preencherSenha("senha_errada").clicarLoginComFalha();
        loginPage.verificarMensagemDeErro("Login ou senha inválidos.");
    }

    @Test
    public void testB_deveLogarComSucessoEIniciarSimulacao() {
        LoginViewObject loginPage = new LoginViewObject(window);
        ConfigSimulacaoViewObject configPage = loginPage.preencherLogin("teste").preencherSenha("123").clicarLoginComSucesso();
        SimulationViewObject simulacaoPage = configPage.preencherDuendes("5").preencherHorizonte("10000").clicarIniciarSimulacao();
        simulacaoPage.estaVisivel();
    }

    @Test
    public void testC_deveLogarVerEstatisticasEVoltar() {
        LoginViewObject loginPage = new LoginViewObject(window);
        ConfigSimulacaoViewObject configPage = loginPage.preencherLogin("teste").preencherSenha("123").clicarLoginComSucesso();
        StatisticsViewObject statsPage = configPage.clicarVerEstatisticas();
        statsPage.clicarVoltar();
        configPage.estaVisivel(); // Verifica se voltou para a tela de configuração
    }

    // --- TESTES DE CRIAÇÃO DE CONTA ---

    @Test
    public void testD_deveFalharAoCriarContaComDadosVazios() {
        LoginViewObject loginPage = new LoginViewObject(window);
        CriarContaDialogObject criarContaPage = loginPage.clicarCriarConta();
        criarContaPage.preencherNovoLogin("").preencherNovaSenha("").clicarConfirmarComFalha();
        criarContaPage.verificarMensagemDeErro("Login e senha não podem ser vazios.");
    }

    @Test
    public void testE_deveCriarContaComSucesso() {
        LoginViewObject loginPage = new LoginViewObject(window);
        CriarContaDialogObject criarContaPage = loginPage.clicarCriarConta();
        
        loginPage = criarContaPage.preencherNovoLogin("novo_usuario")
                                .preencherNovaSenha("nova_senha")
                                .selecionarAvatarDuende()
                                .clicarConfirmarComSucesso();

        loginPage.verificarMensagemDeSucesso("Conta criada com sucesso! Agora você pode fazer o login.");
    }

    @Test
    public void testF_deveCriarContaELogarNaSimulacao() {
        // Cria a conta
        testE_deveCriarContaComSucesso();
        window.optionPane().okButton().click(); // Fecha o dialogo de sucesso

        // Agora faz o login com a nova conta
        LoginViewObject loginPage = new LoginViewObject(window);
        ConfigSimulacaoViewObject configPage = loginPage.preencherLogin("novo_usuario").preencherSenha("nova_senha").clicarLoginComSucesso();
        SimulationViewObject simulacaoPage = configPage.preencherDuendes("2").preencherHorizonte("5000").clicarIniciarSimulacao();
        simulacaoPage.estaVisivel();
    }

    // --- TESTES DE REMOÇÃO DE CONTA ---

    @Test
    public void testG_deveCancelarARemocaoDeConta() {
        LoginViewObject loginPage = new LoginViewObject(window);
        ConfigSimulacaoViewObject configPage = loginPage.preencherLogin("teste").preencherSenha("123").clicarLoginComSucesso();

        configPage.clicarExcluirConta()
                 .negarExclusao();
        configPage.estaVisivel();
    }

    @Test
    public void testH_deveRemoverContaComSucesso() {
        LoginViewObject loginPage = new LoginViewObject(window);
        ConfigSimulacaoViewObject configPage = loginPage.preencherLogin("teste").preencherSenha("123").clicarLoginComSucesso();

        configPage.clicarExcluirConta()
                 .confirmarExclusao();
        configPage.verificarMensagemDeSucesso("Sua conta foi excluída com sucesso.");
    }
}