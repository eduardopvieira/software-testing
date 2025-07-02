package view;

import model.dao.UsuarioDAO;
import model.domain.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class StatisticsView {
    private JDialog dialogo;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    static class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof ImageIcon) {
                setIcon((ImageIcon) value);
                setText("");
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setIcon(null);
            }
            return this;
        }
    }

    public StatisticsView(Frame owner) {
        this.dialogo = new JDialog(owner, "Estatísticas da Simulação", true);
        criarComponentes();
    }

    public void exibir() {
        dialogo.setLocationRelativeTo(dialogo.getOwner());
        dialogo.setVisible(true);
    }

    private void criarComponentes() {
        dialogo.setSize(700, 400);
        dialogo.setLayout(new BorderLayout(10, 10));

        JPanel painelGeral = new JPanel(new GridLayout(1, 2, 10, 5));
        painelGeral.setBorder(BorderFactory.createTitledBorder("Estatísticas Globais"));

        Map<String, Double> gerais = usuarioDAO.getEstatisticasGerais();
        DecimalFormat df = new DecimalFormat("#.##");

        double totalSimulacoes = gerais.getOrDefault("total_simulacoes", 0.0);
        double mediaSucesso = gerais.getOrDefault("media_bem_sucedidas", 0.0);

        JLabel totalLabel = new JLabel("Quantidade Total de Simulações: " + (int) totalSimulacoes);
        JLabel mediaLabel = new JLabel("Média de Simulações Bem-Sucedidas por Usuário: " + df.format(mediaSucesso));

        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mediaLabel.setHorizontalAlignment(SwingConstants.CENTER);

        painelGeral.add(totalLabel);
        painelGeral.add(mediaLabel);

        String[] colunas = {"Avatar", "Login", "Pontuação (Bem-sucedidas)", "Simulações Executadas"};

        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return ImageIcon.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        JTable tabela = new JTable(tableModel);
        tabela.setRowHeight(50);
        tabela.setDefaultRenderer(ImageIcon.class, new ImageRenderer());

        List<Usuario> usuarios = usuarioDAO.getTodosUsuarios();
        for (Usuario u : usuarios) {
            ImageIcon avatarIcon = null;
            try {
                // Supondo que o método getAvatar() retorne o caminho do arquivo
                Image img = new ImageIcon(getClass().getResource(u.getAvatar())).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                avatarIcon = new ImageIcon(img);
            } catch (Exception e) {
                System.err.println("Erro ao carregar avatar para o usuário " + u.getLogin() + ": " + e.getMessage());
            }

            Object[] linha = {
                    avatarIcon,
                    u.getLogin(),
                    u.getPontuacao(),
                    u.getSimulacoesExecutadas()
            };
            tableModel.addRow(linha);
        }

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Estatísticas por Usuário"));

        // <<< INÍCIO DA MUDANÇA >>>
        // Cria um painel para o botão de voltar, alinhado à direita
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoVoltar = new JButton("Voltar");

        botaoVoltar.addActionListener(e -> dialogo.dispose());

        painelBotoes.add(botaoVoltar);

        dialogo.add(painelGeral, BorderLayout.NORTH);
        dialogo.add(scrollPane, BorderLayout.CENTER);
        dialogo.add(painelBotoes, BorderLayout.SOUTH); // Adiciona o painel com o botão no rodapé
    }
}
