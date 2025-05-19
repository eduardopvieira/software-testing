package view;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Controller.SimulationController;

public class MenuView {
    private static JFrame frame;
    private static JPanel inputPanel;
    private static JTextField inputField;
    private static JTextField minField;
    private static JTextField maxField;

    public static void criarJanelaInput() {
        frame = new JFrame("Configuração da Simulação");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 300); // Aumentei o tamanho para acomodar os novos campos
        frame.setLayout(new BorderLayout());

        // Painel principal com bordas e espaçamento
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Configuração de fonte para reutilização
        Font fontLabel = new Font("Arial", Font.BOLD, 14);
        Font fontField = new Font("Arial", Font.PLAIN, 14);
        
        // Campo para número de duendes
        JPanel duendesPanel = new JPanel(new BorderLayout(5, 5));
        JLabel duendesLabel = new JLabel("Digite o número de duendes:", SwingConstants.LEFT);
        duendesLabel.setFont(fontLabel);
        inputField = new JTextField();
        inputField.setFont(fontField);
        duendesPanel.add(duendesLabel, BorderLayout.NORTH);
        duendesPanel.add(inputField, BorderLayout.CENTER);
        
        // Painel para os valores do horizonte (máximo e mínimo)
        JPanel horizontePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        horizontePanel.setBorder(BorderFactory.createTitledBorder("Configuração do Horizonte"));
        
        // Campo para valor mínimo do horizonte
        JPanel minPanel = new JPanel(new BorderLayout(5, 5));
        JLabel minLabel = new JLabel("Valor Mínimo:", SwingConstants.LEFT);
        minLabel.setFont(fontLabel);
        minField = new JTextField();
        minField.setFont(fontField);
        minPanel.add(minLabel, BorderLayout.NORTH);
        minPanel.add(minField, BorderLayout.CENTER);
        
        // Campo para valor máximo do horizonte
        JPanel maxPanel = new JPanel(new BorderLayout(5, 5));
        JLabel maxLabel = new JLabel("Valor Máximo:", SwingConstants.LEFT);
        maxLabel.setFont(fontLabel);
        maxField = new JTextField();
        maxField.setFont(fontField);
        maxPanel.add(maxLabel, BorderLayout.NORTH);
        maxPanel.add(maxField, BorderLayout.CENTER);
        
        horizontePanel.add(minPanel);
        horizontePanel.add(maxPanel);
        
        // Botão de iniciar simulação
        JButton startButton = new JButton("Iniciar Simulação");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> callSimulacao(inputField, minField, maxField));
        
        // Adiciona os componentes ao painel principal com espaçamento
        inputPanel.add(duendesPanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Espaço entre componentes
        inputPanel.add(horizontePanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Mais espaço antes do botão
        inputPanel.add(startButton);
        
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void callSimulacao(JTextField inputField, JTextField minField, JTextField maxField) {
        try {
            int numDuendes = Integer.parseInt(inputField.getText());
            int maxHorizon = Integer.parseInt(maxField.getText());

            if (numDuendes < 1 || numDuendes > 20) {
                JOptionPane.showMessageDialog(frame,
                        "Por favor, digite um número entre 1 e 20",
                        "Entrada inválida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            frame.remove(inputPanel);
            frame.dispose();

            SimulationController.iniciarSimulacao(numDuendes, maxHorizon);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Por favor, digite um número válido",
                    "Entrada inválida",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
