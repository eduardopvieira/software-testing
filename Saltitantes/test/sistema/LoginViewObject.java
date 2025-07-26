package sistema;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import javax.swing.*;

public class LoginViewObject {
    private FrameFixture window;

    public LoginViewObject(FrameFixture window) {
        this.window = window;
    }

    public LoginViewObject preencherLogin(String login) {
        window.textBox("loginField").enterText(login);
        return this;
    }

    public LoginViewObject preencherSenha(String senha) {
        window.textBox("senhaField").enterText(senha);
        return this;
    }

    public ConfigSimulacaoViewObject clicarLoginComSucesso() {
        window.button("loginButton").click();
        // Encontra a próxima janela que foi aberta
        FrameFixture configWindow = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return frame.getTitle().startsWith("Configuração da Simulação") && frame.isShowing();
            }
        }).using(window.robot());

        return new ConfigSimulacaoViewObject(configWindow);
    }
    
    public LoginViewObject clicarLoginComFalha() {
        window.button("loginButton").click();
        return this;
    }

    public void verificarMensagemDeErro(String mensagem) {
        window.optionPane().requireErrorMessage().requireMessage(mensagem);
    }

    public void verificarMensagemDeSucesso(String mensagem) {
        window.optionPane().requireMessage(mensagem);
    }

    public CriarContaDialogObject clicarCriarConta() {
        window.button("criarContaButton").click();
        // Encontra o diálogo de criar conta
        DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Criar Nova Conta".equals(dialog.getTitle()) && dialog.isShowing();
            }
        }).using(window.robot());
        
        return new CriarContaDialogObject(dialog, window);
    }

    public LoginViewObject clicarExcluirConta() {
        window.button("excluirContaButton").click();
        return this;
    }

    // Métodos para interagir com os OptionPanes de exclusão
    public LoginViewObject preencherLoginParaExcluir(String login) {
        window.optionPane().textBox().enterText(login);
        window.optionPane().okButton().click();
        return this;
    }

    public LoginViewObject confirmarExclusao() {
        window.optionPane().yesButton().click();
        return this;
    }

    public LoginViewObject negarExclusao() {
        window.optionPane().noButton().click();
        return this;
    }
}
