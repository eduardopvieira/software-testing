package Controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.domain.datastructure.TreeMapAdaptado;
import model.domain.Cluster;
import model.domain.Duende;
import model.domain.interfaces.EntityOnHorizon;
import view.SimulationView;

public class SimulationController {
    private static long maxCoins;
    private static double maxHorizon;

    public void iniciarSimulacao(int numDuendes, double maxHorizon, long maxCoins) {
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
        duendes.forEach(tma::addDuendeInicial);
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

    public void executarLogicaSimulacao(List<Duende> duendes, TreeMapAdaptado tma, SimulationView panel) {
        new Thread(() -> {
            int iteracao = 0;
            boolean jogoAcabou = false;

            while (!jogoAcabou) {
                iteracao++;
                System.out.println("\nIteração " + iteracao);

                for (Duende duende : duendes) {
                    if (tma.treeMapPrincipal.containsValue(duende)) {
                        processarTurno(duende, tma);
                    }
                }

                panel.updateEntidades(new ArrayList<>(tma.treeMapPrincipal.values()));
                panel.repaint();

                jogoAcabou = verificarCondicaoDeTermino(tma);
                pausaVisualizacao();
            }

            exibirResultadosFinais(new ArrayList<>(tma.treeMapPrincipal.values()));
        }).start();
    }

    public void processarTurno(EntityOnHorizon entidade, TreeMapAdaptado tma) {
        // --- PARTE 1: MOVIMENTO ---
        EntityOnHorizon ator = logicaMovimento(entidade, tma);

        // --- PARTE 2: ROUBO ---
        logicaRoubo(ator, tma);
    }

    public void logicaRoubo(EntityOnHorizon entidade, TreeMapAdaptado tma) {
        EntityOnHorizon vitima = tma.findNearestEntidade(entidade);
        if (vitima != null && vitima != entidade) {
            entidade.steal(vitima);
            System.out.println(entidade.getId() + " agora possui " + entidade.getCoins() + " moedas.");
        } else {
            System.out.println("Nenhum vizinho para roubar.");
        }
    }

    public EntityOnHorizon logicaMovimento(EntityOnHorizon entidade, TreeMapAdaptado tma) {
        double posAntiga = entidade.getPosition();
        tma.treeMapPrincipal.remove(posAntiga);

        entidade.move(maxHorizon);
        double novaPosicao = entidade.getPosition();

        EntityOnHorizon ocupante = tma.treeMapPrincipal.get(novaPosicao);

        if (ocupante == null) {
            // Cenário 0: Posição livre. A entidade simplesmente se move.
            tma.treeMapPrincipal.put(novaPosicao, entidade);
            return entidade; // <<< CORREÇÃO: Retorna a própria entidade que se moveu.
        } else {
            // Cenário de COLISÃO!
            System.out.println("COLISÃO em " + novaPosicao + "! " + TreeMapAdaptado.getNomeEntidade(entidade) + " vs " + TreeMapAdaptado.getNomeEntidade(ocupante));
            tma.treeMapPrincipal.remove(novaPosicao);

            if (entidade instanceof Duende && ocupante instanceof Duende) {
                // Cenário 1: Duende colide com Duende
                Cluster novoCluster = new Cluster((Duende) entidade, (Duende) ocupante);
                tma.treeMapPrincipal.put(novaPosicao, novoCluster);
                System.out.println("Resultado: Novo cluster formado.");
                return novoCluster; // <<< CORREÇÃO: Retorna o NOVO cluster.

            } else if (entidade instanceof Cluster && ocupante instanceof Cluster) {
                // Cenário 2: Cluster colide com Cluster
                Cluster clusterBase = (Cluster) entidade;
                clusterBase.addToCluster(ocupante);
                tma.treeMapPrincipal.put(novaPosicao, clusterBase);
                System.out.println("Resultado: Clusters se fundiram.");
                return clusterBase; // <<< CORREÇÃO: Retorna o cluster que absorveu o outro.

            } else {
                // Cenário 3: Duende colide com Cluster (em qualquer ordem)
                Cluster clusterExistente;
                EntityOnHorizon outro;

                if (entidade instanceof Cluster) {
                    clusterExistente = (Cluster) entidade;
                    outro = ocupante;
                } else { // Ocupante deve ser o Cluster
                    clusterExistente = (Cluster) ocupante;
                    outro = entidade;
                }
                clusterExistente.addToCluster(outro);
                tma.treeMapPrincipal.put(novaPosicao, clusterExistente);
                System.out.println("Resultado: Duende foi adicionado ao cluster.");
                return clusterExistente; // <<< CORREÇÃO: Retorna o cluster atualizado.
            }
        }
    }

    public boolean verificarCondicaoDeTermino(TreeMapAdaptado tma) {
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

    public void exibirResultadosFinais(List<EntityOnHorizon> entidades) {
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
