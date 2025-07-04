package com.chess.game;

import java.awt.*;

public class Board {

    final int MAX_COL = 8; // 8 colunas
    final int MAX_ROW = 8; // 8 linhas
    public static final int SQUARE_SIZE = 100; // Cada quadrado tem 100x100 pixels
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2; // Metade do tamanho

    // metodo desenha o tabuleiro
    public void draw(Graphics2D g2) {

        // C é o que define qual cor usar para cada casa: 0 = Claro / 1 = Escuro
        int c = 0;

        // vai de row = 0 até row = 7 → percorre as 8 linhas do tabuleiro.
        for(int row = 0; row < MAX_ROW; row++) {

            // para cada linha, percorre as 8 colunas → cada casa da linha.
            for (int col = 0; col < MAX_COL; col++) {

                // Primeira casa da linha: se c é 0, desenha clara, dps inverte c → a próxima casa vai ter a cor oposta.
                if (c == 0) {
                    g2.setColor(new Color(210,165,125));
                    c = 1; // Alterna para escura na próxima casa
                }
                else {
                    g2.setColor(new Color(175,115,70));
                    c = 0; // Alterna para clara na próxima casa
                }
                // desenha um retangulo preenchido: col * SQUARE_SIZE → posição X || row * SQUARE_SIZE → posição Y. || Largura = altura = SQUARE_SIZE.
                g2.fillRect(col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE );
            }

            // Inverte, depois de terminar 1 linha, é necessário inverter a cor inicial da próxima linha.
            if (c == 0) {
                c = 1;
            }
            else {
                c = 0;
            }
        }
    }
}
