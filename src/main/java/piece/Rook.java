package piece;

import com.chess.game.GamePanel;
import com.chess.game.Type;

public class Rook extends Piece {
    public Rook(int color, int col, int row) {
        super(color, col, row);

        type = Type.ROOK;

        if (color == GamePanel.WHITE) {
            image = getImage("piece/w-rook.png");
        } else {
            image = getImage("piece/b-rook.png");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {

        if (isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol, targetRow) == false) {
            //garante que o movimento seja em linha reta ou na mesma coluna
            if (targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    return true;
                }
            }
        }
        return false;
    }
}
