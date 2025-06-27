package model.dao;

import model.domain.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        String sql = "SELECT id, login, avatar, pontuacao FROM usuarios WHERE login = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("avatar"),
                        rs.getInt("pontuacao")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}