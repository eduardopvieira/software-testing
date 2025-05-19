package Controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import datastructure.TreeMapAdaptado;
import model.Duende;
import model.Horizon;
import view.SimulationView;

public class SimulationController {
    public static void iniciarSimulacao(int numDuendes, int maxHorizon) {
        List<Duende> duendes = criarDuendes(numDuendes);
        TreeMapAdaptado tma = inicializarTreeMap(duendes);
        Horizon.setRightLimit(maxHorizon);
        SimulationView panel = criarEExibirJanela(duendes, maxHorizon);
        executarLogicaSimulacao(duendes, tma, panel);
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

    private static SimulationView criarEExibirJanela(List<Duende> duendes, int maxHorizon) {
        SimulationView panel = new SimulationView(duendes, maxHorizon);
        JFrame simulationFrame = new JFrame("Simulação de Duendes");
        simulationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simulationFrame.add(panel);
        simulationFrame.pack();
        simulationFrame.setLocationRelativeTo(null);
        simulationFrame.setVisible(true);
        return panel;
    }

    private static void executarLogicaSimulacao(List<Duende> duendes, TreeMapAdaptado tma, SimulationView panel) {
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

    private static boolean processarMovimentoDuendes(List<Duende> duendes, TreeMapAdaptado tma, SimulationView panel) {
        for (Duende duende : duendes) {
            moverERoubar(duende, tma, panel);

            if (verificarChegada(duende)) {
                return true;
            }

            pausaVisualizacao();
        }
        return false;
    }

    private static void moverERoubar(Duende duende, TreeMapAdaptado tma, SimulationView panel) {
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
        if (duende.getPosition() >= Horizon.getRightLimit() - 1) {
            System.out.println("Duende " + duende.getId() + " chegou ao final!");
            System.out.println(Horizon.getRightLimit() - 1);
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
                System.out.println("Duende " + d.getId() + ": " + d.getOuro() + " Dinheiros")
        );

        SwingUtilities.invokeLater(() -> {
            StringBuilder resultados = new StringBuilder("Resultado Final:\n");
            duendes.forEach(d ->
                    resultados.append("Duende ").append(d.getId())
                            .append(": ").append(d.getOuro())
                            .append(" Dinheiros\n")
            );

            JOptionPane.showMessageDialog(null,
                    resultados.toString(),
                    "Fim da Simulação",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
