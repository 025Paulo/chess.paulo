package com.chess.game;

import javax.swing.*;
import java.awt.*;

// Cria uma classe personalizada que é um JPanel. Você vai usar isso como o painel do jogo.
public class GamePanel extends JPanel implements Runnable {

    // Define largura do painel como constante (1100 px).
    public static final int WIDTH = 1100;
    // Define altura do painel como constante (800 px).
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;

    // Construtor do painel.
    public GamePanel() {
        // Diz ao layout manager que o painel deve ter esse tamanho.
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // Define o fundo preto do painel
        setBackground(Color.black);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        // GAME LOOP
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

        }

    }

    private void update() {
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}
