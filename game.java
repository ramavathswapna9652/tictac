import java.util.*;
public class TicTacToe {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Tic-Tac-Toe ===");
        while (true) {
            System.out.println("\nChoose mode:");
            System.out.println("1) Human vs Human");
            System.out.println("2) Human vs Computer (AI)");
            System.out.println("3) Exit");
            System.out.print("Enter choice: ");
            int choice = readInt(sc, 1, 3);
            if (choice == 3) {
                System.out.println("Goodbye!");
                break;
            }
            if (choice == 1) {
                Game game = new Game(PlayerType.HUMAN, PlayerType.HUMAN, sc);
                game.play();
            } else {
                System.out.print("Play as X or O? (X plays first): ");
                char humanMark = readMark(sc);
                char aiMark = (humanMark == 'X') ? 'O' : 'X';
                PlayerType p1 = (humanMark == 'X') ? PlayerType.HUMAN : PlayerType.AI;
                PlayerType p2 = (humanMark == 'X') ? PlayerType.AI : PlayerType.HUMAN;
                Game game = new Game(p1, p2, sc, humanMark, aiMark);
                game.play();
            }
            System.out.println("\nPlay again? (y/n): ");
            String again = sc.next();
            if (!again.trim().toLowerCase().startsWith("y")) {
                System.out.println("Goodbye!");
                break;
            }
        }
        sc.close();
    }
    private static int readInt(Scanner sc, int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(sc.next());
                if (v < min || v > max) {
                    System.out.print("Enter a number between " + min + " and " + max + ": ");
                } else return v;
            } catch (NumberFormatException e) {
                System.out.print("Invalid. Enter a number: ");
            }
        }
    }
    private static char readMark(Scanner sc) {
        while (true) {
            String s = sc.next().trim().toUpperCase();
            if (s.length() > 0 && (s.charAt(0) == 'X' || s.charAt(0) == 'O')) {
                return s.charAt(0);
            }
            System.out.print("Please enter X or O: ");
        }
    }
}
enum PlayerType {
    HUMAN, AI
}
class Game {
    private Board board;
    private PlayerType p1Type, p2Type;
    private Scanner sc;
    private char humanMark;
    private char aiMark;
    public Game(PlayerType p1Type, PlayerType p2Type, Scanner sc) {
        this(p1Type, p2Type, sc, 'X', 'O');
    }
    public Game(PlayerType p1Type, PlayerType p2Type, Scanner sc, char humanMark, char aiMark) {
        this.board = new Board();
        this.p1Type = p1Type;
        this.p2Type = p2Type;
        this.sc = sc;
        this.humanMark = humanMark;
        this.aiMark = aiMark;
    }
    public void play() {
        board.clear();
        char current = 'X';
        board.print();
        while (true) {
            if (current == 'X') {
                System.out.println("\nTurn: X");
                if (p1Type == PlayerType.HUMAN) humanMove(current);
                else aiMove(current);
            } else {
                System.out.println("\nTurn: O");
                if (p2Type == PlayerType.HUMAN) humanMove(current);
                else aiMove(current);
            }
            board.print();

            if (board.isWin(current)) {
                System.out.println("\nPlayer " + current + " wins!");
                return;
            } else if (board.isFull()) {
                System.out.println("\nIt's a draw!");
                return;
            }
            current = (current == 'X') ? 'O' : 'X';
        }
    }
    private void humanMove(char mark) {
        while (true) {
            System.out.print("Enter row(1-3) and col(1-3): ");
            try {
                int r = Integer.parseInt(sc.next()) - 1;
                int c = Integer.parseInt(sc.next()) - 1;
                if (r < 0 || r > 2 || c < 0 || c > 2) {
                    System.out.println("Coordinates out of range.");
                    continue;
                }
                if (board.placeMove(r, c, mark)) break;
                else System.out.println("Cell already taken.");
            } catch (Exception ex) {
                System.out.println("Invalid input.");
                sc.nextLine();
            }
        }
    }

    private void aiMove(char mark) {
        System.out.println("AI thinking...");
        char human = (mark == 'X') ? 'O' : 'X';
        Move m = AI.bestMove(board, mark, human);
        if (m != null) {
            board.placeMove(m.row, m.col, mark);
            System.out.println("AI placed at row " + (m.row + 1) + " col " + (m.col + 1));
        }
    }
}
class Board {
    private char[][] b;

    public Board() {
        b = new char[3][3];
        clear();
    }

    public void clear() {
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                b[i][j] = ' ';
    }

    public boolean placeMove(int r, int c, char mark) {
        if (b[r][c] == ' ') {
            b[r][c] = mark;
            return true;
        }
        return false;
    }

    public void undoMove(int r, int c) {
        b[r][c] = ' ';
    }

    public boolean isWin(char mark) {
        for (int i = 0; i < 3; ++i)
            if (b[i][0] == mark && b[i][1] == mark && b[i][2] == mark) return true;
        for (int j = 0; j < 3; ++j)
            if (b[0][j] == mark && b[1][j] == mark && b[2][j] == mark) return true;
        if (b[0][0] == mark && b[1][1] == mark && b[2][2] == mark) return true;
        if (b[0][2] == mark && b[1][1] == mark && b[2][0] == mark) return true;
        return false;
    }

    public boolean isFull() {
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                if (b[i][j] == ' ') return false;
        return true;
    }

    public List<int[]> availableMoves() {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                if (b[i][j] == ' ')
                    moves.add(new int[]{i, j});
        return moves;
    }

    public void print() {
        System.out.println();
        for (int i = 0; i < 3; i++) {
            System.out.println("*___*___*___*");
            System.out.print("|");
            for (int j = 0; j < 3; j++) {
                System.out.print(" " + (b[i][j] == ' ' ? " " : b[i][j]) + " |");
            }
            System.out.println();
        }
        System.out.println("*___*___*___*");
    }
}
class Move {
    int row, col, score;
    Move(int r, int c) { row = r; col = c; }
    Move(int r, int c, int s) { row = r; col = c; score = s; }
}
class AI {

    public static Move bestMove(Board board, char aiMark, char humanMark) {
        if (board.availableMoves().size() == 9) {
            return new Move(1, 1); // take center
        }
        return minimaxDecision(board, aiMark, humanMark);
    }

    private static Move minimaxDecision(Board board, char aiMark, char humanMark) {
        int bestScore = Integer.MIN_VALUE;
        Move best = null;
        for (int[] mv : board.availableMoves()) {
            int r = mv[0], c = mv[1];
            board.placeMove(r, c, aiMark);
            int score = minimax(board, 0, false, aiMark, humanMark);
            board.undoMove(r, c);
            if (score > bestScore) {
                bestScore = score;
                best = new Move(r, c, score);
            }
        }
        return best;
    }

    private static int minimax(Board board, int depth, boolean isMax, char aiMark, char humanMark) {
        if (board.isWin(aiMark)) return 10 - depth;
        if (board.isWin(humanMark)) return depth - 10;
        if (board.isFull()) return 0;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int[] mv : board.availableMoves()) {
                board.placeMove(mv[0], mv[1], aiMark);
                int score = minimax(board, depth + 1, false, aiMark, humanMark);
                board.undoMove(mv[0], mv[1]);
                best = Math.max(best, score);
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] mv : board.availableMoves()) {
                board.placeMove(mv[0], mv[1], humanMark);
                int score = minimax(board, depth + 1, true, aiMark, humanMark);
                board.undoMove(mv[0], mv[1]);
                best = Math.min(best, score);
            }
            return best;
        }
    }
}