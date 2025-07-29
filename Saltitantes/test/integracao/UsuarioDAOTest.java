package integracao;

import model.dao.DatabaseManager;
import model.dao.UsuarioDAO;
import model.entities.Usuario;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioDAOTest {

    private UsuarioDAO usuarioDAO;
    private static final String DB_URL_TEST = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    @BeforeAll
    static void setupOnce() {
        DatabaseManager.setDbUrl(DB_URL_TEST);
        DatabaseManager.criarTabelaUsuarios();
    }

    @BeforeEach
    void setup() {
        usuarioDAO = new UsuarioDAO();
        clearDatabase();
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    private void clearDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM usuarios");
            stmt.execute("ALTER TABLE usuarios ALTER COLUMN id RESTART WITH 1");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Falha ao limpar o banco de dados de teste: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Teste de Conexão com o Banco de Dados")
    void testGetConnection() {
        assertDoesNotThrow(() -> {
            Connection conn = DatabaseManager.getConnection();
            assertNotNull(conn, "A conexão com o banco de dados não deve ser nula.");
            assertTrue(conn.isValid(1), "A conexão deve ser válida.");
            conn.close();
            assertTrue(conn.isClosed(), "A conexão deve ser fechada.");
        }, "Não deve lançar exceção ao obter e fechar a conexão.");
    }

    @Test
    @DisplayName("Teste de Criação da Tabela de Usuários")
    void testCriarTabelaUsuarios() {
        assertDoesNotThrow(() -> DatabaseManager.criarTabelaUsuarios(),
                "Não deve lançar exceção ao tentar criar a tabela de usuários novamente.");
    }

    @Test
    @DisplayName("Teste de Adição de Usuário - Sucesso")
    void testAdicionarUsuario_Sucesso() {
        boolean adicionado = usuarioDAO.adicionarUsuario("testeUser", "senha123", "avatar1.png");
        assertTrue(adicionado, "O usuário deve ser adicionado com sucesso.");

        Usuario usuario = usuarioDAO.buscarUsuario("testeUser");
        assertNotNull(usuario, "O usuário adicionado deve ser encontrado.");
        assertEquals("testeUser", usuario.getLogin());
        assertEquals("avatar1.png", usuario.getAvatar());
        assertEquals(0, usuario.getPontuacao());
        assertEquals(0, usuario.getSimulacoesExecutadas());
    }

    @Test
    @DisplayName("Teste de Adição de Usuário - Login Duplicado")
    void testAdicionarUsuario_LoginDuplicado() {
        usuarioDAO.adicionarUsuario("testeUserDuplicado", "senha123", "avatar.png");
        boolean adicionadoNovamente = usuarioDAO.adicionarUsuario("testeUserDuplicado", "outraSenha", "outroAvatar.png");
        assertFalse(adicionadoNovamente, "Não deve ser possível adicionar usuário com login duplicado.");
    }

    @Test
    @DisplayName("Teste de Verificação de Senha - Correta")
    void testVerificarSenha_Correta() {
        usuarioDAO.adicionarUsuario("userSenha", "senhaCorreta", "avatar.png");
        assertTrue(usuarioDAO.verificarSenha("userSenha", "senhaCorreta"), "A senha deve ser verificada como correta.");
    }

    @Test
    @DisplayName("Teste de Verificação de Senha - Incorreta")
    void testVerificarSenha_Incorreta() {
        usuarioDAO.adicionarUsuario("userSenhaErrada", "senhaCerta", "avatar.png");
        assertFalse(usuarioDAO.verificarSenha("userSenhaErrada", "senhaErrada"), "A senha deve ser verificada como incorreta.");
    }

    @Test
    @DisplayName("Teste de Verificação de Senha - Usuário Inexistente")
    void testVerificarSenha_UsuarioInexistente() {
        assertFalse(usuarioDAO.verificarSenha("naoExiste", "qualquerSenha"), "Não deve verificar senha para usuário inexistente.");
    }

    @Test
    @DisplayName("Teste de Busca de Usuário - Existente")
    void testBuscarUsuario_Existente() {
        usuarioDAO.adicionarUsuario("buscaUser", "senha", "avatarBusca.png");
        Usuario encontrado = usuarioDAO.buscarUsuario("buscaUser");
        assertNotNull(encontrado, "O usuário deve ser encontrado.");
        assertEquals("buscaUser", encontrado.getLogin());
    }

    @Test
    @DisplayName("Teste de Busca de Usuário - Inexistente")
    void testBuscarUsuario_Inexistente() {
        Usuario naoEncontrado = usuarioDAO.buscarUsuario("usuarioInexistente");
        assertNull(naoEncontrado, "O usuário não deve ser encontrado.");
    }

    @Test
    @DisplayName("Teste de Incremento de Pontuação")
    void testIncrementarPontuacao() {
        usuarioDAO.adicionarUsuario("userPontos", "senha", "avatar.png");
        usuarioDAO.incrementarPontuacao("userPontos");
        Usuario user = usuarioDAO.buscarUsuario("userPontos");
        assertNotNull(user);
        assertEquals(1, user.getPontuacao(), "A pontuação deve ser incrementada para 1.");

        usuarioDAO.incrementarPontuacao("userPontos");
        user = usuarioDAO.buscarUsuario("userPontos");
        assertEquals(2, user.getPontuacao(), "A pontuação deve ser incrementada para 2.");
    }

    @Test
    @DisplayName("Teste de Incremento de Simulações Executadas")
    void testIncrementarSimulacoesExecutadas() {
        usuarioDAO.adicionarUsuario("userSimulacoes", "senha", "avatar.png");
        usuarioDAO.incrementarSimulacoesExecutadas("userSimulacoes");
        Usuario user = usuarioDAO.buscarUsuario("userSimulacoes");
        assertNotNull(user);
        assertEquals(1, user.getSimulacoesExecutadas(), "O número de simulações deve ser incrementado para 1.");

        usuarioDAO.incrementarSimulacoesExecutadas("userSimulacoes");
        user = usuarioDAO.buscarUsuario("userSimulacoes");
        assertEquals(2, user.getSimulacoesExecutadas(), "O número de simulações deve ser incrementado para 2.");
    }

    @Test
    @DisplayName("Teste de Obtenção de Todos os Usuários")
    void testGetTodosUsuarios() {
        usuarioDAO.adicionarUsuario("userA", "senha", "a.png");
        usuarioDAO.incrementarPontuacao("userA");
        usuarioDAO.adicionarUsuario("userB", "senha", "b.png");
        usuarioDAO.incrementarPontuacao("userB");
        usuarioDAO.incrementarPontuacao("userB");
        usuarioDAO.adicionarUsuario("userC", "senha", "c.png");

        List<Usuario> usuarios = usuarioDAO.getTodosUsuarios();
        assertNotNull(usuarios);
        assertEquals(3, usuarios.size(), "Deve retornar 3 usuários.");

        assertEquals("userB", usuarios.get(0).getLogin());
        assertEquals("userA", usuarios.get(1).getLogin());
        assertEquals("userC", usuarios.get(2).getLogin());
    }

    @Test
    @DisplayName("Teste de Obtenção de Estatísticas Gerais")
    void testGetEstatisticasGerais() {
        usuarioDAO.adicionarUsuario("user1", "s1", "a1.png");
        usuarioDAO.incrementarPontuacao("user1");
        usuarioDAO.incrementarSimulacoesExecutadas("user1");

        usuarioDAO.adicionarUsuario("user2", "s2", "a2.png");
        usuarioDAO.incrementarPontuacao("user2");
        usuarioDAO.incrementarPontuacao("user2");
        usuarioDAO.incrementarSimulacoesExecutadas("user2");
        usuarioDAO.incrementarSimulacoesExecutadas("user2");

        Map<String, Double> stats = usuarioDAO.getEstatisticasGerais();
        assertNotNull(stats);
        assertEquals(3.0, stats.get("total_simulacoes"), "O total de simulações deve ser 3.");
        assertEquals(1.5, stats.get("media_bem_sucedidas"), 0.001, "A média de pontos deve ser 1.5.");
    }

    @Test
    @DisplayName("Teste de Exclusão de Usuário - Sucesso")
    void testExcluirUsuario_Sucesso() {
        usuarioDAO.adicionarUsuario("paraExcluir", "senha", "avatar.png");
        Usuario usuarioAntes = usuarioDAO.buscarUsuario("paraExcluir");
        assertNotNull(usuarioAntes, "Usuário deve existir antes da exclusão.");

        boolean excluido = usuarioDAO.excluirUsuario("paraExcluir");
        assertTrue(excluido, "O usuário deve ser excluído com sucesso.");

        Usuario usuarioDepois = usuarioDAO.buscarUsuario("paraExcluir");
        assertNull(usuarioDepois, "O usuário não deve ser encontrado após a exclusão.");
    }

    @Test
    @DisplayName("Teste de Exclusão de Usuário - Inexistente")
    void testExcluirUsuario_Inexistente() {
        boolean excluido = usuarioDAO.excluirUsuario("naoExisteParaExcluir");
        assertFalse(excluido, "Não deve ser possível excluir um usuário inexistente.");
    }
}
