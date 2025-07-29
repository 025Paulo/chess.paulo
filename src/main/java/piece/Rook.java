package piece;

import com.chess.game.GamePanel;

public class Rook extends Piece {
    public Rook(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("piece/w-rook.png");
        } else {
            image = getImage("piece/b-rook.png");
        }
    }
}
