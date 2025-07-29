package piece;

import com.chess.game.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece {

    // A imagem da peça (o ícone que será desenhado na tela).
    public BufferedImage image;
    // A posição em pixels da peça na tela (para desenho).
    public int x, y;
    // col, row = A posição da peça em colunas e linhas no tabuleiro (0 a 7).
    // preCol, preRow = Armazena a posição anterior da peça (antes de mover). Pode ser útil para desfazer jogadas, animações ou lógica de regras.
    public int col, row, preCol, preRow;
    //
    public int color;

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

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}