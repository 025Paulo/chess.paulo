package piece;

import com.chess.game.GamePanel;

public class Queen extends Piece {

    public Queen(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("piece/w-queen.png");
        } else {
            image = getImage("piece/b-queen.png");
        }
    }
}
