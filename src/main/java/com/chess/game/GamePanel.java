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
    Piece activeP, checkingP;
    public static Piece castlingP;


    // Cor
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;

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
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {

        // GAME LOPP
        // drawInterval → Tempo entre frames desejado em nanossegundos.
        // Se FPS = 60, então drawInterval = 1 bilhão / 60 ≈ 16.666.666 ns (ou seja, ~16,67 ms por frame).
        double drawInterval = 1000000000 / FPS;
        // delta garante que o jogo atualize na frequência certa, mesmo que o loop rode mais rápido ou mais devagar.
        double delta = 0;
        // Marca o tempo do loop anterior
        long lastTime = System.nanoTime();
        long currentTime;

        // Roda infinitamente enquanto o jogo estiver ativo
        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
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
        } else if (gameover == false && stalemate == false) {
            if (mouse.pressed) {
                if (activeP == null) {

                    for (Piece piece : simPieces) {
                        // Garante que o jogador só consiga selecionar peças da sua própria cor
                        if (piece.color == currentColor &&
                                //Transforma a coordenada pixel x do clique do mouse em uma coluna do tabuleiro (dividindo pelo tamanho de cada quadrado)
                                piece.col == mouse.x / Board.SQUARE_SIZE &&
                                //Transforma a coordenada pixel y do clique do mouse em uma linha do tabuleiro.
                                piece.row == mouse.y / Board.SQUARE_SIZE) {

                            activeP = piece;
                        }
                    }
                } else {
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

                        if (isKingInCheck() && isCheckmate()) {
                            gameover = true;
                        }
                        else if (isStalemate() && isKingInCheck() == false) {
                            stalemate = true;
                        }
                        else { // O jogo continua
                            if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
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
                            piece.col == mouse.x / Board.SQUARE_SIZE &&
                            //Transforma a coordenada pixel y do clique do mouse em uma linha do tabuleiro.
                            piece.row == mouse.y / Board.SQUARE_SIZE) {

                        activeP = piece;
                    }
                }
            } else {
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

            if (isIllegal(activeP) == false && opponentCanCaptureKing() == false) {
                validSquare = true;
            }

            validSquare = true;
        }
    }

    private boolean isIllegal(Piece king) {

        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean opponentCanCaptureKing() {

        Piece king = getKing(false);

        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingInCheck() {

        Piece king = getKing(true);

        if (activeP.canMove(king.col, king.row)) {
            checkingP = activeP;
            return true;
        } else {
            checkingP = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent) {

        Piece king = null;

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }

    private boolean isCheckmate() {

        Piece king = getKing(true);

        if (kingCanMove(king)) {
            return false;
        }
        else {
            // Checa se tem como bloquear o caminho do check

            // checa a posiçao da peça dando check e do rei
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if (colDiff == 0) {
                // A peça dando check esta na vertical
                if (checkingP.row < king.row) {
                    //a peça esta acima do rei
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    //a peça esta abaixo do rei
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (rowDiff == 0) {
                // a peça dando check esta na horizontal
                if (checkingP.col < king.col) {
                    // A peça esta para esquerda
                    for (int col = checkingP.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col) {
                    // A peça esta para direita
                    for (int col = checkingP.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (colDiff == rowDiff) {
                // a peça ta dando check na diagonal
                if (checkingP.row < king.row) {
                    // a peça dando cheque esta acima do rei
                    if (checkingP.col < king.col) {
                        // a peça dando cheque esta para esquerda acima
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        // a peça dando cheque esta para cima na direita
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    // A peça dando cheque esta abaixo do rei
                    if (checkingP.col < king.col) {
                        // a peça dando cheque esta para baixo esquerda
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        // a peça dando cheque esta para baixo na direita
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean kingCanMove(Piece king) {
        // Simula se tem algum lugar que o rei pode se mover
        if (isValidMove(king, -1, -1)) {
            return true;
        }
        if (isValidMove(king, 0, -1)) {
            return true;
        }
        if (isValidMove(king, 1, -1)) {
            return true;
        }
        if (isValidMove(king, -1, 0)) {
            return true;
        }
        if (isValidMove(king, 1, 0)) {
            return true;
        }
        if (isValidMove(king, -1, 1)) {
            return true;
        }
        if (isValidMove(king, 0, 1)) {
            return true;
        }
        if (isValidMove(king, 1, 1)) {
            return true;
        }

        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus) {

        boolean isValidMove = false;

        //atualiza a posicao do rei
        king.col += colPlus;
        king.row += rowPlus;

        if (king.canMove(king.col, king.row)) {

            if (king.hittingP != null) {
                simPieces.remove(king.hittingP.getIndex());
            }
            if (!isIllegal(king)) {
                isValidMove = true;
            }
        }
        //reseta a posiçao do rei
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }
    private boolean isStalemate() {

        int count = 0;
        // Conta o numero de peças
        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                count++;
            }
        }

        if (count == 1) {
            if (kingCanMove(getKing(true)) == false) {
                return true;
            }
        }

        return false;
    }

    private void checkCastling() {

        if (castlingP != null) {
            //move a torre da coluna 0 para 3 casas a direita
            if (castlingP.col == 0) {
                castlingP.col += 3;
            }
            // move a torre da coluna 7 para 2 casas a esquerda
            else if (castlingP.col == 7) {
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
        } else {
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
                if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case ROOK:
                            simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                            break;
                        case QUEEN:
                            simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
                            break;
                        default:
                            break;
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

        Graphics2D g2 = (Graphics2D) g;
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
                if (isIllegal(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    //Desenha um retângulo branco semitransparente na posição da peça que está sendo arrastada
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                } else {
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    //Desenha um retângulo branco semitransparente na posição da peça que está sendo arrastada
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }

                activeP.draw(g2); //desenha a peça arrastada sobre esse quadrado para ela ficar visivel
            }
            // mensagem de status
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
                    if (checkingP != null && checkingP.color == BLACK) {
                        g2.setColor(Color.red);
                        g2.drawString("The King", 840, 650);
                        g2.drawString("Is in check", 840, 700);
                    }
                } else {
                    g2.drawString("Black's Turn", 840, 250);
                    if (checkingP != null && checkingP.color == WHITE) {
                        g2.setColor(Color.red);
                        g2.drawString("The King", 840, 100);
                        g2.drawString("Is in check", 840, 150);
                    }
                }
            }

            if (gameover) {
                String s = "";
                if (currentColor == WHITE) {
                    s = "White wins";
                }
                else {
                    s = "Black wins";
                }
                g2.setFont(new Font("Arial", Font.PLAIN, 90));
                g2.setColor(Color.green);
                g2.drawString(s, 200, 420);
            }
        }
    }

