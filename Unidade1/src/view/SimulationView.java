package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import model.Duende;

public class SimulationView extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int GROUND_Y = 350;
    
    private final List<Duende> duendes;
    private final HashMap<Integer, BufferedImage> sprites;

    private int rightHorizonLimit;
    private static int SCALE;
    
    public SimulationView(List<Duende> duendes, int rightHorizonLimit) {
        this.duendes = duendes;
        this.sprites = new HashMap<>();
        this.rightHorizonLimit = rightHorizonLimit;
        SimulationView.SCALE = (WIDTH - 100) / 10;

        loadSprites(); // Ou generateSprites() para versão programática
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(173, 216, 230));
    }

    private void loadSprites() {
        try {
            // Tenta carregar usando caminho relativo ao classpath
            String resourcePath = "/resources/sprites/duende.png";
            BufferedImage originalSprite = ImageIO.read(getClass().getResource(resourcePath));

            if (originalSprite != null) {
                // Se encontrou, cria versões coloridas para cada duende
                for (Duende d : duendes) {
                    sprites.put(d.getId(), colorizeSprite(originalSprite, d.getId()));
                }
                System.out.println("Sprites carregados com sucesso do classpath!");
            } else {
                System.out.println("Arquivo de sprite não encontrado no classpath: " + resourcePath);
                generateFallbackSprites();
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Erro ao carregar sprite: " + e.getMessage());
            generateFallbackSprites();
        }
    }

    private void generateFallbackSprites() {
        System.out.println("Gerando sprites alternativos...");
        for (Duende d : duendes) {
            // Cria um sprite simples programaticamente
            BufferedImage fallback = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = fallback.createGraphics();

            // Desenha um duende básico
            Color color = Color.getHSBColor((d.getId() * 0.618f) % 1.0f, 0.7f, 0.9f);
            g2d.setColor(color);
            g2d.fillOval(10, 0, 30, 30); // Cabeça
            g2d.fillRect(15, 30, 20, 20); // Corpo

            g2d.setColor(Color.WHITE);
            g2d.fillOval(15, 10, 8, 8); // Olho esquerdo
            g2d.fillOval(27, 10, 8, 8); // Olho direito

            g2d.dispose();
            sprites.put(d.getId(), fallback);
        }
    }

private BufferedImage colorizeSprite(BufferedImage original, int id) {
    // Cria uma cópia da imagem original
    BufferedImage colored = new BufferedImage(
        original.getWidth(), 
        original.getHeight(), 
        BufferedImage.TYPE_INT_ARGB
    );
    
    // Aplica uma cor baseada no ID
    float hue = (id * 0.618f) % 1.0f; // Usa o número áureo para distribuição
    Color tint = Color.getHSBColor(hue, 0.7f, 0.9f);
    
    // Pinta a cópia com a tonalidade
    Graphics2D graph = colored.createGraphics();
    graph.drawImage(original, 0, 0, null);
    
    // Aplica um filtro de cor
    for (int y = 0; y < colored.getHeight(); y++) {
        for (int x = 0; x < colored.getWidth(); x++) {
            Color pixelColor = new Color(original.getRGB(x, y), true);
            // Mantém o alpha (transparência)
            if (pixelColor.getAlpha() > 0) {
                // Mistura a cor original com a tonalidade
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
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        drawBackground(g2d);
        drawGroundScale(g2d);
        
        synchronized (duendes) {
            duendes.forEach(d -> drawDuendeWithSprite(g2d, d));
        }
        
        drawInfo(g2d);
    }
    
    private void drawDuendeWithSprite(Graphics2D g2d, Duende duende) {
        int x = (int)(duende.getPosition() * SCALE) + 25; // Posição ajustada
        int y = GROUND_Y - 70;
        
        BufferedImage sprite = sprites.getOrDefault(duende.getId(), null);
        if (sprite != null) {
            g2d.drawImage(sprite, x, y, 50, 50, null);
        } else {
            drawDuendeFallback(g2d, duende);
        }
        
        // Informações do duende
        g2d.setColor(Color.BLACK);
        g2d.drawString("#" + duende.getId(), x + 15, y + 65);
        g2d.setColor(Color.YELLOW);
        g2d.drawString("$" + (duende.getOuro()/1000) + "k", x + 10, y + 80);
    }
    
    private void drawDuendeFallback(Graphics2D g2d, Duende duende) {
        // Método fallback caso os sprites não carreguem
        int x = (int)(duende.getPosition() * SCALE) + 50 - 15;
        int y = GROUND_Y - 60;
        
        Color color = new Color(
            50 + (duende.getId() * 30) % 200,
            50 + (duende.getId() * 70) % 200,
            50 + (duende.getId() * 110) % 200
        );
        
        g2d.setColor(color);
        g2d.fillOval(x, y - 30, 30, 30);
        g2d.fillRect(x + 5, y, 20, 30);
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x + 5, y - 25, 8, 8);
        g2d.fillOval(x + 17, y - 25, 8, 8);
    }
    
    
    private void drawBackground(Graphics2D g2d) {
        // Céu
        g2d.setColor(new Color(173, 216, 230));
        g2d.fillRect(0, 0, getWidth(), GROUND_Y);
        
        // Grama
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, GROUND_Y, getWidth(), getHeight() - GROUND_Y);
        
        // Linha de chegada
        g2d.setColor(Color.RED);
        g2d.fillRect((rightHorizonLimit - 1) * SCALE + 50, GROUND_Y - 150, SCALE, 150);
        g2d.setColor(Color.WHITE);
        g2d.drawString("CHEGADA", (rightHorizonLimit - 1) * SCALE + 50, GROUND_Y - 160);
    }
    
    private void drawGroundScale(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        int lineOffset = 50;
        
        // Desenha a linha de escala
        g2d.drawLine(lineOffset, GROUND_Y + 20, WIDTH-lineOffset, GROUND_Y + 20);
        
        // Desenha os marcadores e números
        int step = (int) rightHorizonLimit / 10;
        double proportionalCoef = (WIDTH - 2 * lineOffset) / (double) rightHorizonLimit;

        for (int i = 0; i <= rightHorizonLimit; i += step) {
            int x = (int) (i * proportionalCoef) + lineOffset;
            g2d.drawLine(x, GROUND_Y + 15, x, GROUND_Y + 25);
            g2d.drawString(String.valueOf(i), x - 5, GROUND_Y + 40);
        }
    }
    
    
    private void drawInfo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Simulação de Duendes - Movendo e Roubando Ouro", 20, 20);
    }
    
    public static void showSimulation(List<Duende> duendes, int rightHorizonLimit) {
        JFrame frame = new JFrame("Simulação de Duendes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SimulationView(duendes, rightHorizonLimit));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
