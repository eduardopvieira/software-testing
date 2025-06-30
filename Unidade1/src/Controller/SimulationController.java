package Controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.dao.UsuarioDAO;
import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;
import model.domain.Duende;
import model.domain.Cluster;
import model.domain.interfaces.EntityOnHorizon;
import view.SimulationView;

public class SimulationController {
    private static double maxHorizon;
    private GuardiaoDoHorizonte guardiao;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void iniciarSimulacao(int numDuendes, double maxHorizon, String loginUsuario) {
        if (numDuendes <= 1 || numDuendes > 20) {
            throw new IllegalArgumentException("A quantidade de duendes deve ser de 2 a 20.");
        }
        if (maxHorizon <= 0) {
            throw new IllegalArgumentException("O horizonte máximo deve ser maior que zero.");
        }
        if (loginUsuario != null) {
            usuarioDAO.incrementarSimulacoesExecutadas(loginUsuario);
        }

        SimulationController.maxHorizon = maxHorizon;

        List<Duende> duendes = criarDuendes(numDuendes);
        TreeMapAdaptado tma = inicializarTreeMap(duendes);

        double posGuardiao = maxHorizon * 0.8;

        this.guardiao = new GuardiaoDoHorizonte(numDuendes + 1, posGuardiao);
        tma.treeMapPrincipal.put(this.guardiao.getPosition(), this.guardiao);

        SimulationView panel = criarEExibirJanela(new ArrayList<>(tma.treeMapPrincipal.values()));
        executarLogicaSimulacao(tma, panel, loginUsuario);
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

    public void executarLogicaSimulacao(TreeMapAdaptado tma, SimulationView panel, String loginUsuario) {
        new Thread(() -> {
            runGameLoop(tma, panel, loginUsuario);
        }).start();
    }

    private void runGameLoop(TreeMapAdaptado tma, SimulationView panel, String loginUsuario) {
        boolean jogoAcabou = false;
        int iteracao = 0;

        while (!jogoAcabou) {
            iteracao++;
            System.out.println("\nIteração " + iteracao);

            jogoAcabou = executarRodada(tma);

            panel.updateEntidades(new ArrayList<>(tma.treeMapPrincipal.values()));
            panel.repaint();

            if (!jogoAcabou) {
                pausaVisualizacao();
            }
        }

        finalizarSimulacao(loginUsuario, tma);
    }

    private boolean executarRodada(TreeMapAdaptado tma) {
        List<EntityOnHorizon> entidadesDaRodada = new ArrayList<>(tma.treeMapPrincipal.values());
        entidadesDaRodada.remove(this.guardiao);

        for (EntityOnHorizon entidade : entidadesDaRodada) {
            if (tma.treeMapPrincipal.containsValue(entidade)) {
                processarTurno(entidade, tma);

                if (entidade.getPosition() >= maxHorizon) {
                    System.out.println("FIM DE JOGO! " + TreeMapAdaptado.getNomeEntidade(entidade) + " atingiu o horizonte.");
                    return true;
                }
            }
        }

        processarTurnoGuardiao(tma);

        if (this.guardiao.getPosition() >= maxHorizon) {
            System.out.println("FIM DE JOGO! O Guardião atingiu o horizonte.");
            return true;
        }

        return false;
    }

    private void finalizarSimulacao(String loginUsuario, TreeMapAdaptado tma) {
        if (loginUsuario != null) {
            usuarioDAO.incrementarPontuacao(loginUsuario);
            System.out.println("Pontuação incrementada para o usuário: " + loginUsuario);
        }
        exibirResultadosFinais(new ArrayList<>(tma.treeMapPrincipal.values()));
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
            tma.treeMapPrincipal.put(novaPosicao, entidade);
            return entidade; // <<< CORREÇÃO: Retorna a própria entidade que se moveu.
        } else {
            System.out.println("COLISÃO em " + novaPosicao + "! " + TreeMapAdaptado.getNomeEntidade(entidade) + " vs " + TreeMapAdaptado.getNomeEntidade(ocupante));
            tma.treeMapPrincipal.remove(novaPosicao);

            if (entidade instanceof Duende && ocupante instanceof Duende) {
                // Cenário 1: Duende colide com Duende
                Cluster novoCluster = new Cluster((Duende) entidade, (Duende) ocupante);
                tma.treeMapPrincipal.put(novaPosicao, novoCluster);
                System.out.println("Resultado: Novo cluster formado.");
                return novoCluster;

            } else if (entidade instanceof Cluster && ocupante instanceof Cluster) {
                Cluster clusterBase = (Cluster) entidade;
                clusterBase.addToCluster(ocupante);
                tma.treeMapPrincipal.put(novaPosicao, clusterBase);
                System.out.println("Resultado: Clusters se fundiram.");
                return clusterBase;

            } else {
                Cluster clusterExistente;
                EntityOnHorizon outro;

                if (entidade instanceof Cluster) {
                    clusterExistente = (Cluster) entidade;
                    outro = ocupante;
                } else {
                    clusterExistente = (Cluster) ocupante;
                    outro = entidade;
                }
                clusterExistente.addToCluster(outro);
                tma.treeMapPrincipal.put(novaPosicao, clusterExistente);
                System.out.println("Resultado: Duende foi adicionado ao cluster.");
                return clusterExistente;
            }
        }
    }

    public void processarTurnoGuardiao(TreeMapAdaptado tma) {
        if (this.guardiao.getCoins() <= 0) {
            return;
        }

        double posAntiga = this.guardiao.getPosition();

        this.guardiao.move(maxHorizon);
        double novaPosicao = this.guardiao.getPosition();

        tma.treeMapPrincipal.remove(posAntiga);

        EntityOnHorizon ocupante = tma.treeMapPrincipal.get(novaPosicao);

        if (ocupante instanceof Cluster) {
            Cluster clusterVitima = (Cluster) ocupante;
            System.out.println("GUARDIÃO " + this.guardiao.getId() + " COLIDIU COM UM CLUSTER NA POSIÇÃO " + novaPosicao);

            long moedasAbsorvidas = clusterVitima.getCoins();
            this.guardiao.addCoins(moedasAbsorvidas);

            tma.treeMapPrincipal.remove(novaPosicao);
            System.out.println("Cluster com " + clusterVitima.getQuantityDuendes() + " duendes foi ELIMINADO. Guardião absorveu " + moedasAbsorvidas + " moedas.");
        }

        tma.treeMapPrincipal.put(novaPosicao, this.guardiao);
    }

    public boolean verificarCondicaoDeTermino(TreeMapAdaptado tma, String loginUsuario) {
        for (EntityOnHorizon entidade : tma.treeMapPrincipal.values()) {
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
            } else if (entidade instanceof Cluster) {
                Cluster c = (Cluster) entidade;
                linha = String.format("Cluster c/ %d duendes: %d Moedas (Pos: %.1f)\n", c.getQuantityDuendes(), c.getCoins(), c.getPosition());
            } else if (entidade instanceof GuardiaoDoHorizonte) {
                GuardiaoDoHorizonte g = (GuardiaoDoHorizonte) entidade;
                linha = String.format("Guardião %d: %d Moedas (Pos: %.1f)\n", g.getId(), g.getCoins(), g.getPosition());
            } else {
                linha = "Entidade desconhecida.\n";
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
