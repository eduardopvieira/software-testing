package model.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private UsuarioDAO usuarioDAO;

    // teste de domínio, fronteira e estrutural

    @Test
    @DisplayName("Deve adicionar um usuário com sucesso quando os dados são válidos")
    void adicionarUsuario_QuandoDadosValidos_RetornaTrue() throws SQLException {
        try (MockedStatic<DatabaseManager> mockedDbManager = Mockito.mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            boolean resultado = usuarioDAO.adicionarUsuario("novoUser", "senha123", "avatar.png");

            assertThat(resultado).isTrue();
        }
    }

    @Test
    @DisplayName("Deve falhar ao adicionar usuário se o login já existir")
    void adicionarUsuario_QuandoLoginDuplicado_RetornaFalse() throws SQLException {
        try (MockedStatic<DatabaseManager> mockedDbManager = Mockito.mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Login duplicado", "23505", 23505));

            boolean resultado = usuarioDAO.adicionarUsuario("userExistente", "senha123", "avatar.png");

            assertThat(resultado).isFalse();
        }
    }

    @Test
    @DisplayName("Deve verificar a senha com sucesso para credenciais corretas")
    void verificarSenha_QuandoCredenciaisCorretas_RetornaTrue() throws SQLException {
        try (MockedStatic<DatabaseManager> mockedDbManager = Mockito.mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true); // Simula que encontrou o usuário
            when(mockResultSet.getString("senha")).thenReturn("senhaCorreta");

            boolean resultado = usuarioDAO.verificarSenha("user", "senhaCorreta");

            assertThat(resultado).isTrue();
        }
    }

    @Test
    @DisplayName("Deve falhar a verificação de senha para credenciais incorretas")
    void verificarSenha_QuandoCredenciaisIncorretas_RetornaFalse() throws SQLException {
        // esse teste cobre as duas ramificações da decisão "if (senha.equals(senhaDoBanco))"
        try (MockedStatic<DatabaseManager> mockedDbManager = Mockito.mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true); // Usuário encontrado
            when(mockResultSet.getString("senha")).thenReturn("senhaCorreta");

            boolean resultado = usuarioDAO.verificarSenha("user", "senhaErrada");

            assertThat(resultado).isFalse();
        }
    }

    @Test
    @DisplayName("Deve falhar a verificação de senha se o usuário não existir")
    void verificarSenha_QuandoUsuarioNaoExiste_RetornaFalse() throws SQLException {
        // esse teste cobre a ramificação 'false' da decisão "if (rs.next())"
        try (MockedStatic<DatabaseManager> mockedDbManager = Mockito.mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false); // Simula que não encontrou o usuário

            boolean resultado = usuarioDAO.verificarSenha("userInexistente", "qualquerSenha");

            assertThat(resultado).isFalse();
        }
    }

    //TESTE ABAIXO CONTA COM INTEGRAÇÃO COM O BANCO DE DADOS
    //como ainda nao está no escopo, deixei comentado mas ele ta aí pra usos futuros

//    @Test
//    @DisplayName("Deve retornar uma lista de todos os usuários")
//    void getTodosUsuarios_QuandoExistemUsuarios_RetornaListaPopulada() throws SQLException {
//        try (MockedStatic<DatabaseManager> mockedDbManager = Mockito.mockStatic(DatabaseManager.class)) {
//            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
//            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
//            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
//
//            when(mockResultSet.next()).thenReturn(true, true, false);
//            when(mockResultSet.getString("login")).thenReturn("user1", "user2");
//            when(mockResultSet.getInt("pontuacao")).thenReturn(10, 20);
//
//            List<Usuario> usuarios = usuarioDAO.getTodosUsuarios();
//
//            assertThat(usuarios).hasSize(2);
//            assertThat(usuarios.get(0).getLogin()).isEqualTo("user1");
//            assertThat(usuarios.get(1).getLogin()).isEqualTo("user2");
//            assertThat(usuarios.get(1).getPontuacao()).isEqualTo(20);
//        }
//    }


    // teste de propriedade

    @Property
    @DisplayName("Propriedade: Adicionar usuário deve funcionar para qualquer login e senha válidos")
    void adicionarUsuario_PropertyTest(@ForAll @StringLength(min = 1, max = 50) @AlphaChars String login,
                                       @ForAll @StringLength(min = 1, max = 50) String senha) throws SQLException {

        try (MockedStatic<DatabaseManager> mockedDbManager = Mockito.mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            boolean resultado = usuarioDAO.adicionarUsuario(login, senha, "avatar.png");

            assertThat(resultado).isTrue();
        }
    }
}
