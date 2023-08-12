import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    Board initial;
    Board twin;
    MinPQ<Node> pq;
    MinPQ<Node> pqTwin;
    Solvable solvable;

    Node solutionNode;

    enum Solvable {
        Undecided,
        Yes,
        No
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException();

        solvable = Solvable.Undecided;

        this.initial = initial;
        this.twin = initial.twin();

        pq = new MinPQ<>();
        pq.insert(new Node(initial, null));

        pqTwin = new MinPQ<>();
        pqTwin.insert(new Node(twin, null));

        while (solvable == Solvable.Undecided) {
            var bestNode = pq.delMin();
            var board = bestNode.getBoard();
            if (board.isGoal()) {
                solutionNode = bestNode;
                solvable = Solvable.Yes;
            } else {
                for (var neighbor : board.neighbors()) {
                    pq.insert(new Node(neighbor, bestNode));
                }
            }

            // if (solvable == Solvable.Undecided) {
            //     var bestTwinNode = pqTwin.delMin();
            //     var twinBoard = bestTwinNode.board;
            //     if (twinBoard.isGoal()) {
            //         solutionNode = bestNode;
            //         solvable = Solvable.No;
            //     } else {
            //         for (var neighbor : twinBoard.neighbors()) {
            //             pqTwin.insert(new Node(neighbor, bestTwinNode));
            //         }
            //     }
            // }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable == Solvable.Yes;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (solvable == Solvable.No)
            return -1;

        var count = 0;
        var curr = solutionNode;

        while (curr.prev != null) {
            count++;
            curr = curr.prev;
        }

        return count;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (solvable == Solvable.No)
            return null;

        var stack = new Stack<Board>();
        var curr = solutionNode;

        stack.push(curr.board);

        while (curr.prev != null) {
            curr = curr.prev;
            stack.push(curr.board);
        }

        return stack;
    }

    class Node implements Comparable<Node> {
        private Board board;
        private Node prev;
        private int prio;

        Node(Board board, Node prev) {
            this.board = board;
            this.prev = prev;

            prio = board.hamming();
        }

        Node getPrev() {
            return prev;
        }

        Board getBoard() {
            return board;
        }

        int getPrio() {
            return prio;
        }

        @Override
        public int compareTo(Node other) {
            return prio - other.prio;
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}