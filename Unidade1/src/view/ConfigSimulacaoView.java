package view;

import Controller.SimulationController;
import Controller.SimulationEngine;
import Controller.SimulationSetup;
import model.dao.UsuarioDAO;
import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ConfigSimulacaoView {
    private JFrame frame;
    private JTextField duendesField;
    private JTextField horizonteField;
    private String loginUsuario;
    private JLabel avisoHorizonteLabel;

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
        frame.setSize(450, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel duendesLabel = new JLabel("Digite o número de duendes (2-20):");
        duendesField = new JTextField(10);

        JLabel horizonteLabel = new JLabel("Digite o tamanho máximo do horizonte:");
        horizonteField = new JTextField(10);

        avisoHorizonteLabel = new JLabel("OBS: O tamanho recomendado do horizonte é...");
        avisoHorizonteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        avisoHorizonteLabel.setForeground(Color.GRAY);

        JButton startButton = new JButton("Iniciar Simulação");
        JButton estatisticasButton = new JButton("Ver Estatísticas");

        duendesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        duendesField.setAlignmentX(Component.LEFT_ALIGNMENT);
        horizonteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        avisoHorizonteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        horizonteField.setAlignmentX(Component.LEFT_ALIGNMENT);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        estatisticasButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(duendesLabel);
        panel.add(duendesField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(horizonteLabel);
        panel.add(avisoHorizonteLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(horizonteField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(estatisticasButton);

        frame.add(panel, BorderLayout.CENTER);

        duendesField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarAvisoHorizonte();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarAvisoHorizonte();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarAvisoHorizonte();
            }
        });

        startButton.addActionListener(e -> iniciarSimulacao());
        estatisticasButton.addActionListener(e -> new StatisticsView(frame).exibir());
    }

    /**
     * <<< NOVO: Método que atualiza o texto do aviso >>>
     * É chamado sempre que o texto no campo de duendes muda.
     */
    private void atualizarAvisoHorizonte() {
        try {
            int numDuendes = Integer.parseInt(duendesField.getText());
            if (numDuendes > 0) {
                long valorRecomendado = (long) numDuendes * 1_000_000L;
                String valorFormatado = NumberFormat.getNumberInstance(Locale.getDefault()).format(valorRecomendado);

                avisoHorizonteLabel.setText("<html>OBS: O recomendado é <b>" + valorFormatado + "</b>. Diminua para simulações mais rápidas.</html>");
            } else {
                avisoHorizonteLabel.setText("OBS: O número de duendes deve ser positivo.");
            }
        } catch (NumberFormatException e) {
            avisoHorizonteLabel.setText("OBS: Digite um número de duendes válido.");
        }
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

            // cenario
            SimulationSetup setup = new SimulationSetup(numDuendes, maxHorizon);
            TreeMapAdaptado tma = setup.criarCenario();
            GuardiaoDoHorizonte guardiao = setup.criarGuardião();
            tma.treeMapPrincipal.put(guardiao.getPosition(), guardiao);

            // view
            SimulationView panel = new SimulationView(new ArrayList<>(tma.treeMapPrincipal.values()), maxHorizon);

            // jframe
            JFrame simulationFrame = new JFrame("Simulação de Duendes & Clusters");
            simulationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            simulationFrame.add(panel);
            simulationFrame.pack();
            simulationFrame.setLocationRelativeTo(null);
            simulationFrame.setVisible(true);

            SimulationEngine motor = new SimulationEngine(tma, maxHorizon);
            UsuarioDAO dao = new UsuarioDAO();

            SimulationController controller = new SimulationController(loginUsuario, dao, motor, panel, tma);

            controller.iniciar();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Por favor, digite números válidos.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
        }
    }
}

