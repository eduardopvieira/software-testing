package model.dao;

import model.domain.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {


    public boolean adicionarUsuario(String login, String senha, String avatar) {

        String sql = "INSERT INTO usuarios (login, senha, avatar) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, senha);
            pstmt.setString(3, avatar);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                System.err.println("Erro de inclusão: O login '" + login + "' já existe.");
            } else {
                System.err.println("Erro ao adicionar usuário: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean excluirUsuario(String login) {
        String sql = "DELETE FROM usuarios WHERE login = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarSenha(String login, String senha) {
        // <<< ALTERADO: A query agora seleciona a coluna "senha" >>>
        String sql = "SELECT senha FROM usuarios WHERE login = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String senhaDoBanco = rs.getString("senha");
                return senha.equals(senhaDoBanco);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public Usuario buscarUsuario(String login) {
        String sql = "SELECT id, login, avatar, pontuacao, simulacoes_executadas FROM usuarios WHERE login = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("avatar"),
                        rs.getInt("pontuacao"),
                        rs.getInt("simulacoes_executadas")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void incrementarPontuacao(String login) {
        String sql = "UPDATE usuarios SET pontuacao = pontuacao + 1 WHERE login = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementarSimulacoesExecutadas(String login) {
        String sql = "UPDATE usuarios SET simulacoes_executadas = simulacoes_executadas + 1 WHERE login = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retorna todos os usuários para a tabela de estatísticas
    public List<Usuario> getTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY pontuacao DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("avatar"),
                        rs.getInt("pontuacao"),
                        rs.getInt("simulacoes_executadas")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    // Retorna as estatísticas globais
    public Map<String, Double> getEstatisticasGerais() {
        Map<String, Double> estatisticas = new HashMap<>();
        String sql = "SELECT COUNT(*) AS total_usuarios, SUM(simulacoes_executadas) AS total_simulacoes, SUM(pontuacao) AS total_pontos FROM usuarios";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                double totalUsuarios = rs.getDouble("total_usuarios");
                double totalSimulacoes = rs.getDouble("total_simulacoes");
                double totalPontos = rs.getDouble("total_pontos");

                estatisticas.put("total_simulacoes", totalSimulacoes);

                if (totalUsuarios > 0) {
                    estatisticas.put("media_bem_sucedidas", totalPontos / totalUsuarios);
                } else {
                    estatisticas.put("media_bem_sucedidas", 0.0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estatisticas;
    }
}