import model.dao.DatabaseManager;
import view.LoginView;

public class Main {
    public static void main(String[] args) {
        // pd deixar chamar isso toda a vez, o comando só cria a tabela caso n exista
        DatabaseManager.criarTabelaUsuarios();

        LoginView menu = new LoginView();
        menu.exibir();
    }
}