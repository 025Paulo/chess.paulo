package com.chess.game;

import com.chess.game.GamePanel;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class Xadrez {

    public static void main(String[] args) {

            JFrame window = new JFrame("Simple chess");
            // Fecha o programa quando clicar no X
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Usuário não pode redimensionar.
            window.setResizable(false);

            // Adiciona um painel na janela do jogo
            GamePanel gp = new GamePanel();
            // Adiciona o painel na janela
            window.add(gp);
            // Ajusta a janela para o tamanho do painel (usa o preferredSize)
            window.pack();

            // Centraliza a janela na tela.
            window.setLocationRelativeTo(null);
            // Torna a janela visível
            window.setVisible(true);

            gp.launchGame();
        }
    }
