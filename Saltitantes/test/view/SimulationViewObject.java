package view;

import org.assertj.swing.fixture.FrameFixture;

public class SimulationViewObject {
    private FrameFixture window;

    public SimulationViewObject(FrameFixture window) {
        this.window = window;
    }

    public void estaVisivel() {
        window.requireVisible();
    }
}