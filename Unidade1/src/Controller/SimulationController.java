package Controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import datastructure.TreeMapAdaptado;
import model.Cluster;
import model.Duende;
import model.interfaces.EntityOnHorizon;
import view.SimulationView;


public class SimulationController {
    private static long maxCoins;
    private static double maxHorizon;

    public static void iniciarSimulacao(int numDuendes, double maxHorizon, long maxCoins) {
        if (numDuendes <= 1 || numDuendes > 20) {
            throw new IllegalArgumentException("A quantidade de duendes deve ser de 2 a 20.");
        }
        if (maxHorizon <= 0) {
            throw new IllegalArgumentException("O horizonte máximo deve ser maior que zero.");
        }
        if (maxCoins <= 0) {
            throw new IllegalArgumentException("O número máximo de moedas deve ser maior que zero.");
        }
        if (maxCoins > numDuendes * 1000000L) {
            throw new IllegalArgumentException("O ponto de parada não pode ser maior que o valor total de moedas na simulação.");
        }

        SimulationController.maxCoins = maxCoins;
        SimulationController.maxHorizon = maxHorizon;

        List<Duende> duendes = criarDuendes(numDuendes);
        TreeMapAdaptado tma = inicializarTreeMap(duendes);
        // A view é criada com a lista inicial de entidades do mapa
        SimulationView panel = criarEExibirJanela(new ArrayList<>(tma.treeMapPrincipal.values()));
        executarLogicaSimulacao(duendes, tma, panel);
    }

    public static List<Duende> criarDuendes(int quantidade) {
        if (quantidade <= 1 || quantidade > 20) {
            throw new IllegalArgumentException("A quantidade de duendes deve ser de 2 a 20.");
        }
        List<Duende> duendes = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            duendes.add(new Duende(i));
        }
        return duendes;
    }

    public static TreeMapAdaptado inicializarTreeMap(List<Duende> duendes) {
        if (duendes == null || duendes.isEmpty()) {
            throw new IllegalArgumentException("A lista de duendes não pode ser nula ou vazia.");
        }
        TreeMapAdaptado tma = new TreeMapAdaptado();
        // Assume-se que TreeMapAdaptado tem um método para adição inicial sem colisões
        duendes.forEach(tma::addEntity);
        return tma;
    }

    public static SimulationView criarEExibirJanela(List<EntityOnHorizon> entidades) {
        SimulationView panel = new SimulationView(entidades);
        JFrame simulationFrame = new JFrame("Simulação de Duendes & Clusters");
        simulationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simulationFrame.add(panel);
        simulationFrame.pack();
        simulationFrame.setLocationRelativeTo(null);
        simulationFrame.setVisible(true);
        return panel;
    }

    public static void executarLogicaSimulacao(List<Duende> duendes, TreeMapAdaptado tma, SimulationView panel) {
        new Thread(() -> {
            int iteracao = 0;
            boolean jogoAcabou = false;

            while (!jogoAcabou) {
                iteracao++;
                System.out.println("\nIteração " + iteracao);

                // --- FASE 1: MOVIMENTO E CLUSTERIZAÇÃO ---
                for (Duende duende : duendes) {
                    if (tma.treeMapPrincipal.containsValue(duende)) {
                        processarMovimentoECluster(duende, tma);
                    }
                }

                // --- FASE 2: ROUBO ---
                // Após todos se moverem, os clusters realizam suas ações de roubo.
                processarRoubos(tma);

                // --- FASE 3: ATUALIZAÇÃO DA VIEW E VERIFICAÇÃO DE TÉRMINO ---
                panel.updateEntidades(new ArrayList<>(tma.treeMapPrincipal.values()));
                panel.repaint();

                jogoAcabou = verificarCondicaoDeTermino(tma);
                pausaVisualizacao();
            }

            exibirResultadosFinais(new ArrayList<>(tma.treeMapPrincipal.values()));
        }).start();
    }

    public static void processarMovimentoECluster(Duende duende, TreeMapAdaptado tma) {
        double posAntiga = duende.getPosition();
        tma.treeMapPrincipal.remove(posAntiga);

        duende.move(maxHorizon);
        double novaPosicao = duende.getPosition();

        EntityOnHorizon ocupante = tma.treeMapPrincipal.get(novaPosicao);

        if (ocupante == null) {
            tma.treeMapPrincipal.put(novaPosicao, duende);
        } else {
            System.out.println("COLISÃO na posição " + novaPosicao + "!");
            tma.treeMapPrincipal.remove(novaPosicao);

            Cluster clusterResultante;
            if (ocupante instanceof Duende) {
                System.out.println("Duende " + duende.getId() + " colidiu com Duende " + ((Duende) ocupante).getId());
                clusterResultante = new Cluster(duende, (Duende) ocupante);
                System.out.println("Novo cluster formado com " + clusterResultante.getCoins() + " moedas.");
            } else { // Ocupante é um Cluster
                System.out.println("Duende " + duende.getId() + " colidiu com um cluster.");
                clusterResultante = (Cluster) ocupante;
                clusterResultante.addToCluster(duende);
                System.out.println("Cluster agora tem " + clusterResultante.getQuantityDuendes() + " duendes e " + clusterResultante.getCoins() + " moedas.");
            }

            tma.treeMapPrincipal.put(clusterResultante.getPosition(), clusterResultante);
        }
    }

    public static void processarRoubos(TreeMapAdaptado tma) {
        List<EntityOnHorizon> entidadesAtivas = new ArrayList<>(tma.treeMapPrincipal.values());
        for (EntityOnHorizon entidade : entidadesAtivas) {
            if (entidade instanceof Cluster) {
                Cluster clusterAtacante = (Cluster) entidade;
                EntityOnHorizon vitima = tma.findNearestEntity(clusterAtacante);

                if (vitima != null) {
                    System.out.println("Cluster na posição " + clusterAtacante.getPosition() + " vai roubar seu vizinho.");
                    long moedasRoubadas = vitima.beingStealed();
                    clusterAtacante.addCoins(moedasRoubadas);
                    System.out.println("Cluster agora possui " + clusterAtacante.getCoins() + " moedas.");
                }
            }
        }
    }

    public static boolean verificarCondicaoDeTermino(TreeMapAdaptado tma) {
        for (EntityOnHorizon entidade : tma.treeMapPrincipal.values()) {
            if (entidade.getCoins() >= maxCoins) {
                System.out.println("FIM DE JOGO! Uma entidade atingiu " + entidade.getCoins() + " moedas.");
                return true;
            }
            if (entidade.getPosition() >= maxHorizon) {
                System.out.println("FIM DE JOGO! Uma entidade atingiu o horizonte máximo: " + entidade.getPosition());
                return true;
            }
        }
        return false;
    }

    public static void exibirResultadosFinais(List<EntityOnHorizon> entidades) {
        System.out.println("\nResultado Final:");

        entidades.sort((e1, e2) -> Long.compare(e2.getCoins(), e1.getCoins()));

        StringBuilder resultados = new StringBuilder("Fim da Simulação!\n\nResultado Final:\n");
        for (EntityOnHorizon entidade : entidades) {
            String linha;
            if (entidade instanceof Duende) {
                Duende d = (Duende) entidade;
                linha = String.format("Duende %d: %d Moedas (Pos: %.1f)\n", d.getId(), d.getCoins(), d.getPosition());
            } else {
                Cluster c = (Cluster) entidade;
                linha = String.format("Cluster c/ %d duendes: %d Moedas (Pos: %.1f)\n", c.getQuantityDuendes(), c.getCoins(), c.getPosition());
            }
            System.out.print(linha);
            resultados.append(linha);
        }

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                resultados.toString(),
                "Fim da Simulação",
                JOptionPane.INFORMATION_MESSAGE));
    }

    public static void pausaVisualizacao() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public static double getMaxHorizon() {
        return maxHorizon;
    }

    public static void setMaxHorizon(double maxHorizon) {
        SimulationController.maxHorizon = maxHorizon;
    }
}
