package view;

import Controller.SimulationController;

import javax.swing.*;
import java.awt.*;

public class ConfigSimulacaoView {
    private JFrame frame;
    private JTextField duendesField;
    private JTextField horizonteField;
    private String loginUsuario;

    public ConfigSimulacaoView(String loginUsuario) {
        this.loginUsuario = loginUsuario;
        criarComponentes();
    }

    public void exibir() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void criarComponentes() {
        frame = new JFrame("Configuração da Simulação - Logado como: " + loginUsuario);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Campo para número de duendes ---
        JLabel duendesLabel = new JLabel("Digite o número de duendes (2-20):");
        duendesField = new JTextField(10);

        // --- Campo para tamanho do horizonte ---
        JLabel horizonteLabel = new JLabel("Digite o tamanho máximo do horizonte:");
        horizonteField = new JTextField(10);

        JButton startButton = new JButton("Iniciar Simulação");
        JButton estatisticasButton = new JButton("Ver Estatísticas"); // <<< NOVO BOTÃO

        // Alinhamento
        duendesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        duendesField.setAlignmentX(Component.LEFT_ALIGNMENT);
        horizonteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        horizonteField.setAlignmentX(Component.LEFT_ALIGNMENT);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        estatisticasButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adiciona componentes ao painel
        panel.add(duendesLabel);
        panel.add(duendesField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(horizonteLabel);
        panel.add(horizonteField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(estatisticasButton);

        frame.add(panel, BorderLayout.CENTER);

        startButton.addActionListener(e -> iniciarSimulacao());
        estatisticasButton.addActionListener(e -> new StatisticsView(frame).exibir());

        frame.add(panel, BorderLayout.CENTER);

        startButton.addActionListener(e -> iniciarSimulacao());
    }

    private void iniciarSimulacao() {
        try {
            int numDuendes = Integer.parseInt(duendesField.getText());
            double maxHorizon = Double.parseDouble(horizonteField.getText());

            if (numDuendes < 2 || numDuendes > 20) {
                JOptionPane.showMessageDialog(frame, "Número de duendes deve ser entre 2 e 20.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (maxHorizon <= 0) {
                JOptionPane.showMessageDialog(frame, "Horizonte deve ser maior que zero.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            frame.dispose();

            SimulationController controller = new SimulationController();

            controller.iniciarSimulacao(numDuendes, maxHorizon, loginUsuario);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Por favor, digite números válidos.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
        }
    }
}