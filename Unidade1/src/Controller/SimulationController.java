package Controller;

import java.lang.reflect.Array;
import java.security.Guard;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;
import model.domain.Duende;
import model.domain.Cluster;
import model.domain.interfaces.EntityOnHorizon;
import view.SimulationView;

public class SimulationController {
    private static double maxHorizon;
    private GuardiaoDoHorizonte guardiao;

    public void iniciarSimulacao(int numDuendes, double maxHorizon, String loginUsuario) {
        if (numDuendes <= 1 || numDuendes > 20) {
            throw new IllegalArgumentException("A quantidade de duendes deve ser de 2 a 20.");
        }
        if (maxHorizon <= 0) {
            throw new IllegalArgumentException("O horizonte máximo deve ser maior que zero.");
        }

        SimulationController.maxHorizon = maxHorizon;

        List<Duende> duendes = criarDuendes(numDuendes);
        TreeMapAdaptado tma = inicializarTreeMap(duendes);

        double posGuardiao = maxHorizon * 0.8;

        this.guardiao = new GuardiaoDoHorizonte(numDuendes + 1, posGuardiao);
        tma.treeMapPrincipal.put(this.guardiao.getPosition(), this.guardiao);

        SimulationView panel = criarEExibirJanela(new ArrayList<>(tma.treeMapPrincipal.values()));
        executarLogicaSimulacao(tma, panel);
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

    public void executarLogicaSimulacao(TreeMapAdaptado tma, SimulationView panel) {
        new Thread(() -> {
            int iteracao = 0;
            boolean jogoAcabou = false;

            while (!jogoAcabou) {
                iteracao++;
                System.out.println("\nIteração " + iteracao);

                ArrayList<EntityOnHorizon> entities = new ArrayList<>(tma.treeMapPrincipal.values());

                for (EntityOnHorizon entidade : entities) {
                    processarTurno(entidade, tma);
                }

                // --- TURNO DO GUARDIÃO ---
                //processarTurnoGuardiao(tma);

                panel.updateEntidades(entities);
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
        if (!(ator instanceof GuardiaoDoHorizonte)) {
            logicaRoubo(ator, tma);
        }
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

            if (entidade instanceof GuardiaoDoHorizonte && ocupante instanceof Cluster) {
                Cluster clusterVitima = (Cluster) ocupante;
                GuardiaoDoHorizonte guardiao = (GuardiaoDoHorizonte) entidade;

                System.out.println("GUARDIÃO " + guardiao.getId() + " COLIDIU COM UM CLUSTER NA POSIÇÃO " + novaPosicao);

                long moedasAbsorvidas = clusterVitima.getCoins();
                guardiao.addCoins(moedasAbsorvidas);

                //tma.treeMapPrincipal.remove(novaPosicao);
                System.out.println("Cluster com " + clusterVitima.getQuantityDuendes() + " duendes foi ELIMINADO. Guardião absorveu " + moedasAbsorvidas + " moedas.");

                tma.treeMapPrincipal.put(novaPosicao, guardiao);

                return guardiao;
            } 
            else if (entidade instanceof Cluster && ocupante instanceof GuardiaoDoHorizonte) {
                Cluster clusterVitima = (Cluster) entidade;
                GuardiaoDoHorizonte guardiao = (GuardiaoDoHorizonte) ocupante;

                System.out.println("CLUSTER " + guardiao.getId() + " COLIDIU COM UM GUARDIÃO NA POSIÇÃO " + novaPosicao);

                long moedasAbsorvidas = clusterVitima.getCoins();
                guardiao.addCoins(moedasAbsorvidas);

                //tma.treeMapPrincipal.remove(novaPosicao);
                System.out.println("Cluster com " + clusterVitima.getQuantityDuendes() + " duendes foi ELIMINADO. Guardião absorveu " + moedasAbsorvidas + " moedas.");

                tma.treeMapPrincipal.put(novaPosicao, guardiao);
                return guardiao;
            } 
            else if (entidade instanceof GuardiaoDoHorizonte && ocupante instanceof Duende) {
                Duende duende = (Duende) ocupante;
                GuardiaoDoHorizonte guardiao = (GuardiaoDoHorizonte) entidade;

                System.out.println("GUARDIÃO " + guardiao.getId() + " COLIDIU COM UM DUENDE NA POSIÇÃO " + novaPosicao);

                long moedasRoubadas = duende.beingStealed();
                guardiao.addCoins(moedasRoubadas);

                //tma.treeMapPrincipal.remove(novaPosicao);
                System.out.println("Duende " + duende.getId() + " foi ELIMINADO pelo guardião. Moedas roubadas: " + moedasRoubadas);

                tma.treeMapPrincipal.put(novaPosicao, guardiao);
                return guardiao; // <<< CORREÇÃO: Retorna o Guardião que roubou.
            }
            else if (entidade instanceof Duende && ocupante instanceof GuardiaoDoHorizonte) {
                // Cenário 2: Duende colide com Guardião
                Duende duende = (Duende) entidade;
                GuardiaoDoHorizonte guardiao = (GuardiaoDoHorizonte) ocupante;

                System.out.println("DUENDE " + duende.getId() + " COLIDIU COM UM GUARDIÃO NA POSIÇÃO " + novaPosicao);

                long moedasRoubadas = duende.beingStealed();
                guardiao.addCoins(moedasRoubadas);

                //tma.treeMapPrincipal.remove(novaPosicao);
                System.out.println("Duende " + duende.getId() + " foi ELIMINADO pelo guardião. Moedas roubadas: " + moedasRoubadas);

                tma.treeMapPrincipal.put(novaPosicao, guardiao);
                return guardiao; // <<< CORREÇÃO: Retorna o Guardião que roubou.
            } 
            else if (entidade instanceof Duende && ocupante instanceof Duende) {
                // Cenário 1: Duende colide com Duende
                Cluster novoCluster = new Cluster((Duende) entidade, (Duende) ocupante);
                tma.treeMapPrincipal.put(novaPosicao, novoCluster);
                System.out.println("Resultado: Novo cluster formado.");
                return novoCluster; // <<< CORREÇÃO: Retorna o NOVO cluster.

            }
            else if (entidade instanceof Cluster && ocupante instanceof Cluster) {
                Cluster clusterBase = (Cluster) entidade;
                clusterBase.addToCluster(ocupante);
                tma.treeMapPrincipal.put(novaPosicao, clusterBase);
                System.out.println("Resultado: Clusters se fundiram.");
                return clusterBase; // <<< CORREÇÃO: Retorna o cluster que absorveu o outro.

            }
            else {
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

    public boolean verificarCondicaoDeTermino(TreeMapAdaptado tma) {
        if (tma.treeMapPrincipal.size() <= 2) {
            System.out.println("FIM DE JOGO!");
            
            return true;
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
            } else if (entidade instanceof Cluster) { // <<< ADICIONE "ELSE IF" AQUI
                Cluster c = (Cluster) entidade;
                linha = String.format("Cluster c/ %d duendes: %d Moedas (Pos: %.1f)\n", c.getQuantityDuendes(), c.getCoins(), c.getPosition());
            } else if (entidade instanceof GuardiaoDoHorizonte) { // <<< ADICIONE ESTA CONDIÇÃO
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
