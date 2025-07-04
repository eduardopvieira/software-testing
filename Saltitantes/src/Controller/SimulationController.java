package Controller;

import model.dao.UsuarioDAO;
import model.datastructure.TreeMapAdaptado;
import model.entities.Cluster;
import model.entities.Duende;
import model.entities.GuardiaoDoHorizonte;
import model.entities.interfaces.EntityOnHorizon;
import view.SimulationView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class SimulationController {

    private final String loginUsuario;
    private final UsuarioDAO usuarioDAO;
    private final TreeMapAdaptado tma;
    private final SimulationView panel;
    private final SimulationEngine motor;

    public int iteracao = 0;

    public SimulationController(String loginUsuario, UsuarioDAO usuarioDAO, SimulationEngine motor, SimulationView panel, TreeMapAdaptado tma) {
        this.loginUsuario = loginUsuario;
        this.usuarioDAO = usuarioDAO;
        this.motor = motor;
        this.panel = panel;
        this.tma = tma;
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

            while (resultado == 0) {
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

        panel.repaint();

        if (loginUsuario != null && resultado == 1) {
            usuarioDAO.incrementarPontuacao(loginUsuario);
        }

        exibirResultadosFinais(new ArrayList<>(tma.treeMapPrincipal.values()));

    }


    //TESTE DE UI!!! NÃO ESTÁ SENDO COBERTA!!
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

    //TESTE DE UI!!! NAO ESTÁ SENDO COBERTA!!
    private void pausaVisualizacao() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
