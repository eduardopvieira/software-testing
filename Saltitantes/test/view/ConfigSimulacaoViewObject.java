package view;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
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
}