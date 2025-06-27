package com.chess.game;

import javax.swing.*;
import java.awt.*;

// Cria uma classe personalizada que é um JPanel. Você vai usar isso como o painel do jogo.
public class GamePanel extends JPanel {

    // Define largura do painel como constante (1100 px).
    public static final int WIDTH = 1100;
    // Define altura do painel como constante (800 px).
    public static final int HEIGHT = 800;

    // Construtor do painel.
    public GamePanel() {
        // Diz ao layout manager que o painel deve ter esse tamanho.
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // Define o fundo preto do painel
        setBackground(Color.black);
    }
}
