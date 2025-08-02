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
        }

        return false;
    }
}
