package sistema;

import org.assertj.swing.fixture.DialogFixture;

public class StatisticsViewObject {
    private DialogFixture dialog;

    public StatisticsViewObject(DialogFixture dialog) {
        this.dialog = dialog;
    }

    public void clicarVoltar() {
        dialog.button("voltarButton").click();
    }
}
