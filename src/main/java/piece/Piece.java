package piece;

import com.chess.game.Board;
import com.chess.game.GamePanel;
import com.chess.game.Type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece {

    public Type type;
    // A imagem da peça (o ícone que será desenhado na tela).
    public BufferedImage image;
    // A posição em pixels da peça na tela (para desenho).
    public int x, y;
    // col, row = A posição da peça em colunas e linhas no tabuleiro (0 a 7).
    // preCol, preRow = Armazena a posição anterior da peça (antes de mover). Pode ser útil para desfazer jogadas, animações ou lógica de regras.
    public int col, row, preCol, preRow;
    //
    public int color;
    public Piece hittingP;
    public boolean moved, twoStepped;

    public Piece(int color, int col, int row) {

        // Parâmetros informa a cor, a coluna e a linha.
        // D efine esses valores e calcula a posição x e y em pixels com base na coluna e linha usando os métodos abaixo.
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }
    // Esse metodo recebe uma string com o caminho da imagem e retorna uma BufferedImage — que é a imagem da peça carregada na memória.
    public BufferedImage getImage(String imagePath) {

    //Cria uma variável local chamada image que inicialmente está vazia (null).
    //Essa variável vai receber a imagem carregada mais à frente.
        BufferedImage image = null;

        try {
            // ImageIO.read(...) tenta ler um arquivo de imagem.
            // getClass().getResourceAsStream pega o arquivo dentro do diretório de recurso como um fluxo de bytes
            var stream = getClass().getClassLoader().getResourceAsStream(imagePath);
            if (stream == null) {
                System.out.println("Imagem não encontrada: " + imagePath);
            } else {
                image = ImageIO.read(stream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    //Cada casa do tabuleiro tem um tamanho fixo (Board.SQUARE_SIZE = 100).
    //Se a peça está na coluna 2, x = 2 * 100 = 200.
    //Se está na linha 3, y = 3 * 100 = 300.
    //Esses valores são usados na hora de desenhar a peça na tela, já que o sistema gráfico usa coordenadas em pixels, não posições de tabuleiro.
    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }
    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }
    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }
    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }
    // O loop percorre a lista de peças GamePanel.simPieces e retorna o index(posicao) da peca atual
    public int getIndex() {
        for (int index = 0; index < GamePanel.simPieces.size(); index++) {
            if (GamePanel.simPieces.get(index) == this) {
            return index;
            }
        }
        return 0;
    }

    public void updatePosition() {

        if (type == Type.PAWN) {
            if (Math.abs(row - preRow) == 2) {
                twoStepped = true;
            }
        }

        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }
    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
    // Se a targetCol e targetRow for >= 0 ou <= 7 retorna true, é uma posiçao do tabuleiro
    public boolean isWithinBoard(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
            return true;
        }
        return false;
    }
    //Retorna true se estiver escolhendo a mesma posição que esta
    public boolean isSameSquare(int targetCol, int targetRow) {
        if (targetCol == preCol && targetRow == preRow) {
            return true;
        }
        return false;
    }

    public Piece getHittingP(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }
    public boolean isValidSquare(int targetCol, int targetRow) {

        hittingP = getHittingP(targetCol, targetRow);

        if (hittingP == null) { //Quadrado esta vazio
            return true;
        }
        else { //Quadrado ocupado
            if (hittingP.color != this.color) {
                return true;
            }
            else {
                hittingP = null;
            }
        }

        return false;
    }
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {

        // Percorre da direita para esquerda e verifica se tem uma peça no meio do caminho
        //Checa todas as colunas entre preCol
        for (int c = preCol-1; c > targetCol; c--) {
            //Percorre todas as peças do jogo.
            for (Piece piece : GamePanel.simPieces) {
                //Verifica se existe alguma peça exatamente na coluna atual
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // Quando a peça tiver movendo para direita
        for (int c = preCol+1; c < targetCol; c++) {
            //Percorre todas as peças do jogo.
            for (Piece piece : GamePanel.simPieces) {
                //Verifica se existe alguma peça exatamente na coluna atual
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        // Quando a peça tiver movendo para cima
        for (int r = preRow-1; r > targetRow; r--) {
            //Percorre todas as peças do jogo.
            for (Piece piece : GamePanel.simPieces) {
                //Verifica se existe alguma peça exatamente na coluna atual
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        // Quando a peça tiver movendo para baixo
        for (int r = preRow+1; r < targetRow; r++) {
            //Percorre todas as peças do jogo.
            for (Piece piece : GamePanel.simPieces) {
                //Verifica se existe alguma peça exatamente na coluna atual
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        return false;
    }
    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {

        if (targetRow < preRow) {
            // Diagonal subindo para esquerda
            //checa a partir de uma coluna à esquerda da posição atual
            for (int c = preCol-1 ; c > targetCol; c--) {
                //diff representa quantas colunas a posição c está distante da posição atual
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
            // Diagonal subindo para Direita
            for (int c = preCol+1 ; c < targetCol; c++) {
                //diff representa quantas colunas a posição c está distante da posição atual
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        if (targetRow > preRow) {
            // Diagonal descendo para esquerda
            for (int c = preCol-1; c > targetCol; c--) {
                //diff representa quantas colunas a posição c está distante da posição atual
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
            // Diagonal descendo para direita
            for (int c = preCol+1 ; c < targetCol; c++) {
                //diff representa quantas colunas a posição c está distante da posição atual
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }

        }
        return false;
    }

        public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}