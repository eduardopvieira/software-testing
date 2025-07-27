package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static String DB_URL = "jdbc:h2:./simulationDB";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public static void setDbUrl(String newUrl) {
        DB_URL = newUrl;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void criarTabelaUsuarios() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  login VARCHAR(255) UNIQUE NOT NULL," +
                "  senha VARCHAR(255) NOT NULL," +
                "  avatar VARCHAR(255)," +
                "  pontuacao INT DEFAULT 0," +
                "  simulacoes_executadas INT DEFAULT 0" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela 'usuarios' verificada/criada com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar a tabela de usuários: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
