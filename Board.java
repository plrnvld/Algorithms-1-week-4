import java.lang.Math;
import java.util.LinkedList;

public class Board {
    private int[][] tiles;
    private int hammingCalculated;
    private int manhattanCalculated;
    private int zeroRowStored;
    private int zeroColStored;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = tiles;

        hammingCalculated = -1;
        manhattanCalculated = -1;
        zeroRowStored = -1;
        zeroColStored = -1;
    }

    // string representation of this board
    public String toString() {
        var dim = dimension();
        var prefix = new String(new char[numDigits(dim)]).replace('\0', ' ');
        var maxDigits = numDigits(dim * dim);
        var numFormat = "%1$" + maxDigits + "s ";
        var builder = new StringBuilder(dim + "\n");

        for (var y = 0; y < dim; y++) {
            builder.append(prefix);
            for (var x = 0; x < dim; x++) {
                var num = tiles[y][x];
                var formattedNum = String.format(numFormat, String.valueOf(num));
                builder.append(formattedNum);
            }

            builder.append("\n");
        }

        return builder.toString();
    }

    private int numDigits(int number) {
        return (int) Math.round(Math.ceil(Math.log10(number)));
    }

    // board dimension n
    public int dimension() {
        return tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        if (hammingCalculated >= 0)
            return hammingCalculated;

        var dim = dimension();
        var sum = 0;
        for (var y = 0; y < dim; y++)
            for (var x = 0; x < dim; x++)
                sum += hammingFor(y, x);

        hammingCalculated = sum;
        return hammingCalculated;
    }

    private int hammingFor(int row, int col) {
        var dim = dimension();
        var num = tiles[row][col];
        var goal = row * dim + col + 1;
        return num == 0 || num == goal ? 0 : 1;
    }

    private boolean canSlideLeft() {
        return zeroCol() > 0;
    }

    private boolean canSlideRight() {
        return zeroCol() < dimension() - 1;
    }

    private boolean canSlideUp() {
        return zeroRow() > 0;
    }

    private boolean canSlideDown() {
        return zeroRow() < dimension() - 1;
    }

    private int zeroRow() {
        if (zeroRowStored >= 0)
            return zeroRowStored;

        var dim = dimension();
        for (var y = 0; y < dim; y++)
            for (var x = 0; x < dim; x++) {
                if (tiles[y][x] == 0) {
                    zeroRowStored = y;
                    zeroColStored = x;
                }
            }

        return zeroRowStored;
    }

    private int zeroCol() {
        if (zeroColStored >= 0) {
            return zeroColStored;
        }

        var dim = dimension();
        for (var y = 0; y < dim; y++)
            for (var x = 0; x < dim; x++) {
                if (tiles[y][x] == 0) {
                    zeroRowStored = y;
                    zeroColStored = x;
                }
            }

        return zeroColStored;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        if (manhattanCalculated >= 0)
            return manhattanCalculated;

        var dim = dimension();
        var sum = 0;
        for (var y = 0; y < dim; y++)
            for (var x = 0; x < dim; x++)
                sum += manhattanFor(y, x);

        manhattanCalculated = sum;
        return manhattanCalculated;
    }

    private int manhattanFor(int row, int col) {
        var dim = dimension();
        var num = tiles[row][col];

        if (num == 0)
            return 0;

        var y = row;
        var x = col;

        var yGoal = (num - 1) / dim;
        var xGoal = (num - 1) % dim;

        return Math.abs(xGoal - x) + Math.abs(yGoal - y);
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (getClass() != other.getClass())
            return false;

        var dim = dimension();
        var otherBoard = (Board) other;
        if (dim != otherBoard.dimension())
            return false;

        for (var y = 0; y < dim; y++) {
            for (var x = 0; x < dim; x++) {
                if (tiles[y][x] != otherBoard.tiles[y][x])
                    return false;
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        var neigbors = new LinkedList<Board>();

        var zeroCol = zeroCol();
        var zeroRow = zeroRow();

        if (canSlideLeft()) {
            neigbors.add(exch(zeroRow, zeroCol, zeroRow, zeroCol - 1));
        }

        if (canSlideRight()) {
            neigbors.add(exch(zeroRow, zeroCol, zeroRow, zeroCol + 1));
        }

        if (canSlideUp()) {
            neigbors.add(exch(zeroRow, zeroCol, zeroRow - 1, zeroCol));
        }

        if (canSlideDown()) {
            neigbors.add(exch(zeroRow, zeroCol, zeroRow + 1, zeroCol));
        }

        return neigbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        var dim = dimension();

        if (dim < 2)
            throw new UnsupportedOperationException();

        return exch(0, 0, 0, 1);
    }

    private Board exch(int y1, int x1, int y2, int x2) {
        var dim = dimension();

        var newTiles = new int[dim][dim];

        for (var y = 0; y < dim; y++) {
            for (var x = 0; x < dim; x++) {
                newTiles[y][x] = tiles[y][x];
            }
        }

        var num1 = tiles[y1][x1];
        var num2 = tiles[y2][x2];
        ;
        newTiles[y2][x2] = num1;
        newTiles[y1][x1] = num2;

        var unchangedHamming = hamming() - hammingFor(y1, x1) - hammingFor(y2, x2);
        var unchangedManhattan = manhattan() - manhattanFor(y1, x1) - manhattanFor(y2, x2);

        var newBoard = new Board(newTiles);

        if (num1 == 0) {
            newBoard.zeroRowStored = y2;
            newBoard.zeroColStored = x2;
        } else if (num2 == 0) {
            newBoard.zeroRowStored = y1;
            newBoard.zeroColStored = x1;
        }

        newBoard.hammingCalculated = unchangedHamming + newBoard.hammingFor(y1, x1) + newBoard.hammingFor(y2, x2);
        newBoard.manhattanCalculated = unchangedManhattan + newBoard.manhattanFor(y1, x1)
                + newBoard.manhattanFor(y2, x2);

        return newBoard;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        var tiles = new int[][] {
                { 1, 2, 3, 4, 5 },
                { 6, 7, 8, 9, 10 },
                { 11, 12, 13, 14, 15 },
                { 16, 17, 18, 19, 0 },
                { 21, 22, 23, 24, 20 },
        };

        var board = new Board(tiles);

        System.out.println(board.toString());
        System.out.println("Hamming: " + board.hamming());
        System.out.println("Manhattan: " + board.manhattan());

        var neigbors = board.neighbors();

        for (var neighbor : neigbors) {
            System.out.println(neighbor.toString());
        }
    }
}