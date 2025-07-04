import model.dao.DatabaseManager;
import view.LoginView;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.criarTabelaUsuarios();
        new LoginView().exibir();
    }
}
