package view;

import javax.swing.*;

import Controller.SimulationController;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import model.domain.Cluster;
import model.domain.Duende;
import model.domain.GuardiaoDoHorizonte;
import model.domain.interfaces.EntityOnHorizon;

public class SimulationView extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int GROUND_Y = 350;
    private static final int TABLE_WIDTH = 250;
    private static final int TABLE_HEIGHT = 150;

    private List<EntityOnHorizon> entidades;
    private final HashMap<Integer, BufferedImage> sprites;
    private BufferedImage guardiaoSprite;
    private final double maxHorizon;
    private int iteracaoAtual;

    public SimulationView(List<EntityOnHorizon> entidadesIniciais, double maxHorizon) {
        this.entidades = new ArrayList<>(entidadesIniciais);
        this.sprites = new HashMap<>();
        this.maxHorizon = maxHorizon;
        this.iteracaoAtual = 0;

        loadSprites(entidadesIniciais);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(173, 216, 230));
    }

    public void updateEntidades(List<EntityOnHorizon> novasEntidades, int iteracao) {
        synchronized (this.entidades) {
            this.entidades.clear();
            this.entidades.addAll(novasEntidades);
            this.iteracaoAtual = iteracao;
        }
    }

    private int normalizePosition(double position) {
        double relativePosition = position / this.maxHorizon;
        return 50 + (int)(relativePosition * (WIDTH - 100));
    }

    private void loadSprites(List<EntityOnHorizon> entidadesIniciais) {
        try {
            String resourcePath = "/model/domain/resources/sprites/duende.png";
            String guardiaoPath = "/model/domain/resources/sprites/guardiao-com-pa.png";
            BufferedImage originalSprite = ImageIO.read(getClass().getResource(resourcePath));

            this.guardiaoSprite = ImageIO.read(getClass().getResource(guardiaoPath));

            if (originalSprite != null) {
                for (EntityOnHorizon entidade : entidadesIniciais) {
                    if (entidade instanceof Duende) {
                        Duende d = (Duende) entidade;
                        sprites.put(d.getId(), colorizeSprite(originalSprite, d.getId()));
                    }
                }
                System.out.println("Sprites carregados com sucesso do classpath!");
            } else {
                System.out.println("Arquivo de sprite não encontrado no classpath: " + resourcePath);
                generateFallbackSprites(entidadesIniciais);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Erro ao carregar sprite: " + e.getMessage());
            generateFallbackSprites(entidadesIniciais);
        }
    }

    private void generateFallbackSprites(List<EntityOnHorizon> entidadesIniciais) {
        System.out.println("Gerando sprites alternativos...");
        for (EntityOnHorizon entidade : entidadesIniciais) {
            if (entidade instanceof Duende) {
                Duende d = (Duende) entidade;
                BufferedImage fallback = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = fallback.createGraphics();
                Color color = Color.getHSBColor((d.getId() * 0.618f) % 1.0f, 0.7f, 0.9f);
                g2d.setColor(color);
                g2d.fillOval(10, 0, 30, 30); g2d.fillRect(15, 30, 20, 20);
                g2d.setColor(Color.WHITE); g2d.fillOval(15, 10, 8, 8); g2d.fillOval(27, 10, 8, 8);
                g2d.dispose();
                sprites.put(d.getId(), fallback);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawBackground(g2d);
        drawGroundScale(g2d);
        drawIterationCounter(g2d);

        synchronized (entidades) {
            for (EntityOnHorizon entidade : entidades) {
                if (entidade instanceof Duende) {
                    drawDuendeWithSprite(g2d, (Duende) entidade);
                } else if (entidade instanceof Cluster) {
                    drawCluster(g2d, (Cluster) entidade);
                } else if (entidade instanceof GuardiaoDoHorizonte) {
                    drawGuardiao(g2d, (GuardiaoDoHorizonte) entidade);
                }
            }
        }

        drawTop5Table(g2d);
    }

    private void drawIterationCounter(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Iteração: " + this.iteracaoAtual, 10, 25);
    }

    private void drawGuardiao(Graphics2D g2d, GuardiaoDoHorizonte guardiao) {
        int x = normalizePosition(guardiao.getPosition());
        int y = GROUND_Y - 70;

        if (this.guardiaoSprite != null) {
            g2d.drawImage(this.guardiaoSprite, x, y, 50, 50, null);
        }

        g2d.setColor(Color.WHITE);
        g2d.drawString("#" + guardiao.getId() + " Guardião", x - 10, y + 65);
        g2d.setColor(new Color(255, 165, 0));
        g2d.drawString("$" + (guardiao.getCoins() / 1000) + "k", x + 10, y + 80);
    }

    private void drawDuendeWithSprite(Graphics2D g2d, Duende duende) {
        int x = normalizePosition(duende.getPosition());
        int y = GROUND_Y - 70;

        BufferedImage sprite = sprites.get(duende.getId());
        if (sprite != null) {
            g2d.drawImage(sprite, x, y, 50, 50, null);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("#" + duende.getId(), x + 15, y + 65);
        g2d.setColor(Color.YELLOW);
        g2d.drawString("$" + (duende.getCoins() / 1000) + "k", x + 10, y + 80);
    }

    private void drawCluster(Graphics2D g2d, Cluster cluster) {
        int x = normalizePosition(cluster.getPosition());
        int spriteHeight = 50;
        int stackOverlap = 35;

        List<Duende> duendesNaTorre = cluster.getDuendes();

        for (int i = 0; i < duendesNaTorre.size(); i++) {
            Duende duendeDaVez = duendesNaTorre.get(i);
            BufferedImage sprite = sprites.get(duendeDaVez.getId());

            if (sprite != null) {
                int y = (GROUND_Y - 70) - (i * stackOverlap);
                g2d.drawImage(sprite, x, y, spriteHeight, spriteHeight, null);
            }
        }

        int infoY = GROUND_Y + 15;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String clusterText = String.format("Cluster (%d)", cluster.getQuantityDuendes());
        g2d.drawString(clusterText, x, infoY);

        g2d.setColor(Color.YELLOW);
        String coinsText = String.format("$%dk", cluster.getCoins() / 1000);
        g2d.drawString(coinsText, x, infoY + 15);
    }

    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(new Color(173, 216, 230));
        g2d.fillRect(0, 0, getWidth(), GROUND_Y);
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, GROUND_Y, getWidth(), getHeight() - GROUND_Y);
    }

    private void drawGroundScale(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        int lineOffset = 50;
        g2d.drawLine(lineOffset, GROUND_Y + 20, WIDTH - lineOffset, GROUND_Y + 20);

        int step = (int) this.maxHorizon / 10;
        if (step == 0) step = 1;
        double proportionalCoef = (WIDTH - 2 * lineOffset) / this.maxHorizon;

        for (int i = 0; i <= this.maxHorizon; i += step) {
            int x = (int) (i * proportionalCoef) + lineOffset;
            g2d.drawLine(x, GROUND_Y + 15, x, GROUND_Y + 25);
            g2d.drawString(String.valueOf(i), x - 5, GROUND_Y + 40);
        }
    }

    private void drawTop5Table(Graphics2D g2d) {
        List<EntityOnHorizon> sorted;
        synchronized(entidades) {
            sorted = new ArrayList<>(this.entidades);
        }
        sorted.sort((e1, e2) -> Long.compare(e2.getCoins(), e1.getCoins()));

        int tableX = WIDTH - TABLE_WIDTH - 20;
        int tableY = 40;

        g2d.setColor(new Color(240, 240, 240, 200));
        g2d.fillRoundRect(tableX, tableY, TABLE_WIDTH, TABLE_HEIGHT, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(tableX, tableY, TABLE_WIDTH, TABLE_HEIGHT, 10, 10);

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Top 5 Entidades", tableX + 10, tableY + 20);

        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        int yOffset = 40;

        int limit = Math.min(5, sorted.size());
        for (int i = 0; i < limit; i++) {
            EntityOnHorizon e = sorted.get(i);
            String line = switch (e) {
                case Duende d -> String.format("#Duende %d: Gold [%dk] - Pos [%.1f]",
                        d.getId(), d.getCoins() / 1000, d.getPosition());
                case Cluster c -> String.format("#Cluster (%d): Gold [%dk] - Pos [%.1f]",
                        c.getQuantityDuendes(), c.getCoins() / 1000, c.getPosition());
                case GuardiaoDoHorizonte guard -> String.format("#Guardião %d: Gold [%dk] - Pos [%.1f]",
                        guard.getId(), guard.getCoins() / 1000, guard.getPosition());
                case null, default -> "Entidade desconhecida";
            };
            g2d.drawString(line, tableX + 10, tableY + yOffset);
            yOffset += 20;
        }
    }

    private BufferedImage colorizeSprite(BufferedImage original, int id) {
        BufferedImage colored = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        float hue = (id * 0.618f) % 1.0f;
        Color tint = Color.getHSBColor(hue, 0.7f, 0.9f);

        Graphics2D graph = colored.createGraphics();
        graph.drawImage(original, 0, 0, null);

        for (int y = 0; y < colored.getHeight(); y++) {
            for (int x = 0; x < colored.getWidth(); x++) {
                Color pixelColor = new Color(original.getRGB(x, y), true);
                if (pixelColor.getAlpha() > 0) {
                    int r = (int)(pixelColor.getRed() * 0.5 + tint.getRed() * 0.5);
                    int g = (int)(pixelColor.getGreen() * 0.5 + tint.getGreen() * 0.5);
                    int b = (int)(pixelColor.getBlue() * 0.5 + tint.getBlue() * 0.5);
                    Color newColor = new Color(r, g, b, pixelColor.getAlpha());
                    colored.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        graph.dispose();
        return colored;
    }
}
