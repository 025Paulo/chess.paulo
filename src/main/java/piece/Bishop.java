package piece;

import com.chess.game.GamePanel;

public class Bishop extends Piece {

    public Bishop(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("piece/w-bishop.png");
        } else {
            image = getImage("piece/b-bishop.png");
        }
    }
}
