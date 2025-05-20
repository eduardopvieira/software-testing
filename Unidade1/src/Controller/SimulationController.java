package Controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import datastructure.TreeMapAdaptado;
import model.Duende;
import view.SimulationView;

public class SimulationController {
    private static long maxCoins;
    private static int maxHorizon;

    public static void iniciarSimulacao(int numDuendes, int maxHorizon, long maxCoins) {
        //! Os valores de entrada já estão sendo verificados na função
        //! callSimulacao() da classe MenuView.

        SimulationController.maxCoins = maxCoins;
        SimulationController.maxHorizon = maxHorizon;

        List<Duende> duendes = criarDuendes(numDuendes);
        TreeMapAdaptado tma = inicializarTreeMap(duendes);
        SimulationView panel = criarEExibirJanela(duendes);
        executarLogicaSimulacao(duendes, tma, panel);
    }

    public static List<Duende> criarDuendes(int quantidade) {

        //! Teste de pré-condição
        if (quantidade <= 1 || quantidade > 20) {
            throw new IllegalArgumentException("A quantidade de duendes deve ser de 2 a 20.");
        }

        List<Duende> duendes = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            duendes.add(new Duende(i));
        }

        //! Teste de pós condição
        if (duendes.size() != quantidade) {
            throw new IllegalStateException("Erro ao criar a lista de duendes.");
        }

        return duendes;
    }

    public static TreeMapAdaptado inicializarTreeMap(List<Duende> duendes) {
        //! Teste de pré-condição
        if (duendes == null || duendes.isEmpty()) {
            throw new IllegalArgumentException("A lista de duendes não pode ser nula ou vazia.");
        }

        TreeMapAdaptado tma = new TreeMapAdaptado();
        duendes.forEach(tma::addDuende);

        //! Teste de pós-condição
        if (tma.treeMapPrincipal.isEmpty()) {
            throw new IllegalStateException("Erro ao inicializar o TreeMapAdaptado.");
        }
        return tma;
    }

    private static SimulationView criarEExibirJanela(List<Duende> duendes) {

        //! Teste de pré-condição
        if (duendes == null || duendes.isEmpty()) {
            throw new IllegalArgumentException("A lista de duendes não pode ser nula ou vazia.");
        }

        SimulationView panel = new SimulationView(duendes);
        JFrame simulationFrame = new JFrame("Simulação de Duendes");
        simulationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simulationFrame.add(panel);
        simulationFrame.pack();
        simulationFrame.setLocationRelativeTo(null);
        simulationFrame.setVisible(true);

        //! Não é necessario teste de pós-condição
        return panel;
    }

    private static void executarLogicaSimulacao(List<Duende> duendes, TreeMapAdaptado tma, SimulationView panel) {
        //! Verificação dos parametros já é feita na função iniciarSimulacao() dessa mesma classe

        new Thread(() -> {
            int iteracao = 0;
            boolean alguemChegou = false;

            while (!alguemChegou) {
                iteracao++;
                System.out.println("\nIteração " + iteracao);

                for (Duende duende : duendes) {

                    if (verificarChegada(duende, SimulationController.maxCoins)) {
                        alguemChegou = true;

                        break;
                    }
                    moverERoubar(duende, tma, panel);

                    pausaVisualizacao();

                }

                if (alguemChegou) {
                    exibirResultadosFinais(duendes);
                    break;
                }
            }
        }).start();
    }


    public static void moverERoubar(Duende duende, TreeMapAdaptado tma, SimulationView panel) {

        //! Teste de pré-condição
        if (duende == null || tma == null || panel == null) {
            throw new IllegalArgumentException("Parâmetros não podem ser nulos.");
        }

        SwingUtilities.invokeLater(() -> {
            tma.treeMapPrincipal.remove(duende.getPosition());
            duende.move();
            tma.addDuende(duende);

            Duende vitima = tma.findNearestDuende(duende);
            if (vitima != null && vitima != duende) {
                duende.steal(vitima);
            }
        });

        //!Não há teste de pós-condição. Função é void.
        panel.repaint();
    }

    public static boolean verificarChegada(Duende duende, Long maxCoins) {

        //! Teste de pré-condição
        if (duende == null) {
            throw new IllegalArgumentException("Duende não pode ser nulo.");
        }

        if (duende.getCoins() >= maxCoins) {
            System.out.println("Duende " + duende.getId() + " atingiu " + duende.getCoins() + 
                            " moedas (limite: " + maxCoins + ")");
            return true;

        } else if (duende.getPosition() >= maxHorizon) {
            System.out.println("Duende " + duende.getId() + " atingiu o horizonte máximo (" + maxHorizon + ")");
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

        List<Duende> sorted = new ArrayList<>(duendes);
        sorted.sort((d1, d2) -> Double.compare(d2.getCoins(), d1.getCoins()));

        sorted.forEach(d -> System.out.println("Duende " + d.getId() + ": " + d.getCoins() + " Moedas"));

        SwingUtilities.invokeLater(() -> {
            StringBuilder resultados = new StringBuilder("Resultado Final:\n");
            sorted.forEach(d -> resultados.append("Duende ").append(d.getId())
                    .append(": ").append(d.getCoins())
                    .append(" Moedas\n"));

            JOptionPane.showMessageDialog(null,
                    resultados.toString(),
                    "Fim da Simulação",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }


    public static int getMaxHorizon() {
        return maxHorizon;
    }
}
