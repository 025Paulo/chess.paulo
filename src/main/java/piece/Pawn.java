package piece;

import com.chess.game.GamePanel;

public class Pawn extends Piece {

    public Pawn(int color, int col, int row) {
        super(color, col, row);

        if(color == GamePanel.WHITE) {
            image = getImage("piece/w-pawn.png");
        }
        else {
            image = getImage("piece/b-pawn.png");
        }
    }
}
