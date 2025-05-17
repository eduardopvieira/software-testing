import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import model.Duende;
import model.TreeMapAdaptado;
import view.SimulationPanel;

public class Main {
    private static JFrame frame;
    private static JPanel inputPanel;
    private static JTextField inputField;
    private static JTextField minField;
    private static JTextField maxField;

    public static void main(String[] args) {
        criarJanelaInput();
    }

    private static void criarJanelaInput() {
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
        startButton.addActionListener(e -> iniciarSimulacao());
        
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

    private static void iniciarSimulacao() {
        try {
            int numDuendes = Integer.parseInt(inputField.getText());
            int minHorizon = Integer.parseInt(minField.getText());
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

            List<Duende> duendes = criarDuendes(numDuendes);
            TreeMapAdaptado tma = inicializarTreeMap(duendes);
            SimulationPanel panel = criarEExibirJanela(duendes, minHorizon, maxHorizon);
            executarLogicaSimulacao(duendes, tma, panel);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Por favor, digite um número válido",
                    "Entrada inválida",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static List<Duende> criarDuendes(int quantidade) {
        List<Duende> duendes = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            duendes.add(new Duende(i));
        }
        return duendes;
    }

    private static TreeMapAdaptado inicializarTreeMap(List<Duende> duendes) {
        TreeMapAdaptado tma = new TreeMapAdaptado();
        duendes.forEach(tma::addDuende);
        return tma;
    }

    private static SimulationPanel criarEExibirJanela(List<Duende> duendes, int minHorizon, int maxHorizon) {
        SimulationPanel panel = new SimulationPanel(duendes, minHorizon, maxHorizon);
        JFrame simulationFrame = new JFrame("Simulação de Duendes");
        simulationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simulationFrame.add(panel);
        simulationFrame.pack();
        simulationFrame.setLocationRelativeTo(null);
        simulationFrame.setVisible(true);
        return panel;
    }

    private static void executarLogicaSimulacao(List<Duende> duendes, TreeMapAdaptado tma, SimulationPanel panel) {
        new Thread(() -> {
            int iteracao = 0;
            boolean alguemChegou = false;

            while (!alguemChegou) {
                iteracao++;
                System.out.println("\nIteração " + iteracao);

                alguemChegou = processarMovimentoDuendes(duendes, tma, panel);

                if (alguemChegou) {
                    exibirResultadosFinais(duendes);
                }
            }
        }).start();
    }

    private static boolean processarMovimentoDuendes(List<Duende> duendes, TreeMapAdaptado tma, SimulationPanel panel) {
        for (Duende duende : duendes) {
            moverERoubar(duende, tma, panel);

            if (verificarChegada(duende)) {
                return true;
            }

            pausaVisualizacao();
        }
        return false;
    }

    private static void moverERoubar(Duende duende, TreeMapAdaptado tma, SimulationPanel panel) {
        SwingUtilities.invokeLater(() -> {
            tma.treeMapPrincipal.remove(duende.getPosition());
            duende.move();
            tma.addDuende(duende);

            Duende vitima = tma.findNearestDuende(duende);
            if (vitima != null && vitima != duende) {
                duende.steal(vitima);
            }

            panel.repaint();
        });
    }

    private static boolean verificarChegada(Duende duende) {
        if (duende.getPosition() >= 29) {
            System.out.println("Duende " + duende.getId() + " chegou ao final!");
            return true;
        }
        return false;
    }

    private static void pausaVisualizacao() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void exibirResultadosFinais(List<Duende> duendes) {
        System.out.println("\nResultado Final:");
        duendes.forEach(d ->
                System.out.println("Duende " + d.getId() + ": " + d.getMoney() + " Dinheiros")
        );

        SwingUtilities.invokeLater(() -> {
            StringBuilder resultados = new StringBuilder("Resultado Final:\n");
            duendes.forEach(d ->
                    resultados.append("Duende ").append(d.getId())
                            .append(": ").append(d.getMoney())
                            .append(" Dinheiros\n")
            );

            JOptionPane.showMessageDialog(null,
                    resultados.toString(),
                    "Fim da Simulação",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
