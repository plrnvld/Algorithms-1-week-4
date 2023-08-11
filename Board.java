import java.lang.Math;
import java.util.LinkedList;

public class Board {
    private int[][] tiles;
    private int hammingCalculated;
    private int manhattanCalculated;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = tiles;

        hammingCalculated = -1;
        manhattanCalculated = -1;
    }

    public Board(int[][] tiles, int hamming, int manhattan) {
        this.tiles = tiles;

        hammingCalculated = hamming;
        manhattanCalculated = manhattan;
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


        return neigbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        return null;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        var tiles = new int[][] {
                { 1, 2, 3, 4, 5 },
                { 6, 7, 8, 9, 10 },
                { 11, 12, 13, 14, 15 },
                { 16, 17, 18, 0, 19 },
                { 21, 22, 23, 24, 20 },
        };

        var board = new Board(tiles);

        System.out.println(board.toString());
        System.out.println("Hamming: " + board.hamming());
        System.out.println("Manhattan: " + board.manhattan());
    }
}