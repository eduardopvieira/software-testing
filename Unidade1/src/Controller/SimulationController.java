package Controller;

import model.dao.UsuarioDAO;
import model.domain.Cluster;
import model.domain.Duende;
import model.domain.GuardiaoDoHorizonte;
import model.domain.datastructure.TreeMapAdaptado;
import model.domain.interfaces.EntityOnHorizon;
import view.SimulationView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class SimulationController {

    private static volatile boolean simulacaoEmAndamento = false;

    private final String loginUsuario;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final TreeMapAdaptado tma;
    private final SimulationView panel;
    private final SimulationEngine motor;

    public int iteracao = 0;

    public SimulationController(int numDuendes, double maxHorizon, String loginUsuario) {
        if (simulacaoEmAndamento) {
            this.tma = null;
            this.panel = null;
            this.motor = null;
            this.loginUsuario = null;
            return;
        }
        simulacaoEmAndamento = true;

        this.loginUsuario = loginUsuario;

        SimulationSetup setup = new SimulationSetup(numDuendes, maxHorizon);
        this.tma = setup.criarCenario();
        GuardiaoDoHorizonte guardiao = setup.criarGuardião();
        this.tma.treeMapPrincipal.put(guardiao.getPosition(), guardiao);

        this.panel = criarEExibirJanela(new ArrayList<>(tma.treeMapPrincipal.values()), maxHorizon);
        this.motor = new SimulationEngine(tma, maxHorizon);
    }

    public void iniciar() {
        if (tma == null) return;

        if (loginUsuario != null) {
            usuarioDAO.incrementarSimulacoesExecutadas(loginUsuario);
        }
        runGameLoop();
    }

    private void runGameLoop() {
        new Thread(() -> {
            int resultado = 0;  // 0 = em andamento, 1 = vitória, -1 = derrota

            while ((resultado == 0) && simulacaoEmAndamento) {
                iteracao++;
                System.out.println("\nIteração " + iteracao);

                motor.executarRodada();

                panel.updateEntidades(new ArrayList<>(tma.treeMapPrincipal.values()), iteracao);
                panel.repaint();

                resultado = motor.verificarCondicaoDeTermino();

                if (resultado == 0) {
                    pausaVisualizacao();
                }
            }
            finalizarSimulacao(resultado);
        }).start();
    }

    private void finalizarSimulacao(int resultado) {
        if (!simulacaoEmAndamento) return;

        panel.repaint();

        if (loginUsuario != null && resultado == 1) {
            usuarioDAO.incrementarPontuacao(loginUsuario);
        }

        exibirResultadosFinais(new ArrayList<>(tma.treeMapPrincipal.values()));

        simulacaoEmAndamento = false;
        System.out.println("Simulação finalizada. Trava liberada.");
    }

    private SimulationView criarEExibirJanela(ArrayList<EntityOnHorizon> entities, double maxHorizon) {
        SimulationView panel = new SimulationView(entities, maxHorizon);
        JFrame simulationFrame = new JFrame("Simulação de Duendes & Clusters");
        simulationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        simulationFrame.add(panel);

        simulationFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                simulacaoEmAndamento = false;
                System.out.println("Janela de simulação fechada. Trava liberada.");
            }
        });

        simulationFrame.pack();
        simulationFrame.setLocationRelativeTo(null);
        simulationFrame.setVisible(true);
        return panel;
    }

    private void exibirResultadosFinais(List<EntityOnHorizon> entidades) {
        entidades.sort((e1, e2) -> Long.compare(e2.getCoins(), e1.getCoins()));
        StringBuilder resultados = new StringBuilder("Fim da Simulação!\n\nResultado Final:\n");
        for (EntityOnHorizon entidade : entidades) {
            if (entidade instanceof Duende) {
                resultados.append(String.format("Duende %d: %d Moedas (Pos: %.1f)\n", entidade.getId(), entidade.getCoins(), entidade.getPosition()));
            } else if (entidade instanceof Cluster) {
                resultados.append(String.format("Cluster c/ %d duendes: %d Moedas (Pos: %.1f)\n", ((Cluster) entidade).getQuantityDuendes(), entidade.getCoins(), entidade.getPosition()));
            } else if (entidade instanceof GuardiaoDoHorizonte) {
                resultados.append(String.format("Guardião %d: %d Moedas (Pos: %.1f)\n", entidade.getId(), entidade.getCoins(), entidade.getPosition()));
            }
        }
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                resultados.toString(), "Fim da Simulação", JOptionPane.INFORMATION_MESSAGE));
    }

    private void pausaVisualizacao() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
