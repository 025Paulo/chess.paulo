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
}
