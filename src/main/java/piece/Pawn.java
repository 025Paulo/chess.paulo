package piece;

import com.chess.game.GamePanel;
import com.chess.game.Type;

public class Pawn extends Piece {

    public Pawn(int color, int col, int row) {
        super(color, col, row);

        type = Type.PAWN;

        if(color == GamePanel.WHITE) {
            image = getImage("piece/w-pawn.png");
        }
        else {
            image = getImage("piece/b-pawn.png");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {

        if (isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow) == false) {

            //Define o movimento baseado na cor
            int moveValue;
            if (color== GamePanel.WHITE) {
                moveValue = -1;
            } else {
                moveValue = 1;
            }

            hittingP = getHittingP(targetCol, targetRow);

            //andando 1 casa
            if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
                return true;
            }
            //andando 2 casas
            if (targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && moved == false &&
                    pieceIsOnStraightLine(targetCol, targetRow) == false) {
                return true;
            }
            //capturando peça na diagonal
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null &&
                    hittingP.color != color) {
                return true;
            }

            // En passant
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == targetCol && piece.row == preRow && piece.twoStepped == true ) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
