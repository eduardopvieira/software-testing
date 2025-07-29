package sistema;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import javax.swing.*;

public class ConfigSimulacaoViewObject {
    private FrameFixture window;

    public ConfigSimulacaoViewObject(FrameFixture window) {
        this.window = window;
    }

    public ConfigSimulacaoViewObject preencherDuendes(String numero) {
        window.textBox("duendesField").enterText(numero);
        return this;
    }

    public ConfigSimulacaoViewObject preencherHorizonte(String tamanho) {
        window.textBox("horizonteField").enterText(tamanho);
        return this;
    }

    public SimulationViewObject clicarIniciarSimulacao() {
        window.button("startButton").click();
        FrameFixture simFrame = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Simulação de Duendes & Clusters".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(window.robot());
        return new SimulationViewObject(simFrame);
    }

    public StatisticsViewObject clicarVerEstatisticas() {
        window.button("estatisticasButton").click();
        DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Estatísticas da Simulação".equals(dialog.getTitle()) && dialog.isShowing();
            }
        }).using(window.robot());
        return new StatisticsViewObject(dialog);
    }

    public void estaVisivel() {
        window.requireVisible();
    }

    public ConfigSimulacaoViewObject clicarExcluirConta() {
        window.button("excluirContaButton").click();
        return this;
    }

    public ConfigSimulacaoViewObject confirmarExclusao() {
        window.optionPane().yesButton().click();
        return this;
    }

    public ConfigSimulacaoViewObject negarExclusao() {
        window.optionPane().noButton().click();
        return this;
    }

    public void verificarMensagemDeErro(String mensagem) {
        window.optionPane().requireErrorMessage().requireMessage(mensagem);
    }

    public void verificarMensagemDeSucesso(String mensagem) {
        window.optionPane().requireMessage(mensagem);
    }
}