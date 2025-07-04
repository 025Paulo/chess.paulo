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
    Board board = new Board();

    // Construtor do painel.
    public GamePanel() {
        // Diz ao layout manager que o painel deve ter esse tamanho.
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // Define o fundo preto do painel
        setBackground(Color.black);
    }

    // Cria uma nova Thread
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        // GAME LOPP
        // drawInterval → Tempo entre frames desejado em nanossegundos.
        // Se FPS = 60, então drawInterval = 1 bilhão / 60 ≈ 16.666.666 ns (ou seja, ~16,67 ms por frame).
        double drawInterval = 1000000000/FPS;
        // delta garante que o jogo atualize na frequência certa, mesmo que o loop rode mais rápido ou mais devagar.
        double delta = 0;
        // Marca o tempo do loop anterior
        long lastTime = System.nanoTime();
        long currentTime;

        // Roda infinitamente enquanto o jogo estiver ativo
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

    //Logica do jogo: Exemplo > mover personagem, detectar colisão, atualizar pontuação.m
    private void update() {
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        board.draw(g2);

    }

}
