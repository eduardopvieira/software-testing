package view;

import model.dao.UsuarioDAO;
import model.domain.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class StatisticsView {
    private JDialog dialogo;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public StatisticsView(Frame owner) {
        this.dialogo = new JDialog(owner, "Estatísticas da Simulação", true);
        criarComponentes();
    }

    public void exibir() {
        dialogo.setLocationRelativeTo(dialogo.getOwner());
        dialogo.setVisible(true);
    }

    private void criarComponentes() {
        dialogo.setSize(600, 400);
        dialogo.setLayout(new BorderLayout(10, 10));

        JPanel painelGeral = new JPanel(new GridLayout(1, 2, 10, 5));
        painelGeral.setBorder(BorderFactory.createTitledBorder("Estatísticas Globais"));

        Map<String, Double> gerais = usuarioDAO.getEstatisticasGerais();
        DecimalFormat df = new DecimalFormat("#.##");

        double totalSimulacoes = gerais.getOrDefault("total_simulacoes", 0.0);
        double mediaSucesso = gerais.getOrDefault("media_bem_sucedidas", 0.0);

        JLabel totalLabel = new JLabel("Quantidade Total de Simulações: " + (int)totalSimulacoes);
        JLabel mediaLabel = new JLabel("Média de Simulações Bem-Sucedidas por Usuário: " + df.format(mediaSucesso));

        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mediaLabel.setHorizontalAlignment(SwingConstants.CENTER);

        painelGeral.add(totalLabel);
        painelGeral.add(mediaLabel);

        String[] colunas = {"Login", "Pontuação (Bem-sucedidas)", "Simulações Executadas"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(tableModel);

        List<Usuario> usuarios = usuarioDAO.getTodosUsuarios();
        for(Usuario u : usuarios) {
            Object[] linha = {
                    u.getLogin(),
                    u.getPontuacao(),
                    u.getSimulacoesExecutadas()
            };
            tableModel.addRow(linha);
        }

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Estatísticas por Usuário"));

        dialogo.add(painelGeral, BorderLayout.NORTH);
        dialogo.add(scrollPane, BorderLayout.CENTER);
    }
}
