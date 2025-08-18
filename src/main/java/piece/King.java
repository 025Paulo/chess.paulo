package piece;

import com.chess.game.GamePanel;

public class King extends Piece {

    public King(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("piece/w-king.png");
        } else {
            image = getImage("piece/b-king.png");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        //Verifica se o destino está dentro dos limites do tabuleiro
        if (isWithinBoard(targetCol, targetRow)) {
            //Math.abs = verifica se o movimento foi de exatamente uma casa em linha reta
            // Se targetCol for 1 e preCol for 4 da -3, entao nao se pode fazer esse movimento
            if (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||
                    //Permite apenas movimentos diagonais de uma casa
                    Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {

                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
            // CASTLE
            if (moved == false) {

                // Castle para direita -> verifica se tem peças entre o rei e a torre e se a torre nao se moveu
                if (targetCol == preCol+2 && targetRow == preRow && pieceIsOnStraightLine(targetCol,targetRow) == false) {
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece.col == preCol+3 && piece.row == preRow && piece.moved == false) {
                            GamePanel.castlingP = piece;
                            return true;
                        }
                    }
                }

                // Castle para esquerda -> faz a mesma verificação mas com mais casas que tem no caminho e se a torre nao se moveu
                if (targetCol == preCol-2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    // array para armazenar peças que possam estar entre o rei e torre
                    Piece p[] = new Piece[2];
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece.col == preCol-3 && piece.row == targetRow) {
                            //peça na coluna preCol-3
                            p[0] = piece;
                        }
                        if (piece.col == preCol-4 && piece.row == targetRow) {
                            //a torre na posicao preCol-4
                            p[1] = piece;
                        }
                        //== false signfica que nao tem peça entre o rei e a torre e que a torre nao se moveu
                        if (p[0] == null && p[1] != null && p[1].moved == false) {
                            GamePanel.castlingP = p[1];
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
