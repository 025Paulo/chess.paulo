package piece;

import com.chess.game.GamePanel;

public class Knight extends Piece {

    public Knight(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("piece/w-knight.png");
        } else {
            image = getImage("piece/b-knight.png");
        }
    }
    public boolean canMove(int targetCol, int targelRow) {

        if (isWithinBoard(targetCol, targelRow)) {
            //Cavalo pode mover se o moviment ratio de col e row for 1:2 ou 2:1
            if (Math.abs(targetCol - preCol) * Math.abs(targelRow - preRow) == 2) {
                if(isValidSquare(targetCol,targelRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
