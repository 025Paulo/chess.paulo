package com.chess.game;

import piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// Cria uma classe personalizada que é um JPanel. Você vai usar isso como o painel do jogo.
public class GamePanel extends JPanel implements Runnable {

    // Define largura do painel como constante (1100 px).
    public static final int WIDTH = 1100;
    // Define altura do painel como constante (800 px).
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // Peças
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP;
    public static Piece castlingP;


    // Cor
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    boolean canMove;
    boolean validSquare;
    boolean promotion;

    // Construtor do painel.
    public GamePanel() {
        // Diz ao layout manager que o painel deve ter esse tamanho.
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // Define o fundo preto do painel
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        // pieces = source, simPieces = target
        copyPieces(pieces, simPieces);
    }

    // Cria uma nova Thread
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void setPieces() {

        //Brancas
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        //Pretas
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        // Limpa a lista target antes de copiar os elementos da source.
        target.clear();
        for (int i = 0; i<source.size(); i++) {
            target.add(source.get(i));
        }
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

    //Logica do jogo: Exemplo = mover peça, detectar colisão
    private void update() {

        if (promotion) {
            promoting();
        }
        else {
            if (mouse.pressed) {
                if (activeP == null) {

                    for (Piece piece : simPieces) {
                        // Garante que o jogador só consiga selecionar peças da sua própria cor
                        if (piece.color == currentColor &&
                                //Transforma a coordenada pixel x do clique do mouse em uma coluna do tabuleiro (dividindo pelo tamanho de cada quadrado)
                                piece.col == mouse.x/Board.SQUARE_SIZE &&
                                //Transforma a coordenada pixel y do clique do mouse em uma linha do tabuleiro.
                                piece.row == mouse.y/Board.SQUARE_SIZE) {

                            activeP = piece;
                        }
                    }
                }
                else {
                    simulate();
                }
            }

            if (!mouse.pressed) {
                if (activeP != null) {

                    if (validSquare) {

                        //Atualiza a lista se uma peça tiver sido capturada e removida durante o metodo simulate
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }

                        if (canPromote()) {
                            promotion = true;
                        }
                        else {
                            changePlayer();
                        }
                    } else {
                        //O movimento nao é valido, é para resetar
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }

        if (mouse.pressed) {
            if (activeP == null) {

                for (Piece piece : simPieces) {
                    // Garante que o jogador só consiga selecionar peças da sua própria cor
                    if (piece.color == currentColor &&
                        //Transforma a coordenada pixel x do clique do mouse em uma coluna do tabuleiro (dividindo pelo tamanho de cada quadrado)
                        piece.col == mouse.x/Board.SQUARE_SIZE &&
                        //Transforma a coordenada pixel y do clique do mouse em uma linha do tabuleiro.
                        piece.row == mouse.y/Board.SQUARE_SIZE) {

                        activeP = piece;
                    }
                }
            }
            else {
                simulate();
            }
        }

        if (!mouse.pressed) {
            if (activeP != null) {

                if (validSquare) {

                    //Atualiza a lista se uma peça tiver sido capturada e removida durante o metodo simulate
                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();
                    if (castlingP != null) {
                        castlingP.updatePosition();
                    }

                    if (canPromote()) {
                        promotion = true;
                    }
                    else {
                        changePlayer();
                    }
                } else {
                    //O movimento nao é valido, é para resetar
                    copyPieces(pieces, simPieces);
                    activeP.resetPosition();
                    activeP = null;
                }
            }
        }
    }

    private void simulate() {

        canMove = false;
        validSquare = false;

        // Reseta a lista de peças em todo loop
        // Isso é para restaurar a peça removida durante a simulaçao
        copyPieces(pieces, simPieces);

        // Reseta a posiçao das peça do roque
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        //se a peça esta sendo segurrada, atualiza a posiçao
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        if (activeP.canMove(activeP.col, activeP.row)) {

            canMove = true;

            //Se acertar outra peça, remove ela da lista
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkCastling();

            validSquare = true;
        }

    }
    private void checkCastling() {

        if (castlingP != null) {
            //move a torre da coluna 0 para 3 casas a direita
            if (castlingP.col == 0) {
                castlingP.col += 3;
            }
            // move a torre da coluna 7 para 2 casas a esquerda
            else if(castlingP.col == 7) {
                castlingP.col -= 2;
            }
            //garante que a torre vai aparecer na tela no novo lugar
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private void changePlayer() {

        if (currentColor == WHITE) {
            currentColor = BLACK;
            // Reseta o two step status das pretas
            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }
        }
        else {
            currentColor = WHITE;
            // Reseta o two step status das branca
            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private boolean canPromote() {

        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }

    private void promoting() {

        if (mouse.pressed) {
            for (Piece piece : promoPieces) {
                if (piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row)); break;
                        case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row)); break;
                        case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
                        case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
                        default: break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        //Board
        board.draw(g2);
        //Peças
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        //se o jogador estiver segurando um peão, o simulate vai atualizar a posição com base no mouse,
        // e esse if vai desenhar um destaque  semitransparente na casa onde o peão está sendo arrastado
        if (activeP != null) {
            if (canMove) {
                g2.setColor(Color.white);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                //Desenha um retângulo branco semitransparente na posição da peça que está sendo arrastada
                g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            activeP.draw(g2); //desenha a peça arrastada sobre esse quadrado para ela ficar visivel
        }
        // mensagem de status
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON) ;
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if (promotion) {
            g2.drawString("Promote to:", 840, 150);
            for (Piece piece : promoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {

            if (currentColor == WHITE) {
                g2.drawString("White's Turn", 840, 550);
            } else {
                g2.drawString("Black's Turn", 840, 250);
            }
        }
    }
}
