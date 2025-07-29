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
}
