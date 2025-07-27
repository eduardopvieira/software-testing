package sistema;

import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;

public class CriarContaDialogObject {
    private DialogFixture dialog;
    private FrameFixture parent; // Referência à janela de login

    public CriarContaDialogObject(DialogFixture dialog, FrameFixture parent) {
        this.dialog = dialog;
        this.parent = parent;
    }

    public CriarContaDialogObject preencherNovoLogin(String login) {
        dialog.textBox("novoLoginField").enterText(login);
        return this;
    }

    public CriarContaDialogObject preencherNovaSenha(String senha) {
        dialog.textBox("novaSenhaField").enterText(senha);
        return this;
    }

    public CriarContaDialogObject selecionarAvatarDuende() {
        dialog.radioButton("avatarDuendeRadio").check();
        return this;
    }

    public LoginViewObject clicarConfirmarComSucesso() {
        dialog.button("confirmarButton").click();
        // Após sucesso, o diálogo fecha e voltamos para a tela de login
        return new LoginViewObject(parent);
    }
    
    public CriarContaDialogObject clicarConfirmarComFalha() {
        dialog.button("confirmarButton").click();
        return this; // Permanece no mesmo diálogo
    }
    
    public void verificarMensagemDeErro(String mensagem) {
        dialog.optionPane().requireErrorMessage().requireMessage(mensagem);
        dialog.optionPane().okButton().click(); // Fecha o JOptionPane de erro
    }
}